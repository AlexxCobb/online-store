ALTER TABLE cart_item
DROP CONSTRAINT fk_cart_item_cart, ADD CONSTRAINT fk_cart_item_cart
FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE;
