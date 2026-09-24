CREATE TABLE tickets (
    id uuid,
    customer_id uuid --REFERENCES users(id),
    support_id uuid --REFERENCES users(id),
    project_id uuid --REFERENCES projects(id),
    title VARCHAR(50),
    description VARCHAR(150),
    category VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    PRIMARY KEY (id)
)