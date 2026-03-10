-- =============================================
-- HolaRent Sample Data Script
-- Run this after the application creates tables
-- =============================================

-- =============================================
-- 📝 THÔNG TIN ĐĂNG NHẬP
-- =============================================
-- Tất cả tài khoản đều có password: password123
--
-- Admin: admin@holarent.com / password123
-- Host chính: host@holarent.com / password123
-- Host phụ: host2@holarent.com / password123
-- User thường: user@holarent.com / password123
-- Guest: guest1@holarent.com, guest2@holarent.com... / password123
-- =============================================

-- =============================================
-- 1. ROLES
-- =============================================
USE `stayease_db`;
INSERT INTO roles (id, name, description)
VALUES (1, 'ROLE_USER', 'Regular user role'),
       (2, 'ROLE_HOST', 'Property host role'),
       (3, 'ROLE_ADMIN', 'Administrator role') ON DUPLICATE KEY
UPDATE description =
VALUES (description);

-- =============================================
-- 2. CATEGORIES
-- =============================================
INSERT INTO categories (id, name, description, icon, slug, is_active, display_order, created_at, is_deleted)
VALUES (1, 'Phòng Trọ Sinh Viên', 'Phòng trọ giá rẻ dành cho sinh viên', 'school', 'student-rooms', true, 1, NOW(), false),
       (2, 'Studio & Mini', 'Căn hộ studio và mini cao cấp', 'apartment', 'studio-mini', true, 2, NOW(), false),
       (3, 'Có Gác', 'Phòng trọ có gác lửng', 'stairs', 'loft-rooms', true, 3, NOW(), false),
       (4, 'Nguyên Căn', 'Nhà nguyên căn cho thuê', 'home', 'whole-house', true, 4, NOW(), false),
       (5, 'Gần Trường', 'Gần các trường đại học', 'location_on', 'near-university', true, 5, NOW(), false),
       (6, 'Resort & Villa', 'Villa và resort nghỉ dưỡng ngắn hạn', 'villa', 'resort-villa', true, 6, NOW(), false),
       (7, 'Beachfront', 'Căn hộ view biển', 'beach_access', 'beachfront', true, 7, NOW(), false),
       (8, 'City Center', 'Trung tâm thành phố', 'location_city', 'city-center', true, 8, NOW(), false),
       (9, 'Budget', 'Giá rẻ tiết kiệm', 'attach_money', 'budget', true, 9, NOW(), false),
       (10, 'Premium', 'Cao cấp full nội thất', 'star', 'premium', true, 10, NOW(), false) ON DUPLICATE KEY
UPDATE name =
VALUES (name);

-- =============================================
-- 3. AMENITIES
-- =============================================
INSERT INTO amenities (id, name, icon, category, is_active, created_at, is_deleted)
VALUES (1, 'WiFi', 'wifi', 'BASIC', true, NOW(), false),
       (2, 'TV', 'tv', 'ENTERTAINMENT', true, NOW(), false),
       (3, 'Air Conditioning', 'ac_unit', 'HEATING_COOLING', true, NOW(), false),
       (4, 'Heating', 'thermostat', 'HEATING_COOLING', true, NOW(), false),
       (5, 'Kitchen', 'kitchen', 'BASIC', true, NOW(), false),
       (6, 'Washer', 'local_laundry_service', 'BASIC', true, NOW(), false),
       (7, 'Dryer', 'dry_cleaning', 'BASIC', true, NOW(), false),
       (8, 'Free Parking', 'local_parking', 'PARKING', true, NOW(), false),
       (9, 'Pool', 'pool', 'OUTDOOR', true, NOW(), false),
       (10, 'Hot Tub', 'hot_tub', 'OUTDOOR', true, NOW(), false),
       (11, 'Gym', 'fitness_center', 'BASIC', true, NOW(), false),
       (12, 'Fireplace', 'fireplace', 'BASIC', true, NOW(), false),
       (13, 'BBQ Grill', 'outdoor_grill', 'OUTDOOR', true, NOW(), false),
       (14, 'Balcony', 'balcony', 'OUTDOOR', true, NOW(), false),
       (15, 'Garden View', 'yard', 'OUTDOOR', true, NOW(), false),
       (16, 'Mountain View', 'terrain', 'OUTDOOR', true, NOW(), false),
       (17, 'Ocean View', 'beach_access', 'OUTDOOR', true, NOW(), false),
       (18, 'City View', 'location_city', 'OUTDOOR', true, NOW(), false),
       (19, 'Workspace', 'desktop_windows', 'BASIC', true, NOW(), false),
       (20, 'Pet Friendly', 'pets', 'FAMILY', true, NOW(), false) ON DUPLICATE KEY
UPDATE name =
VALUES (name);

-- =============================================
-- 4. USERS (Password: password123)
-- =============================================
INSERT INTO users (id, email, password, first_name, last_name, phone, avatar_url, bio, city, country, is_active,
                   is_verified, is_host, created_at, is_deleted)
VALUES
-- Regular User
(1, 'user@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'Regular', 'User',
        '+84123456789', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face',
        'Người dùng thông thường, thích tìm phòng trọ giá rẻ.',
        'Ho Chi Minh City', 'Vietnam', TRUE, TRUE, FALSE, NOW(), false),
-- Host 1 (Chủ nhà chính - có nhiều properties)
(2, 'host@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'Minh', 'Nguyen',
        '+84123456790', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face',
        'Chủ nhà 5 năm kinh nghiệm cho thuê phòng trọ. Uy tín, nhiệt tình hỗ trợ khách thuê.',
        'Ho Chi Minh City', 'Vietnam', TRUE, TRUE, TRUE, NOW(), false),
-- Host 2
(3, 'host2@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'Lan', 'Tran',
        '+84123456791', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop&crop=face',
        'Chủ nhà trọ gần khu vực đại học, chuyên cho sinh viên thuê.',
        'Ho Chi Minh City', 'Vietnam', TRUE, TRUE, TRUE, NOW(), false),
-- Host 3
(4, 'host3@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'Hoang', 'Le',
        '+84123456792', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop&crop=face',
        'Sở hữu nhiều căn hộ mini cao cấp tại Quận 7.',
        'Ho Chi Minh City', 'Vietnam', TRUE, TRUE, TRUE, NOW(), false),
-- Admin User
(5, 'admin@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'Admin', 'System',
        '+84123456793', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face',
        'Quản trị viên hệ thống HolaRent.',
        'Ha Noi', 'Vietnam', TRUE, TRUE, TRUE, NOW(), false),
-- Guest Users
(6, 'guest1@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'David', 'Nguyen',
        '+84901111111', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face',
        'Sinh viên ĐHQG, đang tìm phòng trọ gần trường.',
        'Ho Chi Minh City', 'Vietnam', TRUE, TRUE, FALSE, NOW(), false),
(7, 'guest2@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'Sarah', 'Tran',
        '+84902222222', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop&crop=face',
        'Nhân viên văn phòng, cần thuê căn hộ mini dài hạn.',
        'Ho Chi Minh City', 'Vietnam', TRUE, TRUE, FALSE, NOW(), false),
(8, 'guest3@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'Michael', 'Le',
        '+84903333333', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop&crop=face',
        'Designer freelance, thích không gian yên tĩnh để làm việc.',
        'Ha Noi', 'Vietnam', TRUE, TRUE, FALSE, NOW(), false),
(9, 'guest4@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'Emily', 'Pham',
        '+84904444444', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&h=100&fit=crop&crop=face',
        'Marketing manager, thỉnh thoảng book villa nghỉ dưỡng.',
        'Ha Noi', 'Vietnam', TRUE, TRUE, FALSE, NOW(), false),
(10, 'guest5@holarent.com', '$2a$10$/IZzfVHompbqSQMkQrfaJeB.7f2y/4aPkhCueM1XHlTDwY.O5q8ue', 'John', 'Vo',
        '+84905555555', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=100&h=100&fit=crop&crop=face',
        'Developer, đang tìm phòng trọ có không gian làm việc.',
        'Ho Chi Minh City', 'Vietnam', TRUE, TRUE, FALSE, NOW(), false)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id)
VALUES
-- Regular User: ROLE_USER
(1, 1),
-- Host 1: ROLE_USER + ROLE_HOST
(2, 1),
(2, 2),
-- Host 2: ROLE_USER + ROLE_HOST
(3, 1),
(3, 2),
-- Host 3: ROLE_USER + ROLE_HOST
(4, 1),
(4, 2),
-- Admin User: ALL 3 ROLES
(5, 1),
(5, 2),
(5, 3),
-- Guest Users: ROLE_USER
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 1)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- =============================================
-- 5. PROPERTIES (LONG_TERM + SHORT_TERM)
-- =============================================
INSERT INTO properties (id, title, description, property_type, rental_type, address, city, state, country, latitude, longitude,
                        price_per_night, price_per_month, electricity_cost, water_cost, internet_cost,
                        deposit_months, minimum_lease_months,
                        cleaning_fee, service_fee, max_guests, bedrooms, beds, bathrooms, area_sqft, min_nights, max_nights,
                        check_in_time, check_out_time, status, is_instant_book, is_featured, average_rating,
                        total_reviews, view_count, host_id, category_id, created_at, is_deleted)
VALUES
-- ==================== LONG_TERM PROPERTIES (Phòng trọ dài hạn - Chính) ====================
(1, 'Phòng Trọ Sinh Viên Quận 10 - Gần ĐH Bách Khoa',
 'Phòng trọ 25m² có gác lửng, phù hợp sinh viên. Khu vực an ninh, gần trường ĐH Bách Khoa, chợ, siêu thị. Chủ nhà thân thiện, hỗ trợ nhiệt tình.',
 'APARTMENT', 'LONG_TERM', '123 Lý Thường Kiệt, Phường 7, Quận 10', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7721, 106.6659,
 100000, 2500000, 'Theo giá điện', '100,000đ/tháng', 'Miễn phí', 1, 3,
 NULL, NULL, 2, 1, 1, 1, 25, 1, 365,
 '08:00', '22:00', 'ACTIVE', false, true, 4.85, 67, 1250, 2, 1, NOW(), false),

(2, 'Phòng Trọ Giá Rẻ Bình Thạnh - 2tr/tháng',
 'Phòng 20m² giá sinh viên, đầy đủ nội thất cơ bản. Có ban công, thoáng mát. Gần chợ, xe bus, tiện đi lại.',
 'APARTMENT', 'LONG_TERM', '456 Xô Viết Nghệ Tĩnh, Phường 25, Bình Thạnh', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.8020, 106.7101,
 70000, 2000000, '3,500đ/kWh', '80,000đ/tháng', '100,000đ/tháng', 1, 3,
 NULL, NULL, 2, 1, 1, 1, 20, 1, 365,
 '07:00', '23:00', 'ACTIVE', true, true, 4.92, 89, 2100, 2, 9, NOW(), false),

(3, 'Căn Hộ Mini Cao Cấp Q.7 - Full Nội Thất',
 'Căn hộ mini 30m² cao cấp, full nội thất hiện đại: máy lạnh, tủ lạnh, máy giặt, bếp điện từ. An ninh 24/7, thang máy, parking xe miễn phí.',
 'STUDIO', 'LONG_TERM', '789 Nguyễn Thị Thập, Phường Tân Phú, Quận 7', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7322, 106.7217,
 180000, 5500000, 'Theo giá điện', 'Miễn phí', 'Miễn phí', 2, 6,
 NULL, NULL, 2, 1, 1, 1, 30, 1, 365,
 '09:00', '21:00', 'ACTIVE', false, true, 4.88, 134, 3200, 2, 2, NOW(), false),

(4, 'Phòng Có Ban Công Rộng - Gần ĐH FPT',
 'Phòng 22m² có ban công rộng để phơi đồ, thoáng mát. Giờ giấc tự do, có chỗ để xe rộng rãi. Khu dân cư an ninh.',
 'APARTMENT', 'LONG_TERM', '321 Đường D2, Phường 25, Bình Thạnh', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.8021, 106.7102,
 90000, 2800000, 'Theo giá điện', '100,000đ/tháng', 'Miễn phí', 1, 3,
 NULL, NULL, 2, 1, 1, 1, 22, 1, 365,
 '00:00', '23:59', 'ACTIVE', true, true, 4.90, 178, 2800, 2, 3, NOW(), false),

(5, 'Nhà Nguyên Căn 2PN Thủ Đức',
 'Nhà 2 tầng, 2 phòng ngủ, phù hợp gia đình nhỏ hoặc nhóm bạn ở cùng. Có sân để xe, khu vực yên tĩnh.',
 'HOUSE', 'LONG_TERM', '555 Kha Vạn Cân, Phường Linh Trung, Thủ Đức', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.8701, 106.8000,
 250000, 7500000, 'Theo giá điện', 'Theo giá nước', 'Miễn phí', 2, 6,
 NULL, NULL, 4, 2, 3, 2, 80, 1, 365,
 '08:00', '22:00', 'ACTIVE', false, false, 4.75, 92, 1500, 2, 4, NOW(), false),

(6, 'Phòng Trọ Gần ĐH Khoa Học Tự Nhiên',
 'Phòng 18m² sạch sẽ, giá rẻ. Gần trường ĐHKHTN, ĐHQG. Có WC riêng, bếp chung.',
 'APARTMENT', 'LONG_TERM', '111 Nguyễn Văn Cừ, Phường 4, Quận 5', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7588, 106.6789,
 65000, 1800000, '3,500đ/kWh', '80,000đ/tháng', 'Chung', 1, 3,
 NULL, NULL, 1, 1, 1, 1, 18, 1, 365,
 '06:00', '23:00', 'ACTIVE', true, false, 4.80, 45, 890, 2, 5, NOW(), false),

(7, 'Studio Cao Cấp Q.1 - View Sông',
 'Studio 35m² view sông Saigon, full nội thất cao cấp. Ngay trung tâm Q.1, tiện ích 5 sao.',
 'STUDIO', 'LONG_TERM', '222 Tôn Đức Thắng, Phường Bến Nghé, Quận 1', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7770, 106.7041,
 220000, 6800000, 'Miễn phí', 'Miễn phí', 'Miễn phí', 2, 6,
 NULL, NULL, 2, 1, 1, 1, 35, 1, 365,
 '10:00', '22:00', 'ACTIVE', false, true, 4.95, 156, 4500, 2, 10, NOW(), false),

(8, 'Phòng Có Gác Rộng Q.Tân Bình',
 'Phòng 28m² có gác lửng rộng, để đồ thoải mái. Gần sân bay, thuận tiện đi lại.',
 'APARTMENT', 'LONG_TERM', '333 Hoàng Văn Thụ, Phường 4, Tân Bình', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7998, 106.6550,
 85000, 2600000, 'Theo giá điện', '100,000đ/tháng', '100,000đ/tháng', 1, 3,
 NULL, NULL, 2, 1, 1, 1, 28, 1, 365,
 '07:00', '23:00', 'ACTIVE', true, false, 4.82, 67, 1100, 2, 3, NOW(), false),

(9, 'Phòng Trọ Giá Rẻ Q.12 - 1.5tr',
 'Phòng 16m² giá cực rẻ, phù hợp sinh viên. Có máy lạnh, WC riêng.',
 'APARTMENT', 'LONG_TERM', '444 Lê Văn Khương, Phường Hiệp Thành, Quận 12', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.8655, 106.6280,
 55000, 1500000, '3,500đ/kWh', '60,000đ/tháng', 'Chung', 1, 3,
 NULL, NULL, 2, 1, 1, 1, 16, 1, 365,
 '06:00', '23:00', 'ACTIVE', true, false, 4.70, 34, 670, 2, 9, NOW(), false),

(10, 'Nhà Nguyên Căn 3PN Q.Bình Thạnh',
 'Nhà 3 tầng, 3 phòng ngủ, sân thượng rộng. Phù hợp gia đình hoặc công ty thuê.',
 'HOUSE', 'LONG_TERM', '666 Xô Viết Nghệ Tĩnh, Phường 26, Bình Thạnh', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.8123, 106.7145,
 350000, 10500000, 'Theo giá điện', 'Theo giá nước', 'Miễn phí', 2, 12,
 NULL, NULL, 6, 3, 4, 3, 120, 1, 365,
 '09:00', '21:00', 'ACTIVE', false, false, 4.88, 78, 2300, 2, 4, NOW(), false),

(14, 'Phòng Trọ Sinh Viên Gò Vấp - 1.8tr',
 'Phòng 19m² gần ĐH Công Nghiệp Thực Phẩm, có WC riêng, máy lạnh. Chủ nhà thân thiện, giá tốt.',
 'APARTMENT', 'LONG_TERM', '234 Quang Trung, Phường 10, Gò Vấp', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.8372, 106.6564,
 60000, 1800000, '3,500đ/kWh', '70,000đ/tháng', 'Chung', 1, 3,
 NULL, NULL, 2, 1, 1, 1, 19, 1, 365,
 '06:00', '23:00', 'ACTIVE', true, true, 4.87, 56, 980, 3, 1, NOW(), false),

(15, 'Studio Mini Q.Phú Nhuận - Có Bếp Riêng',
 'Studio 28m² có bếp riêng, ban công. Full nội thất, thang máy, parking. Khu dân cư yên tĩnh.',
 'STUDIO', 'LONG_TERM', '567 Phan Xích Long, Phường 2, Phú Nhuận', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7990, 106.6810,
 150000, 4500000, 'Miễn phí', 'Miễn phí', 'Miễn phí', 2, 6,
 NULL, NULL, 2, 1, 1, 1, 28, 1, 365,
 '08:00', '22:00', 'ACTIVE', true, true, 4.93, 112, 2680, 3, 2, NOW(), false),

(16, 'Phòng Có Gác Lửng Q.Tân Phú - 2.2tr',
 'Phòng 24m² có gác để ngủ, tầng dưới để bàn làm việc. Gần Đầm Sen, Aeon Mall.',
 'APARTMENT', 'LONG_TERM', '890 Lũy Bán Bích, Phường Hòa Thạnh, Tân Phú', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7725, 106.6234,
 75000, 2200000, 'Theo giá điện', '90,000đ/tháng', '100,000đ/tháng', 1, 3,
 NULL, NULL, 2, 1, 1, 1, 24, 1, 365,
 '07:00', '23:00', 'ACTIVE', true, false, 4.79, 43, 750, 3, 3, NOW(), false),

(17, 'Căn Hộ 2PN Q.2 - View Sông',
 'Căn hộ 55m² view sông tại khu Thảo Điền, full nội thất cao cấp. Gym, pool, an ninh 24/7.',
 'APARTMENT', 'LONG_TERM', '123 Đường Số 1, Phường Thảo Điền, Quận 2', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.8044, 106.7399,
 280000, 8500000, 'Miễn phí', 'Miễn phí', 'Miễn phí', 2, 6,
 NULL, NULL, 4, 2, 2, 2, 55, 1, 365,
 '09:00', '21:00', 'ACTIVE', false, true, 4.91, 145, 3900, 4, 10, NOW(), false),

(18, 'Nhà Trọ Giá Rẻ Q.8 - 1.3tr',
 'Phòng 15m² cơ bản, giá siêu rẻ. Phù hợp sinh viên, người lao động. Có WC riêng.',
 'APARTMENT', 'LONG_TERM', '456 Phạm Thế Hiển, Phường 7, Quận 8', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7393, 106.6650,
 45000, 1300000, '3,500đ/kWh', '50,000đ/tháng', 'Chung', 1, 3,
 NULL, NULL, 1, 1, 1, 1, 15, 1, 365,
 '06:00', '23:30', 'ACTIVE', true, false, 4.65, 28, 520, 3, 9, NOW(), false),

(19, 'Phòng Trọ Gần ĐH Kinh Tế - 2.3tr',
 'Phòng 21m² gần ĐH Kinh Tế, có ban công nhỏ. Khu vực sầm uất, tiện ăn uống mua sắm.',
 'APARTMENT', 'LONG_TERM', '789 Điện Biên Phủ, Phường 22, Bình Thạnh', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.8030, 106.7112,
 80000, 2300000, 'Theo giá điện', '80,000đ/tháng', '100,000đ/tháng', 1, 3,
 NULL, NULL, 2, 1, 1, 1, 21, 1, 365,
 '07:00', '23:00', 'ACTIVE', true, true, 4.86, 95, 1780, 3, 5, NOW(), false),

(20, 'Studio Cao Cấp Q.3 - Full Nội Thất',
 'Studio 32m² ngay trung tâm Q.3. Full nội thất xịn, view đẹp. Phù hợp người đi làm.',
 'STUDIO', 'LONG_TERM', '345 Nam Kỳ Khởi Nghĩa, Phường 7, Quận 3', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7756, 106.6917,
 200000, 6000000, 'Miễn phí', 'Miễn phí', 'Miễn phí', 2, 6,
 NULL, NULL, 2, 1, 1, 1, 32, 1, 365,
 '09:00', '22:00', 'ACTIVE', false, true, 4.94, 167, 4100, 4, 10, NOW(), false),

-- ==================== SHORT_TERM PROPERTIES (Apartment ngắn hạn - Phụ) ====================
(11, 'Luxury Beachfront Villa Phú Quốc',
 'Biệt thự biển 5 sao tại Phú Quốc. Pool riêng, view biển 180°, phù hợp nghỉ dưỡng gia đình.',
 'VILLA', 'SHORT_TERM', '123 Bãi Trường Beach', 'Phu Quoc', 'Kien Giang', 'Vietnam', 10.2231, 103.9673,
 250000, NULL, NULL, NULL, NULL, NULL, NULL,
 50000, 30000, 8, 4, 4, 3, 200, 2, 30,
 '14:00', '12:00', 'ACTIVE', true, true, 4.92, 234, 8900, 2, 6, NOW(), false),

(12, 'Dreamy Mountain Retreat Đà Lạt',
 'Villa view núi tuyệt đẹp tại Đà Lạt. Lò sưởi, vườn hoa, không gian lãng mạn.',
 'VILLA', 'SHORT_TERM', '456 Valley View Road', 'Da Lat', 'Lam Dong', 'Vietnam', 11.9404, 108.4583,
 120000, NULL, NULL, NULL, NULL, NULL, NULL,
 25000, 15000, 4, 2, 2, 2, 100, 2, 14,
 '15:00', '11:00', 'ACTIVE', true, true, 4.96, 128, 5600, 2, 6, NOW(), false),

(13, 'Ocean View Condo Nha Trang',
 'Căn hộ view biển Nha Trang, rooftop pool, gym. Ngay trung tâm du lịch.',
 'CONDO', 'SHORT_TERM', '789 Beach Boulevard', 'Nha Trang', 'Khanh Hoa', 'Vietnam', 12.2388, 109.1967,
 110000, NULL, NULL, NULL, NULL, NULL, NULL,
 25000, 15000, 4, 2, 2, 2, 70, 2, 21,
 '15:00', '11:00', 'ACTIVE', true, false, 4.75, 92, 3800, 2, 7, NOW(), false),

(21, 'Modern Apartment Q.1 - Short Stay',
 'Căn hộ hiện đại ngắn hạn tại Q.1, gần Nhà Thờ Đức Bà. Phù hợp du lịch công tác.',
 'APARTMENT', 'SHORT_TERM', '99 Pasteur, Phường Bến Nghé, Quận 1', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7789, 106.6989,
 80000, NULL, NULL, NULL, NULL, NULL, NULL,
 20000, 10000, 2, 1, 1, 1, 40, 1, 30,
 '14:00', '12:00', 'ACTIVE', true, true, 4.83, 67, 2100, 2, 8, NOW(), false),

(22, 'Resort Villa Vũng Tàu - Private Beach',
 'Villa resort riêng biệt tại Vũng Tàu. Bãi biển riêng, BBQ area, pool.',
 'VILLA', 'SHORT_TERM', '234 Thùy Vân, Phường Thắng Tam, Vũng Tàu', 'Vung Tau', 'Ba Ria-Vung Tau', 'Vietnam', 10.3460, 107.0843,
 180000, NULL, NULL, NULL, NULL, NULL, NULL,
 40000, 25000, 6, 3, 3, 2, 150, 2, 14,
 '14:00', '12:00', 'ACTIVE', true, true, 4.89, 178, 5200, 4, 6, NOW(), false),

(23, 'Cozy Studio Đà Nẵng - Beach Access',
 'Studio ấm cúng gần biển Mỹ Khê. Tiện nghi đầy đủ, giá tốt.',
 'STUDIO', 'SHORT_TERM', '567 Võ Nguyên Giáp, Phường Phước Mỹ, Sơn Trà', 'Da Nang', 'Da Nang', 'Vietnam', 16.0544, 108.2440,
 70000, NULL, NULL, NULL, NULL, NULL, NULL,
 15000, 10000, 2, 1, 1, 1, 35, 1, 21,
 '14:00', '11:00', 'ACTIVE', true, false, 4.78, 89, 2800, 3, 7, NOW(), false),

(24, 'Luxury Penthouse Q.2 - Weekend Getaway',
 'Penthouse sang trọng tại Q.2 cho kỳ nghỉ cuối tuần. Rooftop pool, view 360°.',
 'CONDO', 'SHORT_TERM', '888 Đào Trí, Phường Phú Thuận, Quận 7', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7335, 106.7220,
 200000, NULL, NULL, NULL, NULL, NULL, NULL,
 50000, 30000, 6, 3, 3, 2, 120, 2, 7,
 '15:00', '11:00', 'ACTIVE', false, true, 4.96, 145, 4800, 4, 6, NOW(), false)

ON DUPLICATE KEY UPDATE title = VALUES(title);

-- =============================================
-- 6. PROPERTY IMAGES
-- =============================================
INSERT INTO property_images (id, property_id, image_url, caption, display_order, is_primary, created_at, is_deleted)
VALUES
-- LONG_TERM Property 1 - Phòng trọ sinh viên Q.10
(1, 1, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/564057778.jpg?k=73ee7fd3b53248c3685c23906f1afd041cf873f0c5de1f7e9fcc1a3750ee60cc&o=',
 'Phòng trọ có gác lửng rộng rãi', 1, true, NOW(), false),
(2, 1, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800',
 'Khu vực bếp chung', 2, false, NOW(), false),

-- LONG_TERM Property 2 - Phòng giá rẻ Bình Thạnh
(3, 2, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/555473560.jpg?k=bde60077636f53eb22399e51f4b632e9b39bd044dc9434d881de76194f0f1416&o=',
 'Phòng trọ giá rẻ có ban công', 1, true, NOW(), false),
(4, 2, 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800',
 'Nội thất cơ bản đầy đủ', 2, false, NOW(), false),

-- LONG_TERM Property 3 - Căn hộ mini cao cấp Q.7
(5, 3, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/566740260.jpg?k=086347f9d31f99e70ab7bf6cd1cae666332fd4a3918ab1ef8c900bb3e2447c10&o=',
 'Studio cao cấp full nội thất', 1, true, NOW(), false),
(6, 3, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800',
 'Phòng khách hiện đại', 2, false, NOW(), false),

-- LONG_TERM Property 4 - Phòng có ban công gần FPT
(7, 4, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/555473559.jpg?k=ccc032344831b60e5d6f5e1c7d5ecea7fe47067152dd89ab2e6a099b595f61bf&o=',
 'Ban công rộng thoáng mát', 1, true, NOW(), false),

-- LONG_TERM Property 5 - Nhà nguyên căn 2PN
(8, 5, 'https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800',
 'Nhà nguyên căn 2 tầng', 1, true, NOW(), false),
(9, 5, 'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800',
 'Phòng ngủ tầng 2', 2, false, NOW(), false),

-- LONG_TERM Property 6 - Gần ĐHKHTN
(10, 6, 'https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800',
 'Phòng trọ sạch sẽ gần trường', 1, true, NOW(), false),

-- LONG_TERM Property 7 - Studio Q.1 view sông
(11, 7, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/363097546.jpg?k=c35835b26e4882daf2ef3d67793ee6b99015b5b3a9cc1ee21f5b74d183c22353&o=',
 'Studio view sông Saigon', 1, true, NOW(), false),
(12, 7, 'https://images.unsplash.com/photo-1502672260066-6bc35f0ffecd?w=800',
 'View sông tuyệt đẹp', 2, false, NOW(), false),

-- LONG_TERM Property 8 - Phòng có gác Tân Bình
(13, 8, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800',
 'Gác lửng rộng rãi', 1, true, NOW(), false),

-- LONG_TERM Property 9 - Giá rẻ Q.12
(14, 9, 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800',
 'Phòng giá rẻ có máy lạnh', 1, true, NOW(), false),

-- LONG_TERM Property 10 - Nhà 3PN Bình Thạnh
(15, 10, 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800',
 'Nhà 3 tầng rộng rãi', 1, true, NOW(), false),
(16, 10, 'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800',
 'Sân thượng thoáng', 2, false, NOW(), false),

-- SHORT_TERM Property 11 - Villa Phú Quốc
(17, 11, 'https://lh3.googleusercontent.com/aida-public/AB6AXuBA1FZXF9cRjf-uEn_FJtCFTrX2defPgNDht71F8NB2_RkRQVbUSeiRC9EUKSu9qd86GlPp421zXb0dUFvMqsUNc5Ro4JDRuxOkEM_DMoau6u3Q51wzcHXB7wEeBoZ9M6b_0QgrJ7ZwCAp4f0KjxXu2xF0VMUVLnk73lLIb0LlgWhWJrYYl2jUEEeTC3TchqGvoE9TNhJH1hVjKVMGdTSYMWP4EthwfXHr8OQxUwNbZJ6YbaKNcPE5kRvAGlbtZk3hv6uaQFhBMKko',
 'Villa biển Phú Quốc', 1, true, NOW(), false),
(18, 11, 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/2c/aa/76/21/can-h-co-t-m-nhin-phao.jpg?w=1400&h=-1&s=1',
 'Pool riêng view biển', 2, false, NOW(), false),
(19, 11, 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/2c/aa/76/1d/can-h-co-t-m-nhin-phao.jpg?w=1100&h=-1&s=1',
 'Phòng ngủ master', 3, false, NOW(), false),

-- SHORT_TERM Property 12 - Villa Đà Lạt
(20, 12, 'https://lh3.googleusercontent.com/aida-public/AB6AXuBnjqLsczraflUeSqAym0kpTUehfR_CPG1p8fkeEu5uyIfw6skunxAhMhZKGjDC7k-qPI9aY2wF7wBLSjkfSe2Kx9yIlzQXCo2Rj80_qBUDmsr7ryCubOd1oKQMrMkir0btGJ_dO3KXDhq5tEQI3y8BnAnOFyI8xnFEzZ5oCYXOT40H8pao5VGzvyc2Z7M63Xa1pUdI9c3MoeTAmkFgYrND2Mi191q9gba-OMBwei4_W38QEXmFEJafBbnT9bCgHMrDwuB_JikldFA',
 'Villa Đà Lạt view núi', 1, true, NOW(), false),
(21, 12, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/564057778.jpg?k=73ee7fd3b53248c3685c23906f1afd041cf873f0c5de1f7e9fcc1a3750ee60cc&o=',
 'Lò sưởi ấm áp', 2, false, NOW(), false),

-- SHORT_TERM Property 13 - Condo Nha Trang
(22, 13, 'https://lh3.googleusercontent.com/aida-public/AB6AXuDDsfGYfrut18yfxTaFwKmV01f31CCBoVp2cG9nsOyf_S-I4Xe4WTPL3hTnYEtFkM-dCB6jATXnv85X0tulBS5WH4MPwGaD0AcMC_mKEdq8MLtNHY0HZKOqwyJUzCpU0BGKxOMOo_M-GW1V84OfrxekctjtiBME_A6MUtLxCwWQUeEjQEu111ub8ZEkhg2jK1NLtybGKqpWUbCsx7oUDIzSikwounRwUjYTRn09M6sNGyxndxI65LFkqacjp0R1XAdiLjXsf1LRieg',
 'Condo view biển Nha Trang', 1, true, NOW(), false),
(23, 13, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/782427677.jpg?k=621ac63bf947e8b9d9ac50ab26ca4ceaa6cb95c10cb17036f8a8aae9ef080473&o=',
 'Rooftop pool', 2, false, NOW(), false),

-- LONG_TERM Property 14 - Phòng Gò Vấp
(24, 14, 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800',
 'Phòng trọ sinh viên Gò Vấp', 1, true, NOW(), false),

-- LONG_TERM Property 15 - Studio Phú Nhuận
(25, 15, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/566740260.jpg?k=086347f9d31f99e70ab7bf6cd1cae666332fd4a3918ab1ef8c900bb3e2447c10&o=',
 'Studio mini Phú Nhuận', 1, true, NOW(), false),
(26, 15, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800',
 'Bếp riêng hiện đại', 2, false, NOW(), false),

-- LONG_TERM Property 16 - Phòng có gác Tân Phú
(27, 16, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800',
 'Phòng có gác Tân Phú', 1, true, NOW(), false),

-- LONG_TERM Property 17 - Căn hộ 2PN Q.2
(28, 17, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/363097546.jpg?k=c35835b26e4882daf2ef3d67793ee6b99015b5b3a9cc1ee21f5b74d183c22353&o=',
 'Căn hộ view sông Q.2', 1, true, NOW(), false),
(29, 17, 'https://images.unsplash.com/photo-1502672260066-6bc35f0ffecd?w=800',
 'View sông đẹp', 2, false, NOW(), false),

-- LONG_TERM Property 18 - Nhà trọ Q.8
(30, 18, 'https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800',
 'Nhà trọ giá rẻ Q.8', 1, true, NOW(), false),

-- LONG_TERM Property 19 - Phòng gần ĐH Kinh Tế
(31, 19, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/555473559.jpg?k=ccc032344831b60e5d6f5e1c7d5ecea7fe47067152dd89ab2e6a099b595f61bf&o=',
 'Phòng trọ gần ĐH Kinh Tế', 1, true, NOW(), false),

-- LONG_TERM Property 20 - Studio Q.3
(32, 20, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/566740260.jpg?k=086347f9d31f99e70ab7bf6cd1cae666332fd4a3918ab1ef8c900bb3e2447c10&o=',
 'Studio cao cấp Q.3', 1, true, NOW(), false),
(33, 20, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800',
 'Nội thất xịn', 2, false, NOW(), false),

-- SHORT_TERM Property 21 - Apartment Q.1
(34, 21, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800',
 'Modern apartment Q.1', 1, true, NOW(), false),

-- SHORT_TERM Property 22 - Villa Vũng Tàu
(35, 22, 'https://lh3.googleusercontent.com/aida-public/AB6AXuBA1FZXF9cRjf-uEn_FJtCFTrX2defPgNDht71F8NB2_RkRQVbUSeiRC9EUKSu9qd86GlPp421zXb0dUFvMqsUNc5Ro4JDRuxOkEM_DMoau6u3Q51wzcHXB7wEeBoZ9M6b_0QgrJ7ZwCAp4f0KjxXu2xF0VMUVLnk73lLIb0LlgWhWJrYYl2jUEEeTC3TchqGvoE9TNhJH1hVjKVMGdTSYMWP4EthwfXHr8OQxUwNbZJ6YbaKNcPE5kRvAGlbtZk3hv6uaQFhBMKko',
 'Villa resort Vũng Tàu', 1, true, NOW(), false),
(36, 22, 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/2c/aa/76/21/can-h-co-t-m-nhin-phao.jpg?w=1400&h=-1&s=1',
 'Private beach', 2, false, NOW(), false),

-- SHORT_TERM Property 23 - Studio Đà Nẵng
(37, 23, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800',
 'Cozy studio Đà Nẵng', 1, true, NOW(), false),

-- SHORT_TERM Property 24 - Penthouse Q.2
(38, 24, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/363097546.jpg?k=c35835b26e4882daf2ef3d67793ee6b99015b5b3a9cc1ee21f5b74d183c22353&o=',
 'Luxury penthouse', 1, true, NOW(), false),
(39, 24, 'https://images.unsplash.com/photo-1502672260066-6bc35f0ffecd?w=800',
 'Rooftop pool view 360°', 2, false, NOW(), false)

ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

-- =============================================
-- 7. PROPERTY AMENITIES
-- =============================================
INSERT INTO property_amenities (property_id, amenity_id)
VALUES
-- Property 1: Phòng trọ sinh viên Q.10
(1, 1),  -- WiFi
(1, 3),  -- Air Conditioning
(1, 5),  -- Kitchen
(1, 14), -- Balcony

-- Property 2: Phòng giá rẻ Bình Thạnh
(2, 1),  -- WiFi
(2, 3),  -- Air Conditioning
(2, 14), -- Balcony

-- Property 3: Studio mini Q.7
(3, 1),  -- WiFi
(3, 2),  -- TV
(3, 3),  -- Air Conditioning
(3, 5),  -- Kitchen
(3, 6),  -- Washer
(3, 8),  -- Parking
(3, 11), -- Gym

-- Property 4: Phòng có ban công FPT
(4, 1),  -- WiFi
(4, 3),  -- Air Conditioning
(4, 14), -- Balcony

-- Property 5: Nhà nguyên căn 2PN
(5, 1),  -- WiFi
(5, 2),  -- TV
(5, 3),  -- Air Conditioning
(5, 5),  -- Kitchen
(5, 6),  -- Washer
(5, 8),  -- Parking

-- Property 6: Gần ĐHKHTN
(6, 1),  -- WiFi
(6, 3),  -- Air Conditioning

-- Property 7: Studio Q.1 view sông
(7, 1),  -- WiFi
(7, 2),  -- TV
(7, 3),  -- Air Conditioning
(7, 5),  -- Kitchen
(7, 6),  -- Washer
(7, 8),  -- Parking
(7, 11), -- Gym
(7, 18), -- City View

-- Property 8: Phòng có gác Tân Bình
(8, 1),  -- WiFi
(8, 3),  -- Air Conditioning
(8, 8),  -- Parking

-- Property 9: Phòng giá rẻ Q.12
(9, 1),  -- WiFi
(9, 3),  -- Air Conditioning

-- Property 10: Nhà 3PN Bình Thạnh
(10, 1), -- WiFi
(10, 2), -- TV
(10, 3), -- Air Conditioning
(10, 5), -- Kitchen
(10, 6), -- Washer
(10, 8), -- Parking

-- Property 11: Villa Phú Quốc
(11, 1), -- WiFi
(11, 2), -- TV
(11, 3), -- Air Conditioning
(11, 5), -- Kitchen
(11, 6), -- Washer
(11, 8), -- Parking
(11, 9), -- Pool
(11, 13), -- BBQ
(11, 14), -- Balcony
(11, 17), -- Ocean View

-- Property 12: Villa Đà Lạt
(12, 1), -- WiFi
(12, 2), -- TV
(12, 4), -- Heating
(12, 5), -- Kitchen
(12, 12), -- Fireplace
(12, 15), -- Garden View
(12, 16), -- Mountain View

-- Property 13: Condo Nha Trang
(13, 1), -- WiFi
(13, 2), -- TV
(13, 3), -- Air Conditioning
(13, 5), -- Kitchen
(13, 9), -- Pool
(13, 11), -- Gym
(13, 14), -- Balcony
(13, 17), -- Ocean View

-- Property 14: Phòng Gò Vấp
(14, 1), -- WiFi
(14, 3), -- Air Conditioning

-- Property 15: Studio Phú Nhuận
(15, 1), -- WiFi
(15, 2), -- TV
(15, 3), -- Air Conditioning
(15, 5), -- Kitchen
(15, 6), -- Washer
(15, 8), -- Parking

-- Property 16: Phòng có gác Tân Phú
(16, 1), -- WiFi
(16, 3), -- Air Conditioning
(16, 14), -- Balcony

-- Property 17: Căn hộ 2PN Q.2
(17, 1), -- WiFi
(17, 2), -- TV
(17, 3), -- Air Conditioning
(17, 5), -- Kitchen
(17, 6), -- Washer
(17, 8), -- Parking
(17, 9), -- Pool
(17, 11), -- Gym

-- Property 18: Nhà trọ Q.8
(18, 1), -- WiFi
(18, 3), -- Air Conditioning

-- Property 19: Phòng gần ĐH Kinh Tế
(19, 1), -- WiFi
(19, 3), -- Air Conditioning
(19, 14), -- Balcony

-- Property 20: Studio Q.3
(20, 1), -- WiFi
(20, 2), -- TV
(20, 3), -- Air Conditioning
(20, 5), -- Kitchen
(20, 6), -- Washer
(20, 11), -- Gym

-- Property 21: Apartment Q.1 short-term
(21, 1), -- WiFi
(21, 2), -- TV
(21, 3), -- Air Conditioning
(21, 5), -- Kitchen
(21, 18), -- City View

-- Property 22: Villa Vũng Tàu
(22, 1), -- WiFi
(22, 2), -- TV
(22, 3), -- Air Conditioning
(22, 5), -- Kitchen
(22, 9), -- Pool
(22, 13), -- BBQ
(22, 17), -- Ocean View

-- Property 23: Studio Đà Nẵng
(23, 1), -- WiFi
(23, 2), -- TV
(23, 3), -- Air Conditioning
(23, 5), -- Kitchen
(23, 17), -- Ocean View

-- Property 24: Penthouse Q.2
(24, 1), -- WiFi
(24, 2), -- TV
(24, 3), -- Air Conditioning
(24, 5), -- Kitchen
(24, 9), -- Pool
(24, 11), -- Gym
(24, 18)  -- City View

ON DUPLICATE KEY UPDATE property_id = VALUES(property_id);

-- =============================================
-- 8. BOOKINGS
-- =============================================
INSERT INTO bookings (id, property_id, guest_id, check_in_date, check_out_date, total_nights, total_months,
                      guests_count, total_price, status, payment_status, payment_method,
                      special_requests, created_at, is_deleted)
VALUES
-- LONG_TERM bookings (phòng trọ)
(1, 1, 6, '2026-02-01', '2026-05-01', 90, 3, 1, 7500000, 'CONFIRMED', 'PAID', 'BANK_TRANSFER',
 'Cần phòng sạch sẽ', NOW(), false),
(2, 2, 7, '2026-01-15', '2026-04-15', 90, 3, 1, 6000000, 'CONFIRMED', 'PAID', 'BANK_TRANSFER',
 'Mong chủ nhà hỗ trợ giấy tờ', NOW(), false),
(3, 3, 7, '2026-03-01', '2026-09-01', 180, 6, 2, 33000000, 'CONFIRMED', 'PAID', 'BANK_TRANSFER',
 'Thuê dài hạn 6 tháng', NOW(), false),
(4, 6, 8, '2026-02-10', '2026-05-10', 90, 3, 1, 5400000, 'CONFIRMED', 'PAID', 'CASH',
 NULL, NOW(), false),
(5, 14, 6, '2026-01-01', '2026-04-01', 90, 3, 2, 5400000, 'CONFIRMED', 'PAID', 'BANK_TRANSFER',
 'Sinh viên thuê trọ', NOW(), false),

-- SHORT_TERM bookings (apartment ngắn hạn)
(6, 11, 9, '2026-03-10', '2026-03-15', 5, NULL, 6, 1250000, 'CONFIRMED', 'PAID', 'CREDIT_CARD',
 'Gia đình nghỉ dưỡng', NOW(), false),
(7, 12, 9, '2026-04-01', '2026-04-05', 4, NULL, 4, 480000, 'CONFIRMED', 'PAID', 'CREDIT_CARD',
 'Muốn phòng view núi', NOW(), false),
(8, 13, 10, '2026-02-20', '2026-02-25', 5, NULL, 4, 550000, 'CONFIRMED', 'PAID', 'BANK_TRANSFER',
 NULL, NOW(), false),
(9, 21, 8, '2026-03-01', '2026-03-03', 2, NULL, 2, 160000, 'CONFIRMED', 'PAID', 'CREDIT_CARD',
 'Công tác ngắn hạn', NOW(), false),
(10, 22, 9, '2026-05-01', '2026-05-05', 4, NULL, 5, 720000, 'PENDING', 'PENDING', 'CREDIT_CARD',
 'Kỳ nghỉ gia đình', NOW(), false)

ON DUPLICATE KEY UPDATE id = VALUES(id);

-- =============================================
-- 9. REVIEWS
-- =============================================
INSERT INTO reviews (id, property_id, guest_id, booking_id, rating, cleanliness, accuracy,
                     check_in_experience, communication, location_rating, value,
                     comment, host_reply, created_at, is_deleted)
VALUES
-- Reviews cho LONG_TERM properties
(1, 1, 6, 1, 4.9, 5.0, 4.8, 4.9, 5.0, 4.7, 4.9,
 'Phòng rất tốt, chủ nhà thân thiện. Khu vực gần trường, thuận tiện đi lại. Rất hài lòng!',
 'Cảm ơn bạn! Chúc bạn học tập tốt nhé!', NOW(), false),

(2, 2, 7, 2, 4.9, 5.0, 5.0, 4.8, 4.9, 4.8, 5.0,
 'Giá rẻ mà phòng ổn, sạch sẽ. Chủ nhà dễ tính. Recommend cho ae sinh viên!',
 'Thanks bạn! Ở vui vẻ nhé!', NOW(), false),

(3, 3, 7, 3, 4.9, 5.0, 4.8, 4.9, 4.8, 4.9, 4.7,
 'Studio đẹp, full nội thất xịn. Giá hơi cao nhưng xứng đáng. An ninh tốt.',
 'Cảm ơn feedback! Mong bạn ở lâu dài!', NOW(), false),

(4, 6, 8, 4, 4.8, 4.7, 4.8, 4.9, 5.0, 4.7, 4.9,
 'Phòng gần trường, giá sinh viên. Chủ nhà nhiệt tình. Tốt!',
 'Cảm ơn bạn! Học tập tốt!', NOW(), false),

-- Reviews cho SHORT_TERM properties
(5, 11, 9, 6, 4.9, 5.0, 4.9, 4.9, 4.8, 5.0, 4.7,
 'Villa tuyệt vời! View biển đẹp, pool riêng sang chảnh. Gia đình rất hài lòng với kỳ nghỉ.',
 'Thank you! Welcome back anytime!', NOW(), false),

(6, 12, 9, 7, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 4.9,
 'Đà Lạt lãng mạn, villa này càng lãng mạn hơn. View núi tuyệt đẹp, lò sưởi ấm áp. Perfect!',
 'Cảm ơn bạn! Chúc hai bạn hạnh phúc!', NOW(), false),

(7, 13, 10, 8, 4.8, 4.7, 4.8, 4.7, 4.9, 4.9, 4.7,
 'Condo view biển Nha Trang đẹp. Pool trên tầng thượng view xuống biển. Recommended!',
 'Thanks! Come back soon!', NOW(), false),

(8, 21, 8, 9, 4.8, 4.9, 4.7, 4.8, 4.8, 5.0, 4.8,
 'Căn hộ ngắn hạn Q.1 tiện lợi cho công tác. Gần trung tâm, dễ đi lại.',
 'Cảm ơn bạn! Chúc công tác tốt!', NOW(), false)

ON DUPLICATE KEY UPDATE id = VALUES(id);

-- =============================================
-- 10. FAVORITES
-- =============================================
INSERT INTO favorites (user_id, property_id, created_at)
VALUES
-- Regular user favorites
(1, 1, NOW()),
(1, 3, NOW()),
(1, 7, NOW()),
-- Guest users favorites - LONG_TERM
(6, 1, NOW()),
(6, 2, NOW()),
(6, 14, NOW()),
(7, 2, NOW()),
(7, 3, NOW()),
(7, 15, NOW()),
(8, 6, NOW()),
(8, 19, NOW()),
(10, 1, NOW()),
(10, 14, NOW()),
-- Guest users favorites - SHORT_TERM
(9, 11, NOW()),
(9, 12, NOW()),
(9, 22, NOW()),
(10, 13, NOW()),
(10, 21, NOW())
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);
INSERT INTO posting_packages (name, slug, description, price, duration_days, priority_level, is_active, features, created_at, is_deleted)
VALUES
    ('Gói Miễn Phí', 'free-package', 'Đăng tin cơ bản không tốn phí', 0, 7, 1, true,
     'Hiển thị trong danh sách,Hỗ trợ 3 hình ảnh,Thời hạn 7 ngày',
     NOW(), false),

    ('Gói Tiết Kiệm', 'economy-package', 'Phù hợp cho tin đăng thông thường', 50000, 15, 1, true,
     'Hiển thị trong danh sách,Hỗ trợ 10 hình ảnh,Thời hạn 15 ngày,Hỗ trợ video 360°',
     NOW(), false),

    ('Gói Tiêu Chuẩn', 'standard-package', 'Lựa chọn phổ biến nhất', 150000, 30, 2, true,
     'Ưu tiên hiển thị,Hỗ trợ 15 hình ảnh,Thời hạn 30 ngày,Hỗ trợ video 360°,Huy hiệu "Tin mới",Thống kê lượt xem',
     NOW(), false),

    ('Gói Cao Cấp', 'premium-package', 'Tăng cơ hội cho thuê nhanh', 300000, 30, 3, true,
     'Hiển thị nổi bật,Hỗ trợ không giới hạn hình ảnh,Thời hạn 30 ngày,Hỗ trợ video 360°,Huy hiệu "Tin VIP",Ưu tiên trong tìm kiếm,Thống kê chi tiết,Hỗ trợ ưu tiên 24/7',
     NOW(), false),

    ('Gói VIP', 'vip-package', 'Hiển thị tối đa - Cho thuê cực nhanh', 500000, 30, 4, true,
     'Top đầu trang chủ,Hỗ trợ không giới hạn hình ảnh,Thời hạn 30 ngày,Hỗ trợ video 360°,Huy hiệu "Tin VIP PRO",Ưu tiên tối đa trong tìm kiếm,Thống kê chi tiết,Hỗ trợ ưu tiên 24/7,Quảng cáo banner,Đẩy tin tự động mỗi ngày',
     NOW(), false);


-- =============================================
-- ✅ Done! Sample data inserted successfully
-- =============================================
-- 📊 Summary:
-- - 10 Users (1 admin, 3 hosts, 6 guests)
-- - 24 Properties (20 LONG_TERM phòng trọ, 5 SHORT_TERM apartments)
-- - 10 Categories
-- - 20 Amenities
-- - 39 Property Images
-- - 10 Bookings (5 long-term, 5 short-term)
-- - 8 Reviews
-- - Multiple favorites
--
-- 🔑 All passwords: password123
-- =============================================

