CREATE TABLE users (
    id uuid,
    name VARCHAR(100),
    phone VARCHAR(50),
    cpf VARCHAR(15) UNIQUE NOT NULL,
    address_id uuid REFERENCES addresses(id),
    email VARCHAR(50) UNIQUE,
    password VARCHAR(100),
    role VARCHAR(50),

    PRIMARY KEY (id)
)
