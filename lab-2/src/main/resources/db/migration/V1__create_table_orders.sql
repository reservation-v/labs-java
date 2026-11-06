CREATE TABLE orders
(
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_name VARCHAR(255)     NOT NULL,
    amount        DOUBLE PRECISION NOT NULL
);
