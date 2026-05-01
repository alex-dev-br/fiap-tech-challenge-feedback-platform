package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.mapper;

import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.OutboxEventEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OutboxEventPersistenceMapper {

    public OutboxEventEntity toEntity(OutboxEvent outboxEvent) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.setId(outboxEvent.id());
        entity.setAggregateId(outboxEvent.aggregateId());
        entity.setEventType(outboxEvent.eventType());
        entity.setPayload(outboxEvent.payload());
        entity.setStatus(outboxEvent.status());
        entity.setCreatedAt(outboxEvent.createdAt());
        entity.setPublishedAt(outboxEvent.publishedAt());
        entity.setRetryCount(outboxEvent.retryCount());
        return entity;
    }

    public OutboxEvent toDomain(OutboxEventEntity entity) {
        return new OutboxEvent(
                entity.getId(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getPayload(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getPublishedAt(),
                entity.getRetryCount()
        );
    }
}
