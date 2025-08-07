-- Customer Service için örnek müşteriler
INSERT INTO customers (name, email, phone, address, has_order_permission, is_active, created_at, updated_at) VALUES
('Ahmet Yılmaz', 'ahmet.yilmaz@example.com', '05551234567', 'İstanbul, Kadıköy', true, true, NOW(), NOW()),
('Fatma Demir', 'fatma.demir@example.com', '05559876543', 'Ankara, Çankaya', true, true, NOW(), NOW()),
('Mehmet Kaya', 'mehmet.kaya@example.com', '05551112233', 'İzmir, Konak', false, true, NOW(), NOW()),
('Ayşe Özkan', 'ayse.ozkan@example.com', '05554445566', 'Bursa, Nilüfer', true, true, NOW(), NOW()),
('Ali Çelik', 'ali.celik@example.com', '05557778899', 'Antalya, Muratpaşa', false, true, NOW(), NOW()),
('Zeynep Arslan', 'zeynep.arslan@example.com', '05556667788', 'Adana, Seyhan', true, true, NOW(), NOW()); 