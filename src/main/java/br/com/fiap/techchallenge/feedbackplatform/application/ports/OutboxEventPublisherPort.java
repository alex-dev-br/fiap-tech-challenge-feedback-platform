package br.com.fiap.techchallenge.feedbackplatform.application.ports;

import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;

public interface OutboxEventPublisherPort {

    void publish(OutboxEvent outboxEvent);
}
