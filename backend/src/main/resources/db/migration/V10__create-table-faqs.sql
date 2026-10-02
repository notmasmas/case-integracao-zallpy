CREATE TABLE faqs (
    id uuid,
    title VARCHAR(150),
    answer VARCHAR(500),
    category VARCHAR(30),
    updated_by_user_id uuid REFERENCES users(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    PRIMARY KEY (id)
)
