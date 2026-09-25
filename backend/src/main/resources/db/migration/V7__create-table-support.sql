CREATE TABLE support (
    id uuid,
    support_id uuid REFERENCES users(id),

    PRIMARY KEY (id)
)
