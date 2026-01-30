create TABLE
    "role" (
    role_id INTEGER NOT NULL UNIQUE,
    role_name VARCHAR(255) NOT NULL UNIQUE,
    role_description VARCHAR(255) NOT NULL
);

comment on column "role".role_id is 'ID роли';
comment on column "role".role_name is 'Назначение роли в формате: ROLE_USER, ROLE_ADMIN и т.д.';
comment on column "role".role_description is 'Описание, что доступно данной роли';