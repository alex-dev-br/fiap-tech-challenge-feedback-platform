package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.PublishOutboxEventsResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.OutboxEventRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.StatusOutboxEvent;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PublishPendingOutboxEventsUseCaseIntegrationTest {

    private final PublishPendingOutboxEventsUseCase publishPendingOutboxEventsUseCase;
    private final OutboxEventRepositoryPort outboxEventRepository;

    PublishPendingOutboxEventsUseCaseIntegrationTest(
            PublishPendingOutboxEventsUseCase publishPendingOutboxEventsUseCase,
            OutboxEventRepositoryPort outboxEventRepository
    ) {
        this.publishPendingOutboxEventsUseCase = publishPendingOutboxEventsUseCase;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Test
    @Transactional
    void devePublicarEventoPendenteEAtualizarStatusParaPublicado() {
        OutboxEvent eventoPendente = OutboxEvent.pendente(
                UUID.randomUUID(),
                "feedback.created",
                """
                {
                  "id": "feedback-id",
                  "descricao": "A plataforma está travando durante a aula",
                  "nota": 8,
                  "urgencia": "ALTA"
                }
                """
        );

        outboxEventRepository.save(eventoPendente);

        PublishOutboxEventsResult result = publishPendingOutboxEventsUseCase.execute(10);

        OutboxEvent eventoAtualizado = outboxEventRepository.findById(eventoPendente.id())
                .orElseThrow();

        assertTrue(result.totalEncontrados() >= 1);
        assertTrue(result.publicados() >= 1);
        assertEquals(0, result.falhas());

        assertEquals(StatusOutboxEvent.PUBLICADO, eventoAtualizado.status());
        assertNotNull(eventoAtualizado.publishedAt());
        assertEquals(0, eventoAtualizado.retryCount());
    }
}
