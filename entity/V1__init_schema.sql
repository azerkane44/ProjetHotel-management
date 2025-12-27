CREATE TABLE role_entity
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NULL,
    CONSTRAINT pk_roleentity PRIMARY KEY (id)
);

CREATE TABLE roles_privileges
(
    privilege_id BIGINT NOT NULL,
    role_id      BIGINT NOT NULL
);

ALTER TABLE roles_privileges
    ADD CONSTRAINT fk_rolpri_on_privilege FOREIGN KEY (privilege_id) REFERENCES privilege (id);

ALTER TABLE roles_privileges
    ADD CONSTRAINT fk_rolpri_on_role_entity FOREIGN KEY (role_id) REFERENCES role_entity (id);