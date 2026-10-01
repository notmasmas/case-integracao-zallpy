INSERT INTO users (id, name, cpf, email, password, role)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Usuario Teste',
    '12345678901',
    'email@cadastrado.com',
    '248afa0d328e67db55738f227da784f77f30f28ee7f5ea273893c9ab03b62a1f5830018af9bfd95641663a65b95ffe45',
    'CUSTOMER'
);

INSERT INTO users (id, name, cpf, email, password, role)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'Usuario Suporte',
    '10987654321',
    'suporte@email.com',
    '248afa0d328e67db55738f227da784f77f30f28ee7f5ea273893c9ab03b62a1f5830018af9bfd95641663a65b95ffe45',
    'SUPPORT'
    
);

INSERT INTO customers (id, user_id)
VALUES ('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111');

INSERT INTO supports (id, user_id)
VALUES ('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222');