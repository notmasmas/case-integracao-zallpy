-- Chamados e mensagens do cliente email@cadastrado.com com o suporte suporte@email.com (V11).
-- Cada chamado tem 5 mensagens, alternando cliente e suporte.

INSERT INTO tickets (id, customer_id, support_id, project_id, title, description, category, status, evaluate, evaluate_comment, created_at, updated_at)
SELECT
    '55555555-5555-5555-5555-555555555501',
    c.id,
    s.id,
    NULL,
    'Geração abaixo do esperado',
    'O app mostra geração bem abaixo do histórico dos últimos meses.',
    'SYSTEM_MONITORING',
    'IN_PROGRESS',
    NULL,
    NULL,
    TIMESTAMP '2026-10-01 09:00:00',
    TIMESTAMP '2026-10-01 11:20:00'
FROM customers c
JOIN users customer_user ON customer_user.id = c.user_id
CROSS JOIN support s
JOIN users support_user ON support_user.id = s.user_id
WHERE customer_user.email = 'email@cadastrado.com'
  AND support_user.email = 'suporte@email.com';

INSERT INTO tickets (id, customer_id, support_id, project_id, title, description, category, status, evaluate, evaluate_comment, created_at, updated_at)
SELECT
    '55555555-5555-5555-5555-555555555502',
    c.id,
    s.id,
    NULL,
    'Inversor desligando sozinho',
    'O inversor desliga no fim da tarde e só volta no dia seguinte.',
    'EQUIPMENT',
    'UNDER_REVIEW',
    NULL,
    NULL,
    TIMESTAMP '2026-10-02 14:00:00',
    TIMESTAMP '2026-10-02 16:40:00'
FROM customers c
JOIN users customer_user ON customer_user.id = c.user_id
CROSS JOIN support s
JOIN users support_user ON support_user.id = s.user_id
WHERE customer_user.email = 'email@cadastrado.com'
  AND support_user.email = 'suporte@email.com';

INSERT INTO tickets (id, customer_id, support_id, project_id, title, description, category, status, evaluate, evaluate_comment, created_at, updated_at)
SELECT
    '55555555-5555-5555-5555-555555555503',
    c.id,
    s.id,
    NULL,
    'Dúvida na fatura de energia',
    'A conta de luz veio mais alta mesmo com o sistema instalado.',
    'PAYMENT',
    'RESOLVED',
    5,
    'Esclarecimento claro e rápido sobre a compensação.',
    TIMESTAMP '2026-09-20 10:00:00',
    TIMESTAMP '2026-09-20 15:10:00'
FROM customers c
JOIN users customer_user ON customer_user.id = c.user_id
CROSS JOIN support s
JOIN users support_user ON support_user.id = s.user_id
WHERE customer_user.email = 'email@cadastrado.com'
  AND support_user.email = 'suporte@email.com';

INSERT INTO messages (id, ticket_id, sender_user_id, content, created_at)
SELECT message.id, message.ticket_id, sender.id, message.content, message.created_at
FROM (VALUES
    ('66666666-6666-6666-6666-666666666601'::uuid, '55555555-5555-5555-5555-555555555501'::uuid, 'email@cadastrado.com', 'O app mostra geração bem abaixo do histórico dos últimos meses.', TIMESTAMP '2026-10-01 09:05:00'),
    ('66666666-6666-6666-6666-666666666602'::uuid, '55555555-5555-5555-5555-555555555501'::uuid, 'suporte@email.com', 'Vamos analisar o inversor. Há sombra sobre os painéis em algum horário?', TIMESTAMP '2026-10-01 09:40:00'),
    ('66666666-6666-6666-6666-666666666603'::uuid, '55555555-5555-5555-5555-555555555501'::uuid, 'email@cadastrado.com', 'Uma árvore nova no quintal cobre parte do telhado pela manhã.', TIMESTAMP '2026-10-01 10:15:00'),
    ('66666666-6666-6666-6666-666666666604'::uuid, '55555555-5555-5555-5555-555555555501'::uuid, 'suporte@email.com', 'Isso explica a queda. Recomendamos a poda ou o reposicionamento dos módulos.', TIMESTAMP '2026-10-01 10:50:00'),
    ('66666666-6666-6666-6666-666666666605'::uuid, '55555555-5555-5555-5555-555555555501'::uuid, 'email@cadastrado.com', 'Vou providenciar a poda nesta semana e aviso o resultado.', TIMESTAMP '2026-10-01 11:20:00'),

    ('66666666-6666-6666-6666-666666666606'::uuid, '55555555-5555-5555-5555-555555555502'::uuid, 'email@cadastrado.com', 'O inversor desliga no fim da tarde e só volta no dia seguinte.', TIMESTAMP '2026-10-02 14:05:00'),
    ('66666666-6666-6666-6666-666666666607'::uuid, '55555555-5555-5555-5555-555555555502'::uuid, 'suporte@email.com', 'Qual o modelo do inversor e o código de erro exibido no visor?', TIMESTAMP '2026-10-02 14:40:00'),
    ('66666666-6666-6666-6666-666666666608'::uuid, '55555555-5555-5555-5555-555555555502'::uuid, 'email@cadastrado.com', 'É um Growatt e o código que aparece é E03.', TIMESTAMP '2026-10-02 15:10:00'),
    ('66666666-6666-6666-6666-666666666609'::uuid, '55555555-5555-5555-5555-555555555502'::uuid, 'suporte@email.com', 'E03 indica superaquecimento. Vamos agendar uma visita técnica.', TIMESTAMP '2026-10-02 15:50:00'),
    ('66666666-6666-6666-6666-666666666610'::uuid, '55555555-5555-5555-5555-555555555502'::uuid, 'email@cadastrado.com', 'Prefiro a visita na quinta-feira pela manhã.', TIMESTAMP '2026-10-02 16:40:00'),

    ('66666666-6666-6666-6666-666666666611'::uuid, '55555555-5555-5555-5555-555555555503'::uuid, 'email@cadastrado.com', 'A conta de luz veio mais alta mesmo com o sistema instalado.', TIMESTAMP '2026-09-20 10:05:00'),
    ('66666666-6666-6666-6666-666666666612'::uuid, '55555555-5555-5555-5555-555555555503'::uuid, 'suporte@email.com', 'A compensação pode levar um ciclo. Qual o mês e o vencimento da conta?', TIMESTAMP '2026-09-20 11:00:00'),
    ('66666666-6666-6666-6666-666666666613'::uuid, '55555555-5555-5555-5555-555555555503'::uuid, 'email@cadastrado.com', 'É a fatura de setembro, com vencimento no dia 15.', TIMESTAMP '2026-09-20 11:30:00'),
    ('66666666-6666-6666-6666-666666666614'::uuid, '55555555-5555-5555-5555-555555555503'::uuid, 'suporte@email.com', 'A energia injetada foi compensada. O valor maior é do consumo extra.', TIMESTAMP '2026-09-20 14:20:00'),
    ('66666666-6666-6666-6666-666666666615'::uuid, '55555555-5555-5555-5555-555555555503'::uuid, 'email@cadastrado.com', 'Entendi, obrigado pelo esclarecimento.', TIMESTAMP '2026-09-20 15:10:00')
) AS message (id, ticket_id, sender_email, content, created_at)
JOIN users sender ON sender.email = message.sender_email;
