package br.com.fiap.techchallenge.feedbackplatform.infrastructure.messaging;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.OutboxEventPublisherPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LoggingOutboxEventPublisherAdapter implements OutboxEventPublisherPort {

    private static final Logger LOG = Logger.getLogger(LoggingOutboxEventPublisherAdapter.class);

    @Override
    public void publish(OutboxEvent outboxEvent) {
        LOG.infof(
                "Publicando evento da outbox. eventId=%s, aggregateId=%s, eventType=%s, payload=%s",
                outboxEvent.id(),
                outboxEvent.aggregateId(),
                outboxEvent.eventType(),
                outboxEvent.payload()
        );
    }
}
