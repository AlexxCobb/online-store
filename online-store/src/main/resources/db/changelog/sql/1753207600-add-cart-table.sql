create TABLE
    cart (
        cart_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        cart_public_id VARCHAR(255) NOT NULL UNIQUE,
        user_id BIGINT,

        CONSTRAINT fk_cart_user_id FOREIGN KEY (user_id) REFERENCES "user"(user_id)
    );

comment on table cart is 'Корзина для товаров, созданная пользователем';
comment on column cart.cart_id is 'ID корзины';
comment on column cart.cart_public_id is 'Публичный ID корзины';
comment on column cart.user_id is 'ID пользователя';