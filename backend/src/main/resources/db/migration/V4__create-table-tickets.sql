CREATE TABLE tickets (
    id uuid,
    customer_id uuid,
    support_id uuid,
    project_id uuid,
    title VARCHAR(50),
    description VARCHAR(150),
    category VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    PRIMARY KEY (id)
)