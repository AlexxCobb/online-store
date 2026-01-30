
-- КАТЕГОРИЯ: СМАРТФОНЫ

INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'iPhone 16 Pro MAX (512GB)', 110000, (SELECT category_id FROM "category" WHERE category_name = 'Смартфоны'), 187, 0.05, 30, '/images/products/iphone-16-pro-max-gold.jpg', 'SMARTPHONE_FINGERPRINT_A_512', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_A_512'), 'brand', 'Apple' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_A_512'), 'color', 'Золотистый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_A_512'), 'ram', '8' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_A_512'), 'memory', '512';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Samsung S25 Ultra (512GB)', 120000, (SELECT category_id FROM "category" WHERE category_name = 'Смартфоны'), 232, 0.06, 50, '/images/products/Samsung-Galaxy-S25-Ultra-silver.jpg', 'SMARTPHONE_FINGERPRINT_B_512', TRUE, 100000, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_B_512'), 'brand', 'Samsung' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_B_512'), 'color', 'Серебристый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_B_512'), 'ram', '12' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_B_512'), 'memory', '512';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), '14 Ultra (256GB)', 70000, (SELECT category_id FROM "category" WHERE category_name = 'Смартфоны'), 193, 0.05, 20, '/images/products/xiaomi-14-ultra.png', 'SMARTPHONE_FINGERPRINT_C_256', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_C_256'), 'brand', 'Xiaomi' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_C_256'), 'color', 'Черный' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_C_256'), 'ram', '12' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_C_256'), 'memory', '256';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Pixel 9 (128GB)', 74000, (SELECT category_id FROM "category" WHERE category_name = 'Смартфоны'), 187, 0.05, 10, '/images/products/Google-Pixel-9.jpg', 'SMARTPHONE_FINGERPRINT_D_PXL8', TRUE, 60000, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_D_PXL8'), 'brand', 'Google' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_D_PXL8'), 'color', 'Черный' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_D_PXL8'), 'ram', '8' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_D_PXL8'), 'memory', '128';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'OnePlus 12 (512GB)', 89000, (SELECT category_id FROM "category" WHERE category_name = 'Смартфоны'), 220, 0.06, 17, '/images/products/onePlus-12-green.png', 'SMARTPHONE_FINGERPRINT_E_OP12', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_E_OP12'), 'brand', 'OnePlus' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_E_OP12'), 'color', 'Зеленый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_E_OP12'), 'ram', '16' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'SMARTPHONE_FINGERPRINT_E_OP12'), 'memory', '512';


-- КАТЕГОРИЯ: НОУТБУКИ

INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'MacBook Pro 14" M3', 200000, (SELECT category_id FROM "category" WHERE category_name = 'Ноутбуки'), 1550, 0.4, 15, '/images/products/Apple-MacBook-Pro-14.png', 'LAPTOP_FINGERPRINT_A_MBP', TRUE, 170000, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_A_MBP'), 'brand', 'Apple' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_A_MBP'), 'color', 'Серебристый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_A_MBP'), 'ram', '16' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_A_MBP'), 'memory', '512';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'XPS 15', 175000, (SELECT category_id FROM "category" WHERE category_name = 'Ноутбуки'), 1850, 0.45, 45, '/images/products/Dell-XPS-15.jpg', 'LAPTOP_FINGERPRINT_B_XPS', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_B_XPS'), 'brand', 'Dell' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_B_XPS'), 'color', 'Серебристый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_B_XPS'), 'ram', '32' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_B_XPS'), 'memory', '1024';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'ThinkPad X1', 140000, (SELECT category_id FROM "category" WHERE category_name = 'Ноутбуки'), 1150, 0.3, 70, '/images/products/ThinkPad-X1.jpg', 'LAPTOP_FINGERPRINT_C_TINK', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_C_TINK'), 'brand', 'Lenovo' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_C_TINK'), 'color', 'Черный' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_C_TINK'), 'ram', '16' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_C_TINK'), 'memory', '512';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'ROG Zephyrus', 250000, (SELECT category_id FROM "category" WHERE category_name = 'Ноутбуки'), 2200, 0.5, 30, '/images/products/ROG-Zephyrus.jpg', 'LAPTOP_FINGERPRINT_D_ROG', TRUE, 220000, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_D_ROG'), 'brand', 'ASUS' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_D_ROG'), 'color', 'Белый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_D_ROG'), 'ram', '64' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_D_ROG'), 'memory', '2054';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Spectre x360', 160000, (SELECT category_id FROM "category" WHERE category_name = 'Ноутбуки'), 1300, 0.35, 55, '/images/products/HPSpectreX360.jpg', 'LAPTOP_FINGERPRINT_E_HP', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_E_HP'), 'brand', 'HP' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_E_HP'), 'color', 'Черный' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_E_HP'), 'ram', '16' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'LAPTOP_FINGERPRINT_E_HP'), 'memory', '1024';


-- КАТЕГОРИЯ: ПЛАНШЕТЫ

INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'iPad Pro 11"', 79900, (SELECT category_id FROM "category" WHERE category_name = 'Планшеты'), 466, 0.03, 80, '/images/products/apple-ipad-pro-11.jpg', 'TABLET_FINGERPRINT_A', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_A'), 'brand', 'Apple' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_A'), 'color', 'Серый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_A'), 'ram', '8' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_A'), 'memory', '256';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Galaxy Tab S9', 65000, (SELECT category_id FROM "category" WHERE category_name = 'Планшеты'), 499, 0.04, 60, '/images/products/Galaxy-Tab-S9.jpg', 'TABLET_FINGERPRINT_B', TRUE, 54999, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_B'), 'brand', 'Samsung' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_B'), 'color', 'Черый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_B'), 'ram', '12' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_B'), 'memory', '512';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Xiaomi Pad 6', 39900, (SELECT category_id FROM "category" WHERE category_name = 'Планшеты'), 490, 0.03, 110, '/images/products/Xiaomi-Pad-6.jpg', 'TABLET_FINGERPRINT_C', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_C'), 'brand', 'Xiaomi' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_C'), 'color', 'Золотистый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_C'), 'ram', '8' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_C'), 'memory', '256';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Surface Pro', 99900, (SELECT category_id FROM "category" WHERE category_name = 'Планшеты'), 770, 0.05, 40, '/images/products/Microsoft-Surface-Pro.png', 'TABLET_FINGERPRINT_D', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_D'), 'brand', 'Microsoft' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_D'), 'color', 'Серебристый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_D'), 'ram', '16' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_D'), 'memory', '512';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Lenovo Tab P11', 24000, (SELECT category_id FROM "category" WHERE category_name = 'Планшеты'), 520, 0.03, 150, '/images/products/Lenovo-Tab-P11.jpg', 'TABLET_FINGERPRINT_E', TRUE, 18999, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_E'), 'brand', 'Lenovo' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_E'), 'color', 'Серый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_E'), 'ram', '6' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'TABLET_FINGERPRINT_E'), 'memory', '128';


-- КАТЕГОРИЯ: ИГРОВЫЕ ПРИСТАВКИ

INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'PlayStation 5 Slim', 49900, (SELECT category_id FROM "category" WHERE category_name = 'Игровые приставки'), 3200, 0.7, 40, '/images/products/PlayStation-5-Slim.jpg', 'CONSOLE_FINGERPRINT_A', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_A'), 'brand', 'Sony' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_A'), 'color', 'Белый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_A'), 'ram', '16' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_A'), 'memory', '825';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Xbox Series X', 49900, (SELECT category_id FROM "category" WHERE category_name = 'Игровые приставки'), 4450, 0.08, 35, '/images/products/Xbox-Series-X.jpg', 'CONSOLE_FINGERPRINT_B', TRUE, 44000, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_B'), 'brand', 'Microsoft' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_B'), 'color', 'Белый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_B'), 'ram', '16' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_B'), 'memory', '1024';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Switch OLED', 34900, (SELECT category_id FROM "category" WHERE category_name = 'Игровые приставки'), 420, 0.01, 25, '/images/products/Nintendo-Switch-OLED.jpeg', 'CONSOLE_FINGERPRINT_C', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_C'), 'brand', 'Nintendo' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_C'), 'color', 'Белый' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_C'), 'ram', '4' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_C'), 'memory', '64';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Steam Deck OLED', 64900, (SELECT category_id FROM "category" WHERE category_name = 'Игровые приставки'), 640, 0.02, 50, '/images/products/Steam-Deck-OLED.png', 'CONSOLE_FINGERPRINT_D', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_D'), 'brand', 'Valve' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_D'), 'color', 'Black' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_D'), 'ram', '16' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_D'), 'memory', '512';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'PlayStation 5 Portal', 19900, (SELECT category_id FROM "category" WHERE category_name = 'Игровые приставки'), 540, 0.01, 60, '/images/products/PlayStation-5-Portal.jpg', 'CONSOLE_FINGERPRINT_E', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_E'), 'brand', 'Sony' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'CONSOLE_FINGERPRINT_E'), 'color', 'Белый';


-- КАТЕГОРИЯ: НАУШНИКИ

INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Apple AirPods Pro 3', 31999, (SELECT category_id FROM "category" WHERE category_name = 'Наушники'), 50, 0.01, 200, '/images/products/AirPods-Pro-3.jpg', 'HEADPHONE_FINGERPRINT_A', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_A'), 'brand', 'Apple' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_A'), 'color', 'Белый';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'WH-1000XM5', 39900, (SELECT category_id FROM "category" WHERE category_name = 'Наушники'), 250, 0.02, 100, '/images/products/Sony-WH-1000XM5.jpg', 'HEADPHONE_FINGERPRINT_B', TRUE, 34900, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_B'), 'brand', 'Sony' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_B'), 'color', 'Синий';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'QuietComfort Ultra', 42900, (SELECT category_id FROM "category" WHERE category_name = 'Наушники'), 280, 0.02, 90, '/images/products/Bose-QuietComfort-Ultra.jpg', 'HEADPHONE_FINGERPRINT_C', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_C'), 'brand', 'Bose' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_C'), 'color', 'Черный';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Tune 510BT', 5100, (SELECT category_id FROM "category" WHERE category_name = 'Наушники'), 160, 0.01, 300, '/images/products/JBL-Tune-510BT.png', 'HEADPHONE_FINGERPRINT_D', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_D'), 'brand', 'JBL' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_D'), 'color', 'Черный';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Momentum 4', 37900, (SELECT category_id FROM "category" WHERE category_name = 'Наушники'), 290, 0.25, 85, '/images/products/Sennheiser-Momentum-4.jpg', 'HEADPHONE_FINGERPRINT_E', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_E'), 'brand', 'Sennheiser' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'HEADPHONE_FINGERPRINT_E'), 'color', 'Бежевый';


-- КАТЕГОРИЯ: УМНЫЕ ЧАСЫ

INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Watch Series 9', 39900, (SELECT category_id FROM "category" WHERE category_name = 'Умные часы'), 40, 0.01, 120, '/images/products/Apple-Watch-Series-9.png', 'WATCH_FINGERPRINT_A', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_A'), 'brand', 'Apple' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_A'), 'color', 'Серебристый';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Galaxy Watch 6', 29900, (SELECT category_id FROM "category" WHERE category_name = 'Умные часы'), 35, 0.01, 90, '/images/products/Galaxy-Watch-6.png', 'WATCH_FINGERPRINT_B', TRUE, 27000, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_B'), 'brand', 'Samsung' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_B'), 'color', 'Черный';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Fenix 7', 69900, (SELECT category_id FROM "category" WHERE category_name = 'Умные часы'), 75, 0.02, 50, '/images/products/Garmin-Fenix-7.jpg', 'WATCH_FINGERPRINT_C', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_C'), 'brand', 'Garmin' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_C'), 'color', 'Черный';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Watch GT 5 Pro', 35900, (SELECT category_id FROM "category" WHERE category_name = 'Умные часы'), 48, 0.01, 80, '/images/products/Watch-GT-5-pro.jpg', 'WATCH_FINGERPRINT_D', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_D'), 'brand', 'Huawei' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_D'), 'color', 'Серебристый';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Watch 2 Pro', 25000, (SELECT category_id FROM "category" WHERE category_name = 'Умные часы'), 52, 0.01, 100, '/images/products/Xiaomi-Watch-2-Pro.jpg', 'WATCH_FINGERPRINT_E', TRUE, 20000, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_E'), 'brand', 'Xiaomi' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'WATCH_FINGERPRINT_E'), 'color', 'Коричневый';


-- КАТЕГОРИЯ: АКСЕССУАРЫ

INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'MX Master 3S', 9900, (SELECT category_id FROM "category" WHERE category_name = 'Аксессуары'), 141, 0.05, 200, '/images/products/Logitech-MX-Master-3S.jpg', 'ACC_FINGERPRINT_A', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_A'), 'brand', 'Logitech' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_A'), 'color', 'Черный';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'Magic Keyboard', 12900, (SELECT category_id FROM "category" WHERE category_name = 'Аксессуары'), 350, 0.01, 150, '/images/products/Apple-Magic-Keyboard.jpg', 'ACC_FINGERPRINT_B', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_B'), 'brand', 'Apple' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_B'), 'color', 'Белый';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), '45W Charger', 4900, (SELECT category_id FROM "category" WHERE category_name = 'Аксессуары'), 100, 0.02, 300, '/images/products/Samsung-45W-Charger.jpg', 'ACC_FINGERPRINT_C', TRUE, 3900, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_C'), 'brand', 'Samsung' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_C'), 'color', 'Черный';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'USB Hub', 2500, (SELECT category_id FROM "category" WHERE category_name = 'Аксессуары'), 80, 0.01, 400, '/images/products/TP-Link-USB-Hub.jpg', 'ACC_FINGERPRINT_D', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_D'), 'brand', 'TP-Link' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_D'), 'color', 'Silver';


INSERT INTO "product" (product_public_id, product_name, product_price, category_id, product_weight, product_volume, product_stock_quantity, image_path, product_fingerprint, is_discount, discount_price, created_at)
SELECT gen_random_uuid(), 'GoPro Head Strap', 3999, (SELECT category_id FROM "category" WHERE category_name = 'Аксессуары'), 90, 0.003, 80, '/images/products/GoPro-Head-Strap.jpg', 'ACC_FINGERPRINT_E', FALSE, NULL, NOW();

INSERT INTO product_parameters (product_id, param_key, param_value)
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_E'), 'brand', 'GoPro' UNION ALL
SELECT (SELECT product_id FROM product WHERE product_fingerprint = 'ACC_FINGERPRINT_E'), 'color', 'Black';