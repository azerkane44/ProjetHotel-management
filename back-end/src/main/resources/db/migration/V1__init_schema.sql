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
