package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.CreateFeedbackCommand;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.OutboxEventRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.StatusOutboxEvent;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class CreateFeedbackUseCaseIntegrationTest {

    @Inject
    CreateFeedbackUseCase createFeedbackUseCase;

    @Inject
    FeedbackRepositoryPort feedbackRepository;

    @Inject
    OutboxEventRepositoryPort outboxEventRepository;

    @Test
    void deveCriarFeedbackERegistrarEventoNaOutbox() {
        CreateFeedbackCommand command = new CreateFeedbackCommand(
                "A plataforma está travando durante a aula",
                8
        );

        FeedbackCreatedResult result = createFeedbackUseCase.execute(command);

        assertNotNull(result.id());
        assertEquals("A plataforma está travando durante a aula", result.descricao());
        assertEquals(8, result.nota());
        assertEquals(Urgencia.ALTA, result.urgencia());
        assertNotNull(result.dataCriacao());

        Feedback feedbackSalvo = feedbackRepository.findById(result.id())
                .orElseThrow();

        assertEquals(result.id(), feedbackSalvo.id());
        assertEquals(Urgencia.ALTA, feedbackSalvo.urgencia());

        List<OutboxEvent> eventosPendentes = outboxEventRepository.findPending(10);

        OutboxEvent eventoCriado = eventosPendentes.stream()
                .filter(evento -> evento.aggregateId().equals(result.id()))
                .findFirst()
                .orElseThrow();

        assertEquals("feedback.created", eventoCriado.eventType());
        assertEquals(StatusOutboxEvent.PENDENTE, eventoCriado.status());
        assertEquals(0, eventoCriado.retryCount());
        assertTrue(eventoCriado.payload().contains(result.id().toString()));
        assertTrue(eventoCriado.payload().contains("ALTA"));
    }
}
