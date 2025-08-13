create TABLE
    customer_statistic (
        customer_statistic_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        user_id BIGINT NOT NULL,
        order_id BIGINT NOT NULL,
        total_spent numeric(10,2) NOT NULL,
        created_at TIMESTAMPTZ NOT NULL,

        CONSTRAINT fk_customer_statistic_user_id FOREIGN KEY (user_id) REFERENCES "user"(user_id),
        CONSTRAINT fk_customer_statistic_order FOREIGN KEY (order_id) REFERENCES "order"(order_id)
    );

comment on table customer_statistic is 'Таблица статистики клиентских заказов';
comment on column customer_statistic.customer_statistic_id is 'ID статистики клиентского заказа';
comment on column customer_statistic.user_id is 'ID пользователя';
comment on column customer_statistic.order_id is 'ID заказа';
comment on column customer_statistic.total_spent is 'Общая сумма заказа';
comment on column customer_statistic.created_at is 'Дата и время создания записи';

create index idx_total_spent on customer_statistic (total_spent);
create index idx_created_at on customer_statistic (created_at);
create index idx_user_id on customer_statistic (user_id);
