CREATE TABLE managers (
    id uuid,
    user_id uuid UNIQUE REFERENCES users(id),

    PRIMARY KEY (id)
)
