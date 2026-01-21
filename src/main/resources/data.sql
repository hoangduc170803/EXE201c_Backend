-- =============================================
-- StayEase Database Initialization Script
-- =============================================

-- =============================================
-- 1. ROLES
-- =============================================
INSERT INTO roles (name, description) VALUES 
('ROLE_USER', 'Regular user - can browse and book properties'),
('ROLE_HOST', 'Host - can list and manage properties'),
('ROLE_ADMIN', 'Administrator - full system access')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- =============================================
-- 2. CATEGORIES
-- =============================================
INSERT INTO categories (name, description, icon, slug, display_order, is_active, is_deleted, created_at, updated_at) VALUES 
('House', 'Entire houses for your stay', 'home', 'house', 1, true, false, NOW(), NOW()),
('Apartment', 'Cozy apartments in the city', 'building', 'apartment', 2, true, false, NOW(), NOW()),
('Villa', 'Luxury villas with premium amenities', 'castle', 'villa', 3, true, false, NOW(), NOW()),
('Condo', 'Modern condominiums', 'building-2', 'condo', 4, true, false, NOW(), NOW()),
('Cabin', 'Rustic cabins in nature', 'trees', 'cabin', 5, true, false, NOW(), NOW()),
('Cottage', 'Charming countryside cottages', 'home-2', 'cottage', 6, true, false, NOW(), NOW()),
('Beach House', 'Properties near the beach', 'waves', 'beach-house', 7, true, false, NOW(), NOW()),
('Mountain Retreat', 'Mountain getaways', 'mountain', 'mountain-retreat', 8, true, false, NOW(), NOW()),
('Farmhouse', 'Rural farm experiences', 'tractor', 'farmhouse', 9, true, false, NOW(), NOW()),
('Penthouse', 'Luxury penthouses with views', 'building-skyscraper', 'penthouse', 10, true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    description = VALUES(description),
    icon = VALUES(icon),
    display_order = VALUES(display_order);

-- =============================================
-- 3. AMENITIES
-- =============================================
-- Basic Amenities
INSERT INTO amenities (name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
('WiFi', 'High-speed internet connection', 'wifi', 'BASIC', true, false, NOW(), NOW()),
('TV', 'Flat-screen TV with cable', 'tv', 'BASIC', true, false, NOW(), NOW()),
('Kitchen', 'Full kitchen access', 'utensils', 'BASIC', true, false, NOW(), NOW()),
('Washer', 'Washing machine available', 'shirt', 'BASIC', true, false, NOW(), NOW()),
('Dryer', 'Clothes dryer available', 'wind', 'BASIC', true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Bathroom Amenities
INSERT INTO amenities (name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
('Hair dryer', 'Hair dryer available', 'wind', 'BATHROOM', true, false, NOW(), NOW()),
('Hot water', 'Hot water supply 24/7', 'droplet', 'BATHROOM', true, false, NOW(), NOW()),
('Shampoo', 'Complimentary shampoo', 'bottle', 'BATHROOM', true, false, NOW(), NOW()),
('Towels', 'Fresh towels provided', 'bath', 'BATHROOM', true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Safety Amenities
INSERT INTO amenities (name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
('Smoke alarm', 'Smoke detector installed', 'bell', 'SAFETY', true, false, NOW(), NOW()),
('Fire extinguisher', 'Fire safety equipment', 'flame', 'SAFETY', true, false, NOW(), NOW()),
('First aid kit', 'Medical supplies available', 'heart-pulse', 'SAFETY', true, false, NOW(), NOW()),
('Carbon monoxide alarm', 'CO detector installed', 'alert-triangle', 'SAFETY', true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Outdoor Amenities
INSERT INTO amenities (name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
('Pool', 'Swimming pool access', 'waves', 'OUTDOOR', true, false, NOW(), NOW()),
('BBQ grill', 'Outdoor barbecue grill', 'flame', 'OUTDOOR', true, false, NOW(), NOW()),
('Patio', 'Outdoor seating area', 'armchair', 'OUTDOOR', true, false, NOW(), NOW()),
('Garden', 'Private garden access', 'flower', 'OUTDOOR', true, false, NOW(), NOW()),
('Balcony', 'Private balcony', 'door-open', 'OUTDOOR', true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Parking Amenities
INSERT INTO amenities (name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
('Free parking', 'Free parking on premises', 'car', 'PARKING', true, false, NOW(), NOW()),
('Garage', 'Covered garage parking', 'warehouse', 'PARKING', true, false, NOW(), NOW()),
('Street parking', 'Street parking available', 'parking-circle', 'PARKING', true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Heating & Cooling Amenities
INSERT INTO amenities (name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
('Air conditioning', 'Central air conditioning', 'snowflake', 'HEATING_COOLING', true, false, NOW(), NOW()),
('Heating', 'Central heating system', 'thermometer', 'HEATING_COOLING', true, false, NOW(), NOW()),
('Ceiling fan', 'Ceiling fan in rooms', 'fan', 'HEATING_COOLING', true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Entertainment Amenities
INSERT INTO amenities (name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
('Game console', 'Video game console', 'gamepad', 'ENTERTAINMENT', true, false, NOW(), NOW()),
('Books', 'Reading library available', 'book-open', 'ENTERTAINMENT', true, false, NOW(), NOW()),
('Board games', 'Board games collection', 'puzzle', 'ENTERTAINMENT', true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Family Amenities
INSERT INTO amenities (name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
('Crib', 'Baby crib available', 'baby', 'FAMILY', true, false, NOW(), NOW()),
('High chair', 'Baby high chair', 'armchair', 'FAMILY', true, false, NOW(), NOW()),
('Children toys', 'Toys for children', 'blocks', 'FAMILY', true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Accessibility Amenities
INSERT INTO amenities (name, description, icon, category, is_active, is_deleted, created_at, updated_at) VALUES 
('Wheelchair accessible', 'Wheelchair accessible entrance', 'accessibility', 'ACCESSIBILITY', true, false, NOW(), NOW()),
('Elevator', 'Elevator access', 'arrow-up-down', 'ACCESSIBILITY', true, false, NOW(), NOW()),
('Wide doorways', 'Wide doorways for accessibility', 'door-open', 'ACCESSIBILITY', true, false, NOW(), NOW())
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- =============================================
-- DONE
-- =============================================
SELECT 'Data initialization completed!' AS status;

