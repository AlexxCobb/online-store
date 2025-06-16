create TABLE product_parameters (
                        product_id BIGINT NOT NULL,
                        param_key VARCHAR(100) NOT NULL,
                        param_value VARCHAR(255) NOT NULL,
                        PRIMARY KEY (product_id, param_key),

CONSTRAINT fk_product_parameters_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

comment on table product_parameters is 'Хранение динамических параметров товаров';
comment on column product_parameters.product_id is 'ID товара';
comment on column product_parameters.param_key is 'Название параметра (например: brand, color, weight)';
comment on column product_parameters.param_value is 'Значение параметра (например: Apple, Black, 0.5kg)';

create index idx_product_parameters_key_value on product_parameters (param_key, param_value);