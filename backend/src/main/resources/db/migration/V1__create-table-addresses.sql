CREATE TABLE addresses (
    id uuid,
    cep VARCHAR(10),
    state VARCHAR(2),
    city VARCHAR(100),
    neighborhood VARCHAR(100),
    street VARCHAR(100),
    number VARCHAR(20),
    complement VARCHAR(100),

    PRIMARY KEY (id)
)