-- =============================================
-- StayEase Sample Data Script
-- Run this after the application creates tables
-- =============================================

-- =============================================
-- 1. ROLES
-- =============================================
INSERT INTO roles (id, name, description) VALUES 
(1, 'ROLE_USER', 'Regular user role'),
(2, 'ROLE_HOST', 'Property host role'),
(3, 'ROLE_ADMIN', 'Administrator role')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- =============================================
-- 2. CATEGORIES
-- =============================================
INSERT INTO categories (id, name, description, icon, slug, is_active, display_order, created_at, is_deleted) VALUES 
(1, 'Beachfront', 'Properties right on the beach', 'beach_access', 'beachfront', true, 1, NOW(), false),
(2, 'Cabins', 'Cozy cabin retreats', 'cabin', 'cabins', true, 2, NOW(), false),
(3, 'Trending', 'Most popular stays', 'local_fire_department', 'trending', true, 3, NOW(), false),
(4, 'National Parks', 'Near national parks', 'forest', 'national-parks', true, 4, NOW(), false),
(5, 'Amazing Pools', 'Properties with stunning pools', 'pool', 'amazing-pools', true, 5, NOW(), false),
(6, 'Islands', 'Island getaways', 'houseboat', 'islands', true, 6, NOW(), false),
(7, 'Castles', 'Castle stays', 'castle', 'castles', true, 7, NOW(), false),
(8, 'Views', 'Amazing scenic views', 'landscape', 'views', true, 8, NOW(), false),
(9, 'Mountain', 'Mountain retreats', 'terrain', 'mountain', true, 9, NOW(), false),
(10, 'City Center', 'Urban locations', 'location_city', 'city-center', true, 10, NOW(), false)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 3. AMENITIES
-- =============================================
INSERT INTO amenities (id, name, icon, category, is_active, created_at, is_deleted) VALUES 
(1, 'WiFi', 'wifi', 'BASIC', true, NOW(), false),
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
(20, 'Pet Friendly', 'pets', 'FAMILY', true, NOW(), false)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 4. HOST USER (Password: password123)
-- =============================================
INSERT INTO users (id, email, password, first_name, last_name, phone, avatar_url, bio, city, country, is_active, is_verified, is_host, created_at, is_deleted) VALUES 
(1, 'host@stayease.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.ZzBLU1NnZzqg5r1bXi', 'Minh', 'Tran', '+84901234567', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face', 'Superhost with 5 years experience in hospitality. I love sharing beautiful spaces with travelers from around the world.', 'Ho Chi Minh City', 'Vietnam', true, true, true, NOW(), false),
(2, 'user@stayease.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.ZzBLU1NnZzqg5r1bXi', 'Alex', 'Nguyen', '+84907654321', 'https://i.pravatar.cc/150?u=alex', 'Love traveling and exploring new places!', 'Ha Noi', 'Vietnam', true, true, false, NOW(), false)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), (1, 2),  -- Host has USER and HOST roles
(2, 1)           -- Regular user has USER role
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- =============================================
-- 5. PROPERTIES
-- =============================================
INSERT INTO properties (id, title, description, property_type, address, city, state, country, price_per_night, cleaning_fee, service_fee, max_guests, bedrooms, beds, bathrooms, min_nights, max_nights, check_in_time, check_out_time, status, is_instant_book, is_featured, average_rating, total_reviews, view_count, host_id, category_id, created_at, is_deleted) VALUES 
(1, 'Dreamy Mountain Retreat in Da Lat', 'Experience the magic of Da Lat in this stunning mountain retreat. Wake up to breathtaking views of rolling hills and pine forests. Perfect for couples or small families seeking tranquility.', 'VILLA', '123 Pine Valley Road', 'Da Lat', 'Lam Dong', 'Vietnam', 120.00, 25.00, 15.00, 4, 2, 2, 2, 2, 14, '15:00', '11:00', 'ACTIVE', true, true, 4.96, 128, 1250, 1, 9, NOW(), false),

(2, 'Luxury Beachfront Villa in Phu Quoc', 'Indulge in paradise at this stunning beachfront villa. Direct access to pristine white sand beach, private pool, and panoramic ocean views. The ultimate tropical getaway.', 'VILLA', '456 Ocean Drive', 'Phu Quoc', 'Kien Giang', 'Vietnam', 245.00, 50.00, 30.00, 8, 4, 4, 3, 3, 30, '14:00', '12:00', 'ACTIVE', true, true, 4.85, 89, 2100, 1, 1, NOW(), false),

(3, 'Authentic Homestay in Sapa Valley', 'Immerse yourself in the culture of the Vietnamese highlands. This traditional homestay offers stunning valley views, warm hospitality, and authentic local experiences.', 'HOUSE', '789 Valley View Lane', 'Sapa', 'Lao Cai', 'Vietnam', 85.00, 15.00, 10.00, 6, 3, 4, 2, 1, 7, '14:00', '10:00', 'ACTIVE', true, false, 5.00, 156, 3200, 1, 8, NOW(), false),

(4, 'Modern Apartment in Ho Chi Minh City', 'Stay in the heart of Saigon! This sleek, modern apartment offers city views, walking distance to top attractions, restaurants, and nightlife. Perfect for urban explorers.', 'APARTMENT', '101 Nguyen Hue Boulevard', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 65.00, 20.00, 10.00, 2, 1, 1, 1, 1, 30, '15:00', '11:00', 'ACTIVE', true, false, 4.92, 234, 4500, 1, 10, NOW(), false),

(5, 'Charming Riverside House in Hoi An', 'Discover the magic of Hoi An from this beautifully restored riverside house. Traditional architecture meets modern comfort. Just steps from the ancient town and lantern-lit streets.', 'HOUSE', '202 River Road', 'Hoi An', 'Quang Nam', 'Vietnam', 95.00, 20.00, 12.00, 4, 2, 2, 2, 2, 14, '14:00', '11:00', 'ACTIVE', true, false, 4.88, 178, 2800, 1, 8, NOW(), false),

(6, 'Ocean View Condo in Nha Trang', 'Wake up to stunning ocean views in this modern beachside condo. Features a rooftop pool, gym, and direct beach access. Perfect for beach lovers and water sports enthusiasts.', 'CONDO', '303 Beach Boulevard', 'Nha Trang', 'Khanh Hoa', 'Vietnam', 110.00, 25.00, 15.00, 4, 2, 2, 2, 2, 21, '15:00', '11:00', 'ACTIVE', true, false, 4.75, 92, 1800, 1, 1, NOW(), false),

(7, 'Desert Oasis Resort in Mui Ne', 'Escape to this unique desert-meets-ocean paradise. Features stunning views of the famous red sand dunes, private pool, and easy access to world-class kitesurfing beaches.', 'VILLA', '404 Dune Vista Road', 'Mui Ne', 'Binh Thuan', 'Vietnam', 180.00, 40.00, 25.00, 6, 3, 3, 2, 2, 14, '14:00', '12:00', 'ACTIVE', true, false, 4.90, 67, 1500, 1, 8, NOW(), false),

(8, 'Floating House in Ha Long Bay', 'Experience the wonder of Ha Long Bay from this unique floating house. Surrounded by limestone karsts, wake up to the most spectacular sunrise. Includes kayaking and fishing equipment.', 'OTHER', '505 Bay Floating Village', 'Ha Long', 'Quang Ninh', 'Vietnam', 350.00, 75.00, 45.00, 4, 2, 2, 1, 2, 7, '13:00', '10:00', 'ACTIVE', false, true, 4.95, 45, 980, 1, 6, NOW(), false)
ON DUPLICATE KEY UPDATE title = VALUES(title);

-- =============================================
-- 6. PROPERTY IMAGES
-- =============================================
INSERT INTO property_images (id, property_id, image_url, caption, display_order, is_primary, created_at, is_deleted) VALUES 
-- Property 1 - Da Lat
(1, 1, 'https://lh3.googleusercontent.com/aida-public/AB6AXuBnjqLsczraflUeSqAym0kpTUehfR_CPG1p8fkeEu5uyIfw6skunxAhMhZKGjDC7k-qPI9aY2wF7wBLSjkfSe2Kx9yIlzQXCo2Rj80_qBUDmsr7ryCubOd1oKQMrMkir0btGJ_dO3KXDhq5tEQI3y8BnAnOFyI8xnFEzZ5oCYXOT40H8pao5VGzvyc2Z7M63Xa1pUdI9c3MoeTAmkFgYrND2Mi191q9gba-OMBwei4_W38QEXmFEJafBbnT9bCgHMrDwuB_JikldFA', 'Mountain view from the villa', 1, true, NOW(), false),

-- Property 2 - Phu Quoc
(2, 2, 'https://lh3.googleusercontent.com/aida-public/AB6AXuBA1FZXF9cRjf-uEn_FJtCFTrX2defPgNDht71F8NB2_RkRQVbUSeiRC9EUKSu9qd86GlPp421zXb0dUFvMqsUNc5Ro4JDRuxOkEM_DMoau6u3Q51wzcHXB7wEeBoZ9M6b_0QgrJ7ZwCAp4f0KjxXu2xF0VMUVLnk73lLIb0LlgWhWJrYYl2jUEEeTC3TchqGvoE9TNhJH1hVjKVMGdTSYMWP4EthwfXHr8OQxUwNbZJ6YbaKNcPE5kRvAGlbtZk3hv6uaQFhBMKko', 'Beachfront paradise', 1, true, NOW(), false),

-- Property 3 - Sapa
(3, 3, 'https://lh3.googleusercontent.com/aida-public/AB6AXuBTQpZ0qWi79XXuek9pIlqRdyQle44MpIC4MPiOZwG1S8DaREHq09XY6gCSvoZxJUiYkyeVww8vJ5FNHJyNLJ43rtMHzfxNqLdx-3nX6uiplCQWeeZSXf832KyngQsMdxYeBum0pDsH-hq0BSL_erjOFAjn4QXrxUINtqKqImyFUhwPws4P7KK69JvhqpTIrYjZmB5YZtdsg-W88clnR59KKyiKr2RQ7m3BI_SVToy5j1EqhPo5krMW5FtdvfOhrkbfkdeV_XqlT6w', 'Valley view homestay', 1, true, NOW(), false),

-- Property 4 - Ho Chi Minh City
(4, 4, 'https://lh3.googleusercontent.com/aida-public/AB6AXuCfHFPEUeLrSrmADCqhKLP6c39B77V86_0USR3lvrqmMQoMVOILh7wmgIPRsjP04Qwlz9ej7nakucdAoby9hifdWOM1ShDiXXot3C4ikjVnsyl5nszx762C8mq3Ka_8tvH8qTi4WyA2ZWLLCwYlveGNT4ly5Ag3GZKRZMQfU49bO9qbnOs2R38Mho12xZRthskCIrOvO_TTphkw1WXKu5KVOwx8rffOcpYpZMltOCog-ScOAGn40haeGGTy9i2nIEPegf_iPwHPap4', 'Modern city apartment', 1, true, NOW(), false),

-- Property 5 - Hoi An
(5, 5, 'https://lh3.googleusercontent.com/aida-public/AB6AXuBTNpto8pxngd8vyKVCJXV0TC5whJT6-0Z6u-Zj3yuyz4BpsszyGCdb9iL-Vd40E9rgdRbmw0oNxZ3Hk3iJAFYI5tRgcu-HibX2VTTEBadJeJrxjZidXzq_B--pRqBeVYSZrq3a6zg4dy2hjm8cAgK8ZEswngk0cm6dTZabM9xUPN9NO8sMqITQokpLHxcYarHXKjYv4iB-mm7AiXOuuq7patQFW31wZutIyXHiS3nNGcQS207p3oeTARklUrITpOr9n9RFhTglCMs', 'Riverside charm', 1, true, NOW(), false),

-- Property 6 - Nha Trang
(6, 6, 'https://lh3.googleusercontent.com/aida-public/AB6AXuDDsfGYfrut18yfxTaFwKmV01f31CCBoVp2cG9nsOyf_S-I4Xe4WTPL3hTnYEtFkM-dCB6jATXnv85X0tulBS5WH4MPwGaD0AcMC_mKEdq8MLtNHY0HZKOqwyJUzCpU0BGKxOMOo_M-GW1V84OfrxekctjtiBME_A6MUtLxCwWQUeEjQEu111ub8ZEkhg2jK1NLtybGKqpWUbCsx7oUDIzSikwounRwUjYTRn09M6sNGyxndxI65LFkqacjp0R1XAdiLjXsf1LRieg', 'Ocean view condo', 1, true, NOW(), false),

-- Property 7 - Mui Ne
(7, 7, 'https://lh3.googleusercontent.com/aida-public/AB6AXuB1lMcWUk2UErDRIIzvp-UBtPJahNbs7gt7NaHH0u2Ium7mOvpwehlizJbV366l18RwejhafK4pjqPjKzbB3XcTLnqm0c9RORa4ZIfhtAM2EkWJzaKW8Av82IORxKjXlhISo21niyQ6iANfnwVLhAu9cJJ91y75_BoXz1m8FmV8NOLAZ0ci7aKZh75_FbxAd729us9jLYPz-VW1AYCVCb7KvMUufi-R-RK_d_ljFk9F0u7pUy9GEuuIqVQmMlensWuq83AZfskPSAU', 'Desert oasis view', 1, true, NOW(), false),

-- Property 8 - Ha Long Bay
(8, 8, 'https://lh3.googleusercontent.com/aida-public/AB6AXuBlsw4rBemWKd_LbEvogWkTZIo6pGUTErlgBmiOLtAuYnQ60LnQuUI1pqrJayUE0bz_DUHZDFlQFXpvbMseuwkhex0Nj9-ymU6wlaxafE3DVyZpJXcanjFK81FmEzWcYnayLrmTCUMzSzdVefgcf7gLvponyzFRGdgk7X9Xh9scx7bz_JMP3IoB4NqlX-ZUXKEvSf9jL3lF6t3AZYHV737J55J7F7bIo8tg1qC3ThI9kDnN5s9dpO1s6pzaKIlHYnLn6of8tCTeqNU', 'Floating house in Ha Long Bay', 1, true, NOW(), false)
ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

-- =============================================
-- 7. PROPERTY AMENITIES
-- =============================================
-- Da Lat villa amenities
INSERT INTO property_amenities (property_id, amenity_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 12), (1, 14), (1, 16), (1, 19),
-- Phu Quoc villa amenities
(2, 1), (2, 2), (2, 3), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10), (2, 13), (2, 14), (2, 17),
-- Sapa homestay amenities
(3, 1), (3, 4), (3, 5), (3, 12), (3, 15), (3, 16),
-- HCM apartment amenities
(4, 1), (4, 2), (4, 3), (4, 5), (4, 6), (4, 11), (4, 18), (4, 19),
-- Hoi An house amenities
(5, 1), (5, 2), (5, 3), (5, 5), (5, 14), (5, 15), (5, 19),
-- Nha Trang condo amenities
(6, 1), (6, 2), (6, 3), (6, 5), (6, 9), (6, 11), (6, 14), (6, 17),
-- Mui Ne villa amenities
(7, 1), (7, 2), (7, 3), (7, 5), (7, 8), (7, 9), (7, 13), (7, 14), (7, 17),
-- Ha Long floating house amenities
(8, 1), (8, 5), (8, 17)
ON DUPLICATE KEY UPDATE property_id = VALUES(property_id);

-- =============================================
-- Done! Sample data inserted successfully
-- =============================================

