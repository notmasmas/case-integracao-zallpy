-- Senha do gerente: Senha@123 (mesmo hash PBKDF2 dos usuários da V9)
-- ON CONFLICT: ambientes que já têm esse gerente não falham na migration.

INSERT INTO users (id, name, phone, cpf, address_id, email, password, role)
VALUES ('b3000000-0000-0000-0000-000000000001', 'Gerente EcoVolt', '(51) 99333-0001', '33333333301', NULL, 'gerente@ecovolt.com', 'a50c7210b7a999ff3c8e9808778f0b56c650d68f658b2b216c15261caefec33625aa060427aed368e008163f5eeaf013', 'MANAGER')
ON CONFLICT DO NOTHING;

INSERT INTO managers (id, user_id)
SELECT 'd1000000-0000-0000-0000-000000000001', id FROM users WHERE email = 'gerente@ecovolt.com'
ON CONFLICT DO NOTHING;
