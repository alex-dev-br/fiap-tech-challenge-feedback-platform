package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.CreateFeedbackCommand;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackUrgenciaClassifier;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.Notification;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateFeedbackUseCaseTest {

    @Test
    void deveCriarSalvarFeedbackERetornarResultado() {
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.BAIXA;
        List<Feedback> notificacoes = new ArrayList<>();
        Notification<Feedback> notification = notificacoes::add;

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                classifier,
                List.of(notification)
        );

        FeedbackCreatedResult result = useCase.execute(
                new CreateFeedbackCommand("Aula muito boa", 9)
        );

        assertNotNull(result.id());
        assertEquals("Aula muito boa", result.descricao());
        assertEquals(9, result.nota());
        assertEquals(Urgencia.BAIXA, result.urgencia());
        assertNotNull(result.dataCriacao());

        assertNotNull(repository.feedbackSalvo);
        assertEquals(result.id(), repository.feedbackSalvo.id());

        assertTrue(notificacoes.isEmpty());
    }

    @Test
    void deveNotificarQuandoFeedbackForUrgente() {
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.ALTA;
        List<Feedback> notificacoes = new ArrayList<>();
        Notification<Feedback> notification = notificacoes::add;

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                classifier,
                List.of(notification)
        );

        FeedbackCreatedResult result = useCase.execute(
                new CreateFeedbackCommand("Sistema travando", 8)
        );

        assertEquals(Urgencia.ALTA, result.urgencia());
        assertEquals(1, notificacoes.size());
        assertEquals(result.id(), notificacoes.getFirst().id());
    }

    @Test
    void naoDeveNotificarQuandoFeedbackNaoForUrgente() {
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.MEDIA;
        List<Feedback> notificacoes = new ArrayList<>();
        Notification<Feedback> notification = notificacoes::add;

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                classifier,
                List.of(notification)
        );

        FeedbackCreatedResult result = useCase.execute(
                new CreateFeedbackCommand("Aula regular", 5)
        );

        assertEquals(Urgencia.MEDIA, result.urgencia());
        assertTrue(notificacoes.isEmpty());
    }

    @Test
    void deveFalharQuandoCommandForNulo() {
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.BAIXA;

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                classifier,
                List.of()
        );

        assertThrows(NullPointerException.class, () -> useCase.execute(null));
    }

    private static class FakeFeedbackRepository implements FeedbackRepositoryPort {

        private Feedback feedbackSalvo;

        @Override
        public Feedback save(Feedback feedback) {
            this.feedbackSalvo = feedback;
            return feedback;
        }

        @Override
        public Optional<Feedback> findById(UUID id) {
            return Optional.ofNullable(feedbackSalvo)
                    .filter(feedback -> feedback.id().equals(id));
        }
    }
}
