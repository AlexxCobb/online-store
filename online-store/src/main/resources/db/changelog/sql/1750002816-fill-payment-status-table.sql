insert into
    payment_status (
        payment_status_name,
        payment_status_description
    )
values  ('PENDING', 'Ожидается оплата'),
        ('PAID', 'Оплата получена'),
        ('REJECTED', 'Оплата отклонена'),
        ('REFUND', 'Возврат оплаты');