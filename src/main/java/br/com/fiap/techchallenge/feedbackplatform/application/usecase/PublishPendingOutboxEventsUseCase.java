package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.PublishOutboxEventsResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.OutboxEventPublisherPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.OutboxEventRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class PublishPendingOutboxEventsUseCase {

    private static final Logger LOG = Logger.getLogger(PublishPendingOutboxEventsUseCase.class);

    private final OutboxEventRepositoryPort outboxEventRepository;
    private final OutboxEventPublisherPort outboxEventPublisher;

    @Inject
    public PublishPendingOutboxEventsUseCase(
            OutboxEventRepositoryPort outboxEventRepository,
            OutboxEventPublisherPort outboxEventPublisher
    ) {
        this.outboxEventRepository = Objects.requireNonNull(outboxEventRepository);
        this.outboxEventPublisher = Objects.requireNonNull(outboxEventPublisher);
    }

    @Transactional
    public PublishOutboxEventsResult execute(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("O limite de eventos deve ser maior que zero.");
        }

        List<OutboxEvent> eventosPendentes = outboxEventRepository.findPending(limit);

        int publicados = 0;
        int falhas = 0;

        for (OutboxEvent evento : eventosPendentes) {
            try {
                outboxEventPublisher.publish(evento);

                OutboxEvent eventoPublicado = evento.marcarComoPublicado(OffsetDateTime.now());
                outboxEventRepository.save(eventoPublicado);

                publicados++;
            } catch (Exception exception) {
                LOG.errorf(
                        exception,
                        "Falha ao publicar evento da outbox. eventId=%s, aggregateId=%s, eventType=%s",
                        evento.id(),
                        evento.aggregateId(),
                        evento.eventType()
                );

                OutboxEvent eventoComFalha = evento.registrarFalha();
                outboxEventRepository.save(eventoComFalha);

                falhas++;
            }
        }

        return new PublishOutboxEventsResult(
                eventosPendentes.size(),
                publicados,
                falhas
        );
    }
}

