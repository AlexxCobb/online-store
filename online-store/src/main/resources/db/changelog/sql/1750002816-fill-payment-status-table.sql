insert into
    payment_status (
        payment_status_id,
        payment_status_name,
        payment_status_description
    )
values  (1, 'PENDING', 'Ожидается оплата'),
        (2, 'PAID', 'Оплата получена'),
        (3, 'REJECTED', 'Оплата отклонена'),
        (4, 'REFUND', 'Возврат оплаты');