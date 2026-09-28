CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    cpf VARCHAR(11) NOT NULL,
    email VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    address_id UUID,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT uk_users_cpf UNIQUE (cpf),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_address FOREIGN KEY (address_id) REFERENCES addresses(id)
);
