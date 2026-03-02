# 📊 HolaRent Database Sample Data

## 🚀 Quick Start

### Cách 1: Sử dụng PowerShell Script (Khuyến nghị)

```powershell
cd E:\Spring2026\Exe201
.\import_database.ps1
```

Script sẽ tự động:
- Xóa dữ liệu cũ
- Import dữ liệu mới
- Kiểm tra kết quả

### Cách 2: Import Thủ Công

```bash
# Trong MySQL
mysql -u root -p stayease_db < EXE201c_Backend/database/sample_data.sql
```

## 🔑 Thông Tin Đăng Nhập

**TẤT CẢ PASSWORD: `password123`**

| Loại | Email | Role |
|------|-------|------|
| 👑 Admin | admin@holarent.com | ADMIN |
| 🏠 Host | host@holarent.com | HOST |
| 👤 User | user@holarent.com | USER |
| 👥 Guest | guest1@holarent.com | USER |

## 📦 Dữ Liệu Có Sẵn

- **10 Users** (1 admin, 3 hosts, 6 guests)
- **24 Properties** 
  - 19-20 LONG_TERM (phòng trọ - CHÍNH)
  - 4-5 SHORT_TERM (apartment ngắn hạn - PHỤ)
- **10 Bookings** (5 long-term, 5 short-term)
- **8 Reviews**
- **39 Property Images**
- **10 Categories**
- **20 Amenities**

## 📖 Chi Tiết

Xem file: [DATABASE_SETUP_GUIDE.md](../../DATABASE_SETUP_GUIDE.md)

## ✅ Test API

### LONG_TERM (Chính)
```bash
GET http://localhost:8080/api/properties/long-term
```

### SHORT_TERM (Phụ)
```bash
GET http://localhost:8080/api/properties/short-term
```

## 🎯 Mục Tiêu Thiết Kế

Database được thiết kế để:
1. ✅ Tập trung vào **phòng trọ dài hạn** (LONG_TERM) - chức năng CHÍNH
2. ✅ Apartment ngắn hạn (SHORT_TERM) chỉ là chức năng PHỤ
3. ✅ Tách biệt rõ ràng giữa 2 loại properties
4. ✅ Dữ liệu phong phú để test đầy đủ tính năng

