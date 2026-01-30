ALTER TABLE product
    ADD COLUMN image_path VARCHAR(255);

COMMENT ON COLUMN product.image_path IS 'Путь расположения изображения товара';