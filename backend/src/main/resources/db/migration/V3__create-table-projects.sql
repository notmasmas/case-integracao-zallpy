CREATE TABLE projects (
    id uuid,
    customer_id uuid REFERENCES users(id),
    address_id uuid REFERENCES addresses(id),
    name VARCHAR(50),
    status VARCHAR(50),
    installation_date TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    PRIMARY KEY (id)
)