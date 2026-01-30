insert into
    order_status (
        order_status_name,
        order_status_description
    )
values  ('PENDING_PAYMENT', 'Заказ ожидает оплаты'),
        ('PENDING_DELIVERY', 'Заказ ожидает отгрузки'),
        ('SHIPPED', 'Заказ отгружен'),
        ('DELIVERED', 'Заказ доставлен'),
        ('CANCELLED', 'Заказ отменен');