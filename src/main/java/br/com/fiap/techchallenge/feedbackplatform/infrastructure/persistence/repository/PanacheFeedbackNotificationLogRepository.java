package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackNotificationLogEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanacheFeedbackNotificationLogRepository
        implements PanacheRepositoryBase<FeedbackNotificationLogEntity, UUID> {

    public List<FeedbackNotificationLogEntity> findByFeedbackId(UUID feedbackId) {
        return list("feedbackId = ?1 order by dataTentativa desc", feedbackId);
    }
}
