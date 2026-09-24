CREATE TABLE addresses (
    id uuid,
    support_id uuid REFERENCES (users),

    PRIMARY KEY (id)
)