CREATE TABLE messages (
    id uuid,
    ticket_id uuid REFERENCES tickets(id),
    sender_user_id uuid REFERENCES users(id),
    content VARCHAR(250),
    created_at TIMESTAMP,

    PRIMARY KEY (id)
)