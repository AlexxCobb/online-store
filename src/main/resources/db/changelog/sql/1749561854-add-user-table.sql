create TABLE
    "user" (
        user_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        user_public_id VARCHAR(255) NOT NULL UNIQUE,
        user_name VARCHAR(100) NOT NULL,
        user_lastname VARCHAR(100) NOT NULL,
        user_birthday DATE NOT NULL,
        user_email VARCHAR(255) NOT NULL UNIQUE,
        user_password_hash VARCHAR(255) NOT NULL
    );

comment on column "user".user_id is 'ID пользователя';
comment on column "user".user_public_id is 'Публичный ID пользователя для внешнего использования';
comment on column "user".user_name is 'Имя пользователя';
comment on column "user".user_lastname is 'Фамилия пользователя';
comment on column "user".user_birthday is 'Дата рождения';
comment on column "user".user_email is 'Уникальный email для входа в систему';
comment on column "user".user_password_hash is 'Bcrypt-хэш пароля пользователя';

create index idx_user_email on "user" (user_email);
create index idx_user_public_id on "user" (user_public_id);