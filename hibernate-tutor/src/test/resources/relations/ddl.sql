CREATE TABLE role
(
    id     bigserial NOT NULL,
    "name" varchar   NOT NULL,
    CONSTRAINT customer_pk PRIMARY KEY (id)
);

CREATE TABLE role_user
(
    role_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT role_user_pk PRIMARY KEY (role_id, user_id)
);

CREATE TABLE address
(
    id   bigserial NOT NULL,
    zip  varchar   NOT NULL,
    city varchar   NOT NULL,
    CONSTRAINT address_pk PRIMARY KEY (id)
);

CREATE TABLE user
(
    id         bigserial NOT NULL,
    username   varchar   NOT NULL,
    email      varchar   NOT NULL,
    address_id bigint    NOT NULL,
    CONSTRAINT customer_pk PRIMARY KEY (id)
);

CREATE TABLE phone
(
    id   bigserial NOT NULL,
    zip  varchar   NOT NULL,
    city varchar   NOT NULL,
    CONSTRAINT address_pk PRIMARY KEY (id)
);