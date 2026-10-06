CREATE TABLE support (
    id uuid,
    user_id uuid UNIQUE REFERENCES users(id),

    PRIMARY KEY (id)
)
