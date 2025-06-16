insert into
    order_status (
        order_status_id ,
        order_status_name,
        order_status_description
    )
values  (1, 'PENDING_PAYMENT', 'Заказ ожидает оплаты'),
        (2, 'PENDING_DELIVERY', 'Заказ ожидает отгрузки'),
        (3, 'SHIPPED', 'Заказ отгружен'),
        (4, 'DELIVERED', 'Заказ доставлен');