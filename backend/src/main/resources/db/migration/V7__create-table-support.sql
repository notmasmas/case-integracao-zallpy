CREATE TABLE supports (
    id uuid,
    user_id uuid REFERENCES users(id),

    PRIMARY KEY (id)
)