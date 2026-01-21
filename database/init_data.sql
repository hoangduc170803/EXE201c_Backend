-- =============================================
-- StayEase Database Initialization Script
-- Run this file directly in MySQL to seed data
-- =============================================

-- Use the database
USE stayease_db;

-- =============================================
-- 1. ROLES
-- =============================================
INSERT IGNORE INTO roles (id, name, description) VALUES 
(1, 'ROLE_USER', 'Regular user - can browse and book properties'),
(2, 'ROLE_HOST', 'Host - can list and manage properties'),
(3, 'ROLE_ADMIN', 'Administrator - full system access');

-- =============================================
-- 2. CATEGORIES  
-- =============================================
INSERT IGNORE INTO categories (id, name, description, icon, slug, display_order, is_active, is_deleted, created_at, updated_at) VALUES 
(1, 'House', 'Entire houses for your stay', 'home', 'house', 1, 1, 0, NOW(), NOW()),
(2, 'Apartment', 'Cozy apartments in the city', 'building', 'apartment', 2, 1, 0, NOW(), NOW()),
(3, 'Villa', 'Luxury villas with premium amenities', 'castle', 'villa', 3, 1, 0, NOW(), NOW()),
(4, 'Condo', 'Modern condominiums', 'building-2', 'condo', 4, 1, 0, NOW(), NOW()),
(5, 'Cabin', 'Rustic cabins in nature', 'trees', 'cabin', 5, 1, 0, NOW(), NOW()),
(6, 'Cottage', 'Charming countryside cottages', 'home-2', 'cottage', 6, 1, 0, NOW(), NOW()),
(7, 'Beach House', 'Properties near the beach', 'waves', 'beach-house', 7, 1, 0, NOW(), NOW()),
(8, 'Mountain Retreat', 'Mountain getaways', 'mountain', 'mountain-retreat', 8, 1, 0, NOW(), NOW()),
(9, 'Farmhouse', 'Rural farm experiences', 'tractor', 'farmhouse', 9, 1, 0, NOW(), NOW()),
(10, 'Penthouse', 'Luxury penthouses with views', 'building-skyscraper', 'penthouse', 10, 1, 0, NOW(), NOW());

-- =============================================
-- 3. AMENITIES
-- =============================================
INSERT IGNORE INTO amenities (id, name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
-- Basic
(1, 'WiFi', 'High-speed internet connection', 'wifi', 'BASIC', 1, 0, NOW(), NOW()),
(2, 'TV', 'Flat-screen TV with cable', 'tv', 'BASIC', 1, 0, NOW(), NOW()),
(3, 'Kitchen', 'Full kitchen access', 'utensils', 'BASIC', 1, 0, NOW(), NOW()),
(4, 'Washer', 'Washing machine available', 'shirt', 'BASIC', 1, 0, NOW(), NOW()),
(5, 'Dryer', 'Clothes dryer available', 'wind', 'BASIC', 1, 0, NOW(), NOW()),

-- Bathroom
(6, 'Hair dryer', 'Hair dryer available', 'wind', 'BATHROOM', 1, 0, NOW(), NOW()),
(7, 'Hot water', 'Hot water supply 24/7', 'droplet', 'BATHROOM', 1, 0, NOW(), NOW()),
(8, 'Shampoo', 'Complimentary shampoo', 'bottle', 'BATHROOM', 1, 0, NOW(), NOW()),
(9, 'Towels', 'Fresh towels provided', 'bath', 'BATHROOM', 1, 0, NOW(), NOW()),

-- Safety
(10, 'Smoke alarm', 'Smoke detector installed', 'bell', 'SAFETY', 1, 0, NOW(), NOW()),
(11, 'Fire extinguisher', 'Fire safety equipment', 'flame', 'SAFETY', 1, 0, NOW(), NOW()),
(12, 'First aid kit', 'Medical supplies available', 'heart-pulse', 'SAFETY', 1, 0, NOW(), NOW()),
(13, 'Carbon monoxide alarm', 'CO detector installed', 'alert-triangle', 'SAFETY', 1, 0, NOW(), NOW()),

-- Outdoor
(14, 'Pool', 'Swimming pool access', 'waves', 'OUTDOOR', 1, 0, NOW(), NOW()),
(15, 'BBQ grill', 'Outdoor barbecue grill', 'flame', 'OUTDOOR', 1, 0, NOW(), NOW()),
(16, 'Patio', 'Outdoor seating area', 'armchair', 'OUTDOOR', 1, 0, NOW(), NOW()),
(17, 'Garden', 'Private garden access', 'flower', 'OUTDOOR', 1, 0, NOW(), NOW()),
(18, 'Balcony', 'Private balcony', 'door-open', 'OUTDOOR', 1, 0, NOW(), NOW()),

-- Parking
(19, 'Free parking', 'Free parking on premises', 'car', 'PARKING', 1, 0, NOW(), NOW()),
(20, 'Garage', 'Covered garage parking', 'warehouse', 'PARKING', 1, 0, NOW(), NOW()),
(21, 'Street parking', 'Street parking available', 'parking-circle', 'PARKING', 1, 0, NOW(), NOW()),

-- Heating & Cooling
(22, 'Air conditioning', 'Central air conditioning', 'snowflake', 'HEATING_COOLING', 1, 0, NOW(), NOW()),
(23, 'Heating', 'Central heating system', 'thermometer', 'HEATING_COOLING', 1, 0, NOW(), NOW()),
(24, 'Ceiling fan', 'Ceiling fan in rooms', 'fan', 'HEATING_COOLING', 1, 0, NOW(), NOW()),

-- Entertainment
(25, 'Game console', 'Video game console', 'gamepad', 'ENTERTAINMENT', 1, 0, NOW(), NOW()),
(26, 'Books', 'Reading library available', 'book-open', 'ENTERTAINMENT', 1, 0, NOW(), NOW()),
(27, 'Board games', 'Board games collection', 'puzzle', 'ENTERTAINMENT', 1, 0, NOW(), NOW()),

-- Family
(28, 'Crib', 'Baby crib available', 'baby', 'FAMILY', 1, 0, NOW(), NOW()),
(29, 'High chair', 'Baby high chair', 'armchair', 'FAMILY', 1, 0, NOW(), NOW()),
(30, 'Children toys', 'Toys for children', 'blocks', 'FAMILY', 1, 0, NOW(), NOW()),

-- Accessibility
(31, 'Wheelchair accessible', 'Wheelchair accessible entrance', 'accessibility', 'ACCESSIBILITY', 1, 0, NOW(), NOW()),
(32, 'Elevator', 'Elevator access', 'arrow-up-down', 'ACCESSIBILITY', 1, 0, NOW(), NOW()),
(33, 'Wide doorways', 'Wide doorways for accessibility', 'door-open', 'ACCESSIBILITY', 1, 0, NOW(), NOW());

-- =============================================
-- VERIFY DATA
-- =============================================
SELECT 'Roles inserted:' AS info, COUNT(*) AS count FROM roles;
SELECT 'Categories inserted:' AS info, COUNT(*) AS count FROM categories;
SELECT 'Amenities inserted:' AS info, COUNT(*) AS count FROM amenities;

SELECT '✅ Data initialization completed!' AS status;

