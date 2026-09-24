CREATE TABLE addresses (
    id uuid,
    user_id uuid REFERENCES (users),

    PRIMARY KEY (id)
)