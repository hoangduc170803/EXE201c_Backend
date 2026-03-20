package com.stayease.service;

import com.stayease.dto.response.BankStatementImportResponse;
import com.stayease.dto.response.BankStatementLineDto;
import com.stayease.dto.response.BankStatementSessionResponse;
import com.stayease.enums.BankStatementMatchStatus;
import com.stayease.enums.PaymentStatus;
import com.stayease.exception.BadRequestException;
import com.stayease.model.Booking;
import com.stayease.model.BankStatementImportLine;
import com.stayease.model.BankStatementImportSession;
import com.stayease.repository.BookingRepository;
import com.stayease.repository.BankStatementImportLineRepository;
import com.stayease.repository.BankStatementImportSessionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankStatementReconciliationService {

    private final BookingRepository bookingRepository;
    private final CommissionRuleService commissionRuleService;
    private final CommissionCalculationService commissionCalculationService;
    private final BookingSettlementService bookingSettlementService;
    private final BankStatementImportSessionRepository sessionRepository;
    private final BankStatementImportLineRepository lineRepository;

    // Booking codes are generated like: BK + 8 chars
    private static final Pattern BOOKING_CODE_PATTERN = Pattern.compile("\\b(BK[A-Z0-9]{8})\\b");

    // Common Vietnamese date formats in bank exports
    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    /**
     * Parse Vietcombank statement Excel (.xlsx), persist a reconciliation session, and return preview lines.
     */
    @Transactional
    public BankStatementImportResponse importVietcombankExcel(
            MultipartFile file,
            int previewLimit,
            Long adminUserId,
            String adminEmail
    ) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File sao kê không hợp lệ");
        }

        String filename = file.getOriginalFilename() == null ? "statement.xlsx" : file.getOriginalFilename();

        BankStatementImportSession session = sessionRepository.save(
                BankStatementImportSession.builder()
                        .bank("VIETCOMBANK")
                        .filename(filename)
                        .uploadedByUserId(adminUserId)
                        .uploadedByEmail(adminEmail)
                        .totalRows(0)
                        .parsedLines(0)
                        .matchedLines(0)
                        .confirmedLines(0)
                        .note("")
                        .build()
        );

        List<BankStatementImportLine> toPersist = new ArrayList<>();

        try (InputStream in = file.getInputStream(); Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new BadRequestException("Không tìm thấy sheet trong file Excel");
            }

            int headerRowIndex = findHeaderRow(sheet);
            if (headerRowIndex < 0) {
                throw new BadRequestException("Không tìm thấy dòng header trong file sao kê. Hãy export đúng định dạng Vietcombank.");
            }

            Row headerRow = sheet.getRow(headerRowIndex);
            ColumnIndexMap cols = mapColumns(headerRow);

            int lastRowNum = sheet.getLastRowNum();
            int matchedCount = 0;

            for (int r = headerRowIndex + 1; r <= lastRowNum; r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                BankStatementLineDto line = parseRow(row, cols, r + 1);
                if (line == null) continue;

                // Attempt match booking by extracted booking code + amountIn
                if (line.getExtractedBookingCode() != null && line.getAmountIn() != null) {
                    Optional<Booking> bookingOpt = bookingRepository.findByBookingCode(line.getExtractedBookingCode());
                    if (bookingOpt.isPresent()) {
                        Booking booking = bookingOpt.get();
                        BigDecimal total = booking.getTotalPrice() == null ? BigDecimal.ZERO : booking.getTotalPrice();

                        if (total.compareTo(BigDecimal.ZERO) > 0 && line.getAmountIn().compareTo(total) == 0) {
                            line.setMatched(true);
                            line.setMatchedBookingId(booking.getId());
                            line.setMatchedBookingCode(booking.getBookingCode());
                            matchedCount++;
                        } else {
                            line.setNote("Tìm thấy bookingCode nhưng số tiền không khớp totalPrice");
                        }
                    } else {
                        line.setNote("Không tìm thấy booking theo bookingCode trong nội dung chuyển khoản");
                    }
                }

                BankStatementImportLine entity = BankStatementImportLine.builder()
                        .session(session)
                        .rowNumber(line.getRowNumber())
                        .transactionDate(line.getTransactionDate())
                        .description(line.getDescription())
                        .amountIn(line.getAmountIn())
                        .amountOut(line.getAmountOut())
                        .extractedBookingCode(line.getExtractedBookingCode())
                        .matchedBookingId(line.getMatchedBookingId())
                        .matchedBookingCode(line.getMatchedBookingCode())
                        .amountMatched(line.isMatched())
                        .bankReference(line.getDescription())
                        .status(line.isMatched() ? BankStatementMatchStatus.AUTO_MATCHED : BankStatementMatchStatus.UNMATCHED)
                        .note(line.getNote())
                        .build();
                toPersist.add(entity);
            }

            List<BankStatementImportLine> savedLines = toPersist.isEmpty() ? List.of() : lineRepository.saveAll(toPersist);

            session.setTotalRows(lastRowNum + 1);
            session.setParsedLines(savedLines.size());
            session.setMatchedLines(matchedCount);
            session.setConfirmedLines(0);
            session.setNote("Upload bởi admin, cần confirm để ghi nhận thanh toán");
            sessionRepository.save(session);

            List<BankStatementLineDto> preview = savedLines.stream()
                    .sorted(Comparator.comparing(BankStatementImportLine::getRowNumber))
                    .limit(previewLimit)
                    .map(this::toLineDto)
                    .collect(Collectors.toList());

            return BankStatementImportResponse.builder()
                    .sessionId(session.getId())
                    .bank("VIETCOMBANK")
                    .filename(filename)
                    .totalRows(lastRowNum + 1)
                    .parsedLines(savedLines.size())
                    .matchedLines(matchedCount)
                    .confirmedLines(0)
                    .lines(preview)
                    .note("Đã lưu session, cần confirm từng dòng để đánh dấu booking đã thanh toán")
                    .build();

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Không thể đọc file Excel: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<BankStatementSessionResponse> listSessions(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        return sessionRepository.findAll(pageable).map(this::toSessionDto);
    }

    @Transactional(readOnly = true)
    public BankStatementImportResponse getSession(Long sessionId) {
        BankStatementImportSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy session import"));

        List<BankStatementLineDto> lines = lineRepository.findBySessionIdOrderByRowNumberAsc(sessionId)
                .stream()
                .map(this::toLineDto)
                .collect(Collectors.toList());

        return BankStatementImportResponse.builder()
                .sessionId(session.getId())
                .bank(session.getBank())
                .filename(session.getFilename())
                .totalRows(Optional.ofNullable(session.getTotalRows()).orElse(0))
                .parsedLines(Optional.ofNullable(session.getParsedLines()).orElse(0))
                .matchedLines(Optional.ofNullable(session.getMatchedLines()).orElse(0))
                .confirmedLines(Optional.ofNullable(session.getConfirmedLines()).orElse(0))
                .lines(lines)
                .note(session.getNote())
                .build();
    }

    @Transactional
    public BankStatementLineDto confirmLine(
            Long sessionId,
            Long lineId,
            Long bookingId,
            String bookingCode,
            String bankReference,
            String note,
            Long adminUserId
    ) {
        BankStatementImportLine line = lineRepository.findById(lineId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy dòng sao kê"));

        if (!line.getSession().getId().equals(sessionId)) {
            throw new BadRequestException("Line không thuộc session đã chọn");
        }
        if (line.getStatus() == BankStatementMatchStatus.CONFIRMED) {
            throw new BadRequestException("Dòng này đã được confirm trước đó");
        }

        Booking booking = resolveBooking(bookingId, bookingCode);
        String reference = (bankReference != null && !bankReference.isBlank())
                ? bankReference.trim()
                : Optional.ofNullable(line.getBankReference()).orElse(line.getDescription());

        confirmMatchedPayment(booking.getId(), reference);

        line.setMatchedBookingId(booking.getId());
        line.setMatchedBookingCode(booking.getBookingCode());
        line.setAmountMatched(true);
        line.setBankReference(reference);
        line.setStatus(BankStatementMatchStatus.CONFIRMED);
        line.setConfirmedByUserId(adminUserId);
        line.setConfirmedAt(LocalDateTime.now());
        if (note != null && !note.isBlank()) {
            line.setNote(note);
        }

        lineRepository.save(line);

        BankStatementImportSession session = line.getSession();
        int confirmedCount = Optional.ofNullable(session.getConfirmedLines()).orElse(0);
        session.setConfirmedLines(confirmedCount + 1);
        sessionRepository.save(session);

        return toLineDto(line);
    }

    @Transactional
    public BankStatementLineDto rejectLine(Long sessionId, Long lineId, String note, Long adminUserId) {
        BankStatementImportLine line = lineRepository.findById(lineId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy dòng sao kê"));

        if (!line.getSession().getId().equals(sessionId)) {
            throw new BadRequestException("Line không thuộc session đã chọn");
        }
        if (line.getStatus() == BankStatementMatchStatus.CONFIRMED) {
            throw new BadRequestException("Không thể reject dòng đã confirm");
        }

        line.setStatus(BankStatementMatchStatus.REJECTED);
        line.setConfirmedByUserId(adminUserId);
        line.setConfirmedAt(LocalDateTime.now());
        line.setNote(note);
        lineRepository.save(line);

        return toLineDto(line);
    }

    @Transactional
    public void confirmMatchedPayment(Long bookingId, String bankReference) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Booking không tồn tại"));

        LocalDateTime confirmationTime = LocalDateTime.now();

        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            bookingSettlementService.createOrRecalculateForBooking(booking.getId(), confirmationTime);
            return; // idempotent
        }

        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setPaymentMethod("VIETQR_VCB");
        if (bankReference != null && !bankReference.isBlank()) {
            booking.setTransactionId(bankReference);
        }

        // Snapshot commission rule + calculated amounts at confirmation time
        var rule = commissionRuleService.getActiveRule(confirmationTime);
        if (rule != null) {
            var calc = commissionCalculationService.calculate(booking, rule);
            booking.setCommissionRuleId(rule.getId());
            booking.setCommissionType(rule.getType());
            booking.setCommissionPercent(rule.getPercent());
            booking.setCommissionFixedVnd(rule.getFixedAmount());
            booking.setCommissionAppliesTo(rule.getAppliesTo());
            booking.setCommissionRoundingMode(rule.getRoundingMode());
            booking.setCommissionAmountVnd(calc.getCommissionAmount());
            booking.setHostPayoutAmountVnd(calc.getPayoutAmount());
        }

        bookingRepository.save(booking);
        bookingSettlementService.createOrRecalculateForBooking(booking.getId(), confirmationTime);
    }

    private Booking resolveBooking(Long bookingId, String bookingCode) {
        if (bookingId != null) {
            return bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy booking Id=" + bookingId));
        }
        if (bookingCode != null && !bookingCode.isBlank()) {
            return bookingRepository.findByBookingCode(bookingCode.trim().toUpperCase(Locale.ROOT))
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy bookingCode " + bookingCode));
        }
        throw new BadRequestException("Cần cung cấp bookingId hoặc bookingCode");
    }

    private int findHeaderRow(Sheet sheet) {
        int scan = Math.min(sheet.getLastRowNum(), 50);
        for (int i = 0; i <= scan; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String rowText = joinRow(row).toLowerCase(Locale.ROOT);
            // Heuristics for VCB export header
            if (rowText.contains("ngày") && (rowText.contains("diễn") || rowText.contains("mô tả") || rowText.contains("nội dung"))
                    && (rowText.contains("ghi có") || rowText.contains("ghi no") || rowText.contains("ghi nợ") || rowText.contains("phát sinh"))) {
                return i;
            }
        }
        return -1;
    }

    private String joinRow(Row row) {
        StringBuilder sb = new StringBuilder();
        for (Cell cell : row) {
            sb.append(getCellString(cell)).append(' ');
        }
        return sb.toString().trim();
    }

    private ColumnIndexMap mapColumns(Row headerRow) {
        ColumnIndexMap cols = new ColumnIndexMap();
        for (Cell cell : headerRow) {
            String v = getCellString(cell).toLowerCase(Locale.ROOT).trim();
            int idx = cell.getColumnIndex();

            if (cols.dateCol < 0 && (v.contains("ngày") || v.contains("date"))) cols.dateCol = idx;
            if (cols.descCol < 0 && (v.contains("diễn") || v.contains("mô tả") || v.contains("nội dung") || v.contains("description"))) cols.descCol = idx;

            // amount columns
            if (cols.inCol < 0 && (v.contains("ghi có") || v.contains("credit") || v.contains("thu"))) cols.inCol = idx;
            if (cols.outCol < 0 && (v.contains("ghi nợ") || v.contains("debit") || v.contains("chi"))) cols.outCol = idx;

            // some exports use "Phát sinh" with +/-; not supported in MVP
        }

        // Fallbacks (best effort)
        if (cols.dateCol < 0) cols.dateCol = 0;
        if (cols.descCol < 0) cols.descCol = 1;
        if (cols.inCol < 0) cols.inCol = 3;
        if (cols.outCol < 0) cols.outCol = 2;

        return cols;
    }

    private BankStatementLineDto parseRow(Row row, ColumnIndexMap cols, int rowNumber) {
        LocalDate date = parseDate(getCellString(getCell(row, cols.dateCol)));
        String desc = getCellString(getCell(row, cols.descCol));

        BigDecimal amountIn = parseMoney(getCell(row, cols.inCol));
        BigDecimal amountOut = parseMoney(getCell(row, cols.outCol));

        // Skip empty rows
        if ((desc == null || desc.isBlank()) && date == null && amountIn == null && amountOut == null) {
            return null;
        }

        String extracted = extractBookingCode(desc);

        return BankStatementLineDto.builder()
                .rowNumber(rowNumber)
                .transactionDate(date)
                .description(desc)
                .amountIn(amountIn)
                .amountOut(amountOut)
                .extractedBookingCode(extracted)
                .matched(false)
                .build();
    }

    private Cell getCell(Row row, int colIndex) {
        if (colIndex < 0) return null;
        return row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
    }

    private String extractBookingCode(String text) {
        if (text == null) return null;
        Matcher m = BOOKING_CODE_PATTERN.matcher(text.toUpperCase(Locale.ROOT));
        return m.find() ? m.group(1) : null;
    }

    private LocalDate parseDate(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isBlank()) return null;

        // Try parse as excel date numeric (already converted by POI to string sometimes)
        // In MVP: only string date patterns.
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(s, fmt);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private BigDecimal parseMoney(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                double v = cell.getNumericCellValue();
                if (Double.isNaN(v) || Double.isInfinite(v) || v == 0d) return null;
                return BigDecimal.valueOf(v).setScale(0, RoundingMode.HALF_UP);
            }
            String s = getCellString(cell);
            if (s == null) return null;
            s = s.trim();
            if (s.isBlank()) return null;

            // remove thousand separators, currency symbols
            s = s.replace(".", "").replace(",", "");
            s = s.replaceAll("[^0-9-]", "");
            if (s.isBlank() || s.equals("-")) return null;
            BigDecimal bd = new BigDecimal(s);
            if (bd.compareTo(BigDecimal.ZERO) == 0) return null;
            return bd.abs();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getCellString(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                double v = cell.getNumericCellValue();
                // Avoid scientific format
                return BigDecimal.valueOf(v).stripTrailingZeros().toPlainString();
            }
            if (cell.getCellType() == CellType.BOOLEAN) return String.valueOf(cell.getBooleanCellValue());
            if (cell.getCellType() == CellType.FORMULA) {
                // Evaluate formula as string/number
                try {
                    return cell.getStringCellValue();
                } catch (Exception ignored) {
                    try {
                        return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
                    } catch (Exception ignored2) {
                        return null;
                    }
                }
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private BankStatementLineDto toLineDto(BankStatementImportLine line) {
        return BankStatementLineDto.builder()
                .lineId(line.getId())
                .rowNumber(Optional.ofNullable(line.getRowNumber()).orElse(0))
                .transactionDate(line.getTransactionDate())
                .description(line.getDescription())
                .amountIn(line.getAmountIn())
                .amountOut(line.getAmountOut())
                .extractedBookingCode(line.getExtractedBookingCode())
                .matched(line.getMatchedBookingId() != null)
                .matchedBookingId(line.getMatchedBookingId())
                .matchedBookingCode(line.getMatchedBookingCode())
                .status(line.getStatus())
                .confirmed(line.getStatus() == BankStatementMatchStatus.CONFIRMED)
                .note(line.getNote())
                .build();
    }

    private BankStatementSessionResponse toSessionDto(BankStatementImportSession session) {
        return BankStatementSessionResponse.builder()
                .id(session.getId())
                .bank(session.getBank())
                .filename(session.getFilename())
                .totalRows(session.getTotalRows())
                .parsedLines(session.getParsedLines())
                .matchedLines(session.getMatchedLines())
                .confirmedLines(session.getConfirmedLines())
                .uploadedByUserId(session.getUploadedByUserId())
                .uploadedByEmail(session.getUploadedByEmail())
                .createdAt(session.getCreatedAt())
                .build();
    }

    private static class ColumnIndexMap {
        int dateCol = -1;
        int descCol = -1;
        int inCol = -1;
        int outCol = -1;
    }
}







