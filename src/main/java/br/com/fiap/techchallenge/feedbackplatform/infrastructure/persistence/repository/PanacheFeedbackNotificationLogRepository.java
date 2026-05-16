package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository;

import java.util.UUID;

import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackNotificationLogEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanacheFeedbackNotificationLogRepository
        implements PanacheRepositoryBase<FeedbackNotificationLogEntity, UUID> {
}
