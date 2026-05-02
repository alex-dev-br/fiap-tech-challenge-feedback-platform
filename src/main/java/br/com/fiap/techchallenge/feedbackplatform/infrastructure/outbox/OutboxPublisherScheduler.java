package br.com.fiap.techchallenge.feedbackplatform.infrastructure.outbox;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.PublishOutboxEventsResult;
import br.com.fiap.techchallenge.feedbackplatform.application.usecase.PublishPendingOutboxEventsUseCase;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OutboxPublisherScheduler {

    private static final Logger LOG = Logger.getLogger(OutboxPublisherScheduler.class);

    private final PublishPendingOutboxEventsUseCase publishPendingOutboxEventsUseCase;
    private final boolean enabled;
    private final int batchSize;

    public OutboxPublisherScheduler(
            PublishPendingOutboxEventsUseCase publishPendingOutboxEventsUseCase,
            @ConfigProperty(name = "app.outbox.publisher.enabled", defaultValue = "true") boolean enabled,
            @ConfigProperty(name = "app.outbox.publisher.batch-size", defaultValue = "10") int batchSize
    ) {
        this.publishPendingOutboxEventsUseCase = publishPendingOutboxEventsUseCase;
        this.enabled = enabled;
        this.batchSize = batchSize;
    }

    @Scheduled(
            every = "{app.outbox.publisher.interval}",
            concurrentExecution = Scheduled.ConcurrentExecution.SKIP
    )
    void publishPendingEvents() {
        if (!enabled) {
            LOG.debug("Publicador de outbox desabilitado por configuração.");
            return;
        }

        PublishOutboxEventsResult result = publishPendingOutboxEventsUseCase.execute(batchSize);

        if (result.totalEncontrados() > 0) {
            LOG.infof(
                    "Publicação da outbox executada. encontrados=%d, publicados=%d, falhas=%d",
                    result.totalEncontrados(),
                    result.publicados(),
                    result.falhas()
            );
        }
    }
}
