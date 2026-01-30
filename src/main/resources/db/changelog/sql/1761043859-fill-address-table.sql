insert into
        delivery_address (
            delivery_address_public_id,
            user_id,
            address_type_id,
            country,
            town,
            zip_code,
            street,
            house_number,
            flat_number,
            is_active,
            is_system
        )
values  (gen_random_uuid(), null, 3, 'Россия', 'Санкт-Петербург', 190000, 'Невский проспект', 1, null, TRUE, TRUE),
        (gen_random_uuid(), null, 3, 'Россия', 'Москва', 101000, 'Спасская улица', 1, null, TRUE, TRUE),
        (gen_random_uuid(), null, 2, 'Россия', 'Москва', 101000, 'Тверская улица', 1, null, TRUE, TRUE),
        (gen_random_uuid(), null, 2, 'Россия', 'Санкт-Петербург', 190800, 'Литейный проспект', 2, null, TRUE, TRUE);