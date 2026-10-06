CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    cpf VARCHAR(15) UNIQUE NOT NULL,
    address_id uuid REFERENCES addresses(id),
    email VARCHAR(50) UNIQUE,
    password VARCHAR(100),
    role VARCHAR(50)
)
