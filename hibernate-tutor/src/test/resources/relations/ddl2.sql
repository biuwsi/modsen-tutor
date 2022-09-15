CREATE TABLE customer
(
    id     bigserial NOT NULL,
    "name" varchar   NOT NULL,
    "age" smallint   NOT NULL,
    CONSTRAINT customer_pk PRIMARY KEY (id)
);