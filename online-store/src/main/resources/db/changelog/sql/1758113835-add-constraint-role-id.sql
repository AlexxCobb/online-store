ALTER TABLE users_roles
ADD CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id) REFERENCES "role"(role_id);