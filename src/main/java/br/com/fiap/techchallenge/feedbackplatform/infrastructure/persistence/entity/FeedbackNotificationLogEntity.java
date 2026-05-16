package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "feedback_notification_log")
public class FeedbackNotificationLogEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "feedback_id", nullable = false)
    private UUID feedbackId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private FeedbackNotificationType tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FeedbackNotificationStatus status;

    @Column(name = "data_tentativa", nullable = false)
    private OffsetDateTime dataTentativa;

    @Column(name = "mensagem_erro", length = 2000)
    private String mensagemErro;

    public FeedbackNotificationLogEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(UUID feedbackId) {
        this.feedbackId = feedbackId;
    }

    public FeedbackNotificationType getTipo() {
        return tipo;
    }

    public void setTipo(FeedbackNotificationType tipo) {
        this.tipo = tipo;
    }

    public FeedbackNotificationStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackNotificationStatus status) {
        this.status = status;
    }

    public OffsetDateTime getDataTentativa() {
        return dataTentativa;
    }

    public void setDataTentativa(OffsetDateTime dataTentativa) {
        this.dataTentativa = dataTentativa;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }
}
