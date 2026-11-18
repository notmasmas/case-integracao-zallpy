-- Chamados do cliente email@cadastrado.com (V11) ainda sem suporte atribuído e sem categoria,
-- para testar o suporte assumindo o chamado (PATCH /support/tickets/{ticketId}/assign).

INSERT INTO tickets (id, customer_id, support_id, project_id, title, description, category, status, evaluate, evaluate_comment, is_evaluated, created_at, updated_at)
SELECT
    '77777777-7777-7777-7777-777777777701',
    c.id,
    NULL,
    NULL,
    'Painel sem geração',
    'Um dos painéis parou de gerar energia desde a última chuva forte.',
    NULL,
    'PENDING',
    NULL,
    NULL,
    FALSE,
    TIMESTAMP '2026-10-20 08:30:00',
    TIMESTAMP '2026-10-20 08:30:00'
FROM customers c
JOIN users customer_user ON customer_user.id = c.user_id
WHERE customer_user.email = 'email@cadastrado.com';

INSERT INTO tickets (id, customer_id, support_id, project_id, title, description, category, status, evaluate, evaluate_comment, is_evaluated, created_at, updated_at)
SELECT
    '77777777-7777-7777-7777-777777777702',
    c.id,
    NULL,
    NULL,
    'Dúvida na fatura',
    'Não entendi o valor de compensação que aparece na fatura deste mês.',
    NULL,
    'PENDING',
    NULL,
    NULL,
    FALSE,
    TIMESTAMP '2026-10-22 14:10:00',
    TIMESTAMP '2026-10-22 14:10:00'
FROM customers c
JOIN users customer_user ON customer_user.id = c.user_id
WHERE customer_user.email = 'email@cadastrado.com';
