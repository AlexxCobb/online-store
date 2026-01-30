insert into
    "user" (
        user_public_id,
        user_name,
        user_lastname,
        user_birthday,
        user_email,
        user_password_hash)
values (
    gen_random_uuid(),
    'Admin',
    'System',
    '2000-02-20',
    '${admin_email}',
    '${admin_password_hash}');

INSERT INTO users_roles (user_id, role_id)
VALUES (
    (SELECT user_id FROM "user" WHERE user_email = '${admin_email}'),
    (SELECT role_id FROM "role" WHERE role_name = 'ROLE_ADMIN')
);