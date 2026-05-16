CREATE TABLE feedback (
    id UUID PRIMARY KEY,
    descricao VARCHAR(2000) NOT NULL,
    nota INTEGER NOT NULL,
    urgencia VARCHAR(20) NOT NULL,
    data_criacao TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_feedback_nota_range CHECK (nota BETWEEN 0 AND 10),
    CONSTRAINT chk_feedback_urgencia CHECK (urgencia IN ('BAIXA', 'MEDIA', 'ALTA'))
);

CREATE INDEX idx_feedback_data_criacao ON feedback (data_criacao);
CREATE INDEX idx_feedback_urgencia ON feedback (urgencia);
