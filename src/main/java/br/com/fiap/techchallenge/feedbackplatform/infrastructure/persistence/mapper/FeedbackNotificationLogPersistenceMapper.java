package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.mapper;

import br.com.fiap.techchallenge.feedbackplatform.domain.model.FeedbackNotificationLog;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackNotificationLogEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FeedbackNotificationLogPersistenceMapper {

    public FeedbackNotificationLogEntity toEntity(FeedbackNotificationLog notificationLog) {
        FeedbackNotificationLogEntity entity = new FeedbackNotificationLogEntity();
        entity.setId(notificationLog.id());
        entity.setFeedbackId(notificationLog.feedbackId());
        entity.setTipo(notificationLog.tipo());
        entity.setStatus(notificationLog.status());
        entity.setDataTentativa(notificationLog.dataTentativa());
        entity.setMensagemErro(notificationLog.mensagemErro());
        return entity;
    }

    public FeedbackNotificationLog toDomain(FeedbackNotificationLogEntity entity) {
        return new FeedbackNotificationLog(
                entity.getId(),
                entity.getFeedbackId(),
                entity.getTipo(),
                entity.getStatus(),
                entity.getDataTentativa(),
                entity.getMensagemErro());
    }
}
