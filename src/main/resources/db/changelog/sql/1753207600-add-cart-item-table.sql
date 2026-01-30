create TABLE
    cart_item (
        cart_item_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        product_id BIGINT NOT NULL,
        cart_id BIGINT NOT NULL,
        quantity INTEGER NOT NULL,

        CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES product(product_id),
        CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart(cart_id)
    );

comment on table cart_item is 'Таблица товаров, находящихся в корзине';
comment on column cart_item.cart_item_id is 'ID записи';
comment on column cart_item.product_id is 'ID продукта';
comment on column cart_item.cart_id is 'ID корзины';
comment on column cart_item.quantity is 'Количество товара в корзине';