package com.stayease.service;

import com.stayease.dto.request.CreateCommissionRuleRequest;
import com.stayease.enums.CommissionType;
import com.stayease.mapper.CommissionRuleMapper;
import com.stayease.model.CommissionRule;
import com.stayease.repository.CommissionRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommissionRuleService {

    private final CommissionRuleRepository commissionRuleRepository;

    public CommissionRule getActiveRule(LocalDateTime atTime) {
        LocalDateTime at = atTime != null ? atTime : LocalDateTime.now();
        return commissionRuleRepository.findActiveAt(at).orElse(null);
    }

    public List<CommissionRule> listRules() {
        return commissionRuleRepository.findAllByOrderByEffectiveFromDesc();
    }

    @Transactional
    public CommissionRule createRule(CreateCommissionRuleRequest request) {
        validate(request);

        // Strict: prevent overlap with existing rules (by effective time window)
        ensureNoOverlap(request.getEffectiveFrom(), request.getEffectiveTo());

        Integer nextVersion = commissionRuleRepository.findAllByOrderByEffectiveFromDesc().stream()
                .map(CommissionRule::getVersion)
                .filter(v -> v != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;
        String createdBy = getCurrentUsername();

        // If rule is already effective now, deactivate current active and keep only this active
        LocalDateTime now = LocalDateTime.now();
        boolean effectiveNow = !request.getEffectiveFrom().isAfter(now)
                && (request.getEffectiveTo() == null || request.getEffectiveTo().isAfter(now));
        if (effectiveNow) {
            CommissionRule active = getActiveRule(now);
            if (active != null) {
                active.setIsActive(false);
                commissionRuleRepository.save(active);
            }
        }

        CommissionRule entity = CommissionRule.builder()
                .type(request.getCommissionType())
                .percent(request.getCommissionPercent())
                .fixedAmount(request.getCommissionFixedVnd())
                .appliesTo(request.getCommissionAppliesTo())
                .roundingMode(request.getRoundingMode())
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTo(request.getEffectiveTo())
                .isActive(effectiveNow)
                .version(nextVersion)
                .createdBy(createdBy)
                .build();

        CommissionRule saved = commissionRuleRepository.save(entity);
        log.info("Created commission rule id={} version={} type={}", saved.getId(), saved.getVersion(), saved.getType());
        return saved;
    }

    @Transactional
    public CommissionRule activate(Long id) {
        CommissionRule rule = commissionRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commission rule not found"));

        LocalDateTime now = LocalDateTime.now();
        if (rule.getEffectiveFrom() != null && rule.getEffectiveFrom().isAfter(now)) {
            throw new IllegalArgumentException("Cannot activate a rule that is not yet effective");
        }
        if (rule.getEffectiveTo() != null && !rule.getEffectiveTo().isAfter(now)) {
            throw new IllegalArgumentException("Cannot activate an expired rule");
        }

        CommissionRule activeNow = getActiveRule(now);
        if (activeNow != null && !activeNow.getId().equals(rule.getId())) {
            activeNow.setIsActive(false);
            commissionRuleRepository.save(activeNow);
        }

        rule.setIsActive(true);
        CommissionRule saved = commissionRuleRepository.save(rule);
        log.info("Activated commission rule id={}", saved.getId());
        return saved;
    }

    private void validate(CreateCommissionRuleRequest request) {
        if (request.getEffectiveTo() != null && request.getEffectiveFrom() != null
                && !request.getEffectiveTo().isAfter(request.getEffectiveFrom())) {
            throw new IllegalArgumentException("effectiveTo must be after effectiveFrom");
        }

        CommissionType type = request.getCommissionType();
        BigDecimal percent = request.getCommissionPercent();
        BigDecimal fixed = request.getCommissionFixedVnd();

        if (type == CommissionType.PERCENT || type == CommissionType.PERCENT_PLUS_FIXED) {
            if (percent == null) throw new IllegalArgumentException("commissionPercent is required");
        }
        if (type == CommissionType.FIXED || type == CommissionType.PERCENT_PLUS_FIXED) {
            if (fixed == null) throw new IllegalArgumentException("commissionFixedVnd is required");
        }

        if (percent != null && (percent.compareTo(BigDecimal.ZERO) < 0 || percent.compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("commissionPercent must be between 0 and 100");
        }
        if (fixed != null && fixed.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("commissionFixedVnd must be >= 0");
        }
    }

    private void ensureNoOverlap(LocalDateTime from, LocalDateTime to) {
        if (from == null) return;

        // Two intervals overlap if: existing.from < new.to AND new.from < existing.to
        // (treat null end as +infinity)
        LocalDateTime newEnd = to;

        for (CommissionRule existing : commissionRuleRepository.findAll()) {
            if (existing.getIsDeleted() != null && existing.getIsDeleted()) continue;
            if (existing.getEffectiveFrom() == null) continue;

            LocalDateTime existingStart = existing.getEffectiveFrom();
            LocalDateTime existingEnd = existing.getEffectiveTo();

            boolean overlap;
            if (newEnd == null && existingEnd == null) {
                overlap = true;
            } else if (newEnd == null) {
                overlap = existingEnd == null || existingEnd.isAfter(from);
            } else if (existingEnd == null) {
                overlap = newEnd.isAfter(existingStart);
            } else {
                overlap = existingStart.isBefore(newEnd) && from.isBefore(existingEnd);
            }

            if (overlap) {
                throw new IllegalArgumentException("Commission rule effective window overlaps with an existing rule");
            }
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return auth.getName();
    }
}

