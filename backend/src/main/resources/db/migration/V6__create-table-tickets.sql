CREATE TABLE tickets (
    id uuid,
    customer_id uuid REFERENCES customers(id),
    support_id uuid REFERENCES support(id),
    project_id uuid REFERENCES projects(id),
    title VARCHAR(100),
    description VARCHAR(150),
    category VARCHAR(20),
    status VARCHAR(20),
    evaluate INT,
    evaluate_comment VARCHAR(150),
    is_evaluated BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    PRIMARY KEY (id)
)
