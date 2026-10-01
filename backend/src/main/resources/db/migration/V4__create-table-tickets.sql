CREATE TABLE tickets (
    id uuid,
    customer_id uuid NOT NULL,
    support_id uuid,
    project_id uuid,
    title VARCHAR(50) NOT NULL,
    description VARCHAR(150) NOT NULL,
    category VARCHAR(50),
    status VARCHAR(50),
    evaluate INTEGER,
    evaluate_comment VARCHAR(150),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    PRIMARY KEY (id)
)