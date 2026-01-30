ALTER TABLE users_roles
ADD CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES "user"(user_id);