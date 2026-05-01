package br.com.fiap.techchallenge.feedbackplatform.application.ports;

import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxEventRepositoryPort {

    OutboxEvent save(OutboxEvent outboxEvent);

    Optional<OutboxEvent> findById(UUID id);

    List<OutboxEvent> findPending(int limit);
}
