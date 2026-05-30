CREATE TABLE feedback_notification_log (
                                           id UUID PRIMARY KEY,
                                           feedback_id UUID NOT NULL,
                                           tipo VARCHAR(50) NOT NULL,
                                           status VARCHAR(20) NOT NULL,
                                           data_tentativa TIMESTAMP WITH TIME ZONE NOT NULL,
                                           mensagem_erro VARCHAR(2000),
                                           CONSTRAINT fk_feedback_notification_log_feedback
                                               FOREIGN KEY (feedback_id) REFERENCES feedback(id),
                                           CONSTRAINT chk_feedback_notification_type
                                               CHECK (tipo IN ('EMAIL')),
                                           CONSTRAINT chk_feedback_notification_status
                                               CHECK (status IN ('ENVIADA', 'FALHA'))
);

CREATE INDEX idx_feedback_notification_log_feedback_id
    ON feedback_notification_log (feedback_id);

CREATE INDEX idx_feedback_notification_log_status
    ON feedback_notification_log (status);

CREATE INDEX idx_feedback_notification_log_data_tentativa
    ON feedback_notification_log (data_tentativa);

