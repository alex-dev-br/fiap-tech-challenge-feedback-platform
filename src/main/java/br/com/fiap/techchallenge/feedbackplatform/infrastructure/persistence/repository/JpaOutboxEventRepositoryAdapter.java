package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.OutboxEventRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.StatusOutboxEvent;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.OutboxEventEntity;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.mapper.OutboxEventPersistenceMapper;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JpaOutboxEventRepositoryAdapter implements OutboxEventRepositoryPort {

    private final PanacheOutboxEventRepository panacheRepository;
    private final OutboxEventPersistenceMapper mapper;

    public JpaOutboxEventRepositoryAdapter(
            PanacheOutboxEventRepository panacheRepository,
            OutboxEventPersistenceMapper mapper
    ) {
        this.panacheRepository = panacheRepository;
        this.mapper = mapper;
    }

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        OutboxEventEntity entity = mapper.toEntity(outboxEvent);

        boolean exists = panacheRepository.findByIdOptional(outboxEvent.id()).isPresent();

        if (!exists) {
            panacheRepository.persist(entity);
            return mapper.toDomain(entity);
        }

        OutboxEventEntity merged = panacheRepository.getEntityManager().merge(entity);
        return mapper.toDomain(merged);
    }

    @Override
    public Optional<OutboxEvent> findById(UUID id) {
        return panacheRepository.findByIdOptional(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<OutboxEvent> findPending(int limit) {
        return panacheRepository
                .find("status", Sort.by("createdAt"), StatusOutboxEvent.PENDENTE)
                .page(0, limit)
                .list()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
