# StayEase Backend

Backend API cho website bán/cho thuê nhà ở - StayEase

## 📋 Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** (Hibernate)
- **Spring Security** + JWT
- **MySQL** (Production) / H2 (Development)
- **Lombok**
- **OpenAPI/Swagger**

## 🏗️ Project Structure

```
src/main/java/com/stayease/
├── config/                    # Cấu hình project
│   ├── SecurityConfig.java   # Security & JWT config
│   ├── JwtTokenProvider.java # JWT utilities
│   └── DataInitializer.java  # Seed initial data
│
├── controller/                # Nhận requests từ client
│   ├── filter/               # Kiểm tra auth, validate
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtAuthenticationEntryPoint.java
│   ├── request/              # Request DTOs từ client
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── CreatePropertyRequest.java
│   │   ├── CreateBookingRequest.java
│   │   └── ...
│   ├── response/             # Response DTOs trả về client
│   │   ├── ApiResponse.java
│   │   ├── UserResponse.java
│   │   ├── PropertyResponse.java
│   │   ├── BookingResponse.java
│   │   └── ...
│   ├── AuthController.java
│   ├── UserController.java
│   ├── PropertyController.java
│   ├── BookingController.java
│   ├── CategoryController.java
│   └── AmenityController.java
│
├── exception/                 # Custom exceptions
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── ...
│
├── model/                     # Entity classes (DB models)
│   ├── User.java
│   ├── Role.java
│   ├── Property.java
│   ├── PropertyImage.java
│   ├── Category.java
│   ├── Amenity.java
│   ├── Booking.java
│   ├── Review.java
│   ├── Favorite.java
│   ├── Conversation.java
│   └── Message.java
│
├── repository/                # Data access layer
│   ├── UserRepository.java
│   ├── PropertyRepository.java
│   ├── BookingRepository.java
│   └── ...
│
├── service/                   # Business logic
│   ├── AuthService.java
│   ├── UserService.java
│   ├── PropertyService.java
│   ├── BookingService.java
│   └── CustomUserDetailsService.java
│
├── utils/                     # Utility classes
│   ├── UserMapper.java
│   ├── PropertyMapper.java
│   └── BookingMapper.java
│
└── StayEaseApplication.java   # Main class

src/main/resources/
└── application.properties     # Configuration
```

## 📊 Database Schema

| Entity | Description |
|--------|-------------|
| `users` | Người dùng (Guest/Host/Admin) |
| `roles` | Vai trò người dùng |
| `properties` | Bất động sản cho thuê |
| `property_images` | Hình ảnh bất động sản |
| `categories` | Danh mục (House, Apartment...) |
| `amenities` | Tiện nghi (WiFi, Pool...) |
| `bookings` | Đặt phòng |
| `reviews` | Đánh giá |
| `favorites` | Yêu thích |
| `conversations` | Cuộc hội thoại |
| `messages` | Tin nhắn |

## 🚀 Getting Started

### Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### Configuration

Cập nhật `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stayease_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Run

```bash
cd EXE201c_Backend
mvn spring-boot:run
```

Server: `http://localhost:8080`

## 📚 API Endpoints

### Swagger UI
`http://localhost:8080/swagger-ui.html`

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Đăng ký |
| POST | `/api/auth/login` | Đăng nhập |
| GET | `/api/auth/me` | Lấy user hiện tại |

### Properties
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/properties` | Danh sách properties |
| GET | `/api/properties/{id}` | Chi tiết property |
| GET | `/api/properties/search` | Tìm kiếm |
| POST | `/api/properties` | Tạo property (Host) |
| DELETE | `/api/properties/{id}` | Xóa (Host) |

### Bookings
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bookings/my-bookings` | Bookings của tôi |
| POST | `/api/bookings` | Tạo booking |
| PUT | `/api/bookings/{id}/confirm` | Xác nhận (Host) |
| PUT | `/api/bookings/{id}/cancel` | Hủy booking |

## 🔐 Security

- JWT Authentication
- BCrypt Password Encoding
- Role-based Access Control

### Roles
- `ROLE_USER` - Người dùng thường
- `ROLE_HOST` - Chủ nhà
- `ROLE_ADMIN` - Quản trị viên

## 🗄️ Database Generation

Hibernate JPA tự động tạo tables:

```properties
spring.jpa.hibernate.ddl-auto=update
```

DataInitializer sẽ seed Roles, Categories, Amenities khi chạy lần đầu.
