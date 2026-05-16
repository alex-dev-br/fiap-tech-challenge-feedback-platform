package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackEntity;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.mapper.FeedbackPersistenceMapper;
import io.micrometer.core.annotation.Timed;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JpaFeedbackRepositoryAdapter implements FeedbackRepositoryPort {

    private final PanacheFeedbackRepository panacheRepository;
    private final FeedbackPersistenceMapper mapper;

    public JpaFeedbackRepositoryAdapter(
            PanacheFeedbackRepository panacheRepository,
            FeedbackPersistenceMapper mapper) {
        this.panacheRepository = panacheRepository;
        this.mapper = mapper;
    }

    @Timed(value = "feedback.repository.save", description = "Tempo de execução da gravação")
    @Override
    public Feedback save(Feedback feedback) {
        FeedbackEntity entity = mapper.toEntity(feedback);
        panacheRepository.persist(entity);
        return mapper.toDomain(entity);
    }

    @Timed(value = "feedback.repository.findById", description = "Tempo de execução da busca por ID")
    @Override
    public Optional<Feedback> findById(UUID id) {
        return panacheRepository.findByIdOptional(id)
                .map(mapper::toDomain);
    }
}
