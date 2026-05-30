package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackNotificationLogRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.FeedbackNotificationLog;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackNotificationLogEntity;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.mapper.FeedbackNotificationLogPersistenceMapper;
import io.micrometer.core.annotation.Timed;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class JpaFeedbackNotificationLogRepositoryAdapter implements FeedbackNotificationLogRepositoryPort {

    private final PanacheFeedbackNotificationLogRepository panacheRepository;
    private final FeedbackNotificationLogPersistenceMapper mapper;

    public JpaFeedbackNotificationLogRepositoryAdapter(
            PanacheFeedbackNotificationLogRepository panacheRepository,
            FeedbackNotificationLogPersistenceMapper mapper) {
        this.panacheRepository = panacheRepository;
        this.mapper = mapper;
    }

    @Timed(value = "feedback.notification.log.repository.save", description = "Tempo de execução da gravação do log de notificação")
    @Override
    public FeedbackNotificationLog save(FeedbackNotificationLog notificationLog) {
        FeedbackNotificationLogEntity entity = mapper.toEntity(notificationLog);
        panacheRepository.persist(entity);
        return mapper.toDomain(entity);
    }

    @Timed(value = "feedback.notification.log.repository.findByFeedbackId", description = "Tempo de execução da busca de logs por feedback")
    @Override
    public List<FeedbackNotificationLog> findByFeedbackId(UUID feedbackId) {
        return panacheRepository.findByFeedbackId(feedbackId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
