CREATE TABLE privilege (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255)
);

CREATE TABLE role_entity (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255)
);

CREATE TABLE roles_privileges (
                                  role_id BIGINT NOT NULL,
                                  privilege_id BIGINT NOT NULL,
                                  PRIMARY KEY (role_id, privilege_id),
                                  CONSTRAINT fk_role_priv_role FOREIGN KEY (role_id) REFERENCES role_entity(id),
                                  CONSTRAINT fk_role_priv_priv FOREIGN KEY (privilege_id) REFERENCES privilege(id)
);

CREATE TABLE user_entity (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             first_name VARCHAR(255),
                             last_name VARCHAR(255),
                             email VARCHAR(255),
                             password VARCHAR(255),
                             enabled BOOLEAN
);

CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id BIGINT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES user_entity(id),
                             CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role_entity(id)
);