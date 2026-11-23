-- SCRUM-140 (US19): controle de inatividade da sessão.
-- Momento da última atividade válida do usuário autenticado. NULL = sem sessão ativa rastreada.
ALTER TABLE users ADD COLUMN last_activity_at TIMESTAMP WITH TIME ZONE;
