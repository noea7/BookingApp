CREATE TABLE IF NOT EXISTS CUSTOMER (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL VARCHAR(255) NOT NULL
);