ALTER TABLE product
ADD CONSTRAINT check_product_stock_quantity CHECK (product_stock_quantity >= 0);