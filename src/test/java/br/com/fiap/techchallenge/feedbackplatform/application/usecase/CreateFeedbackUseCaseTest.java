package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.CreateFeedbackCommand;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackNotificationLogRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackUrgenciaClassifier;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.Notification;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.FeedbackNotificationLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Caso de uso de criação de feedback")
class CreateFeedbackUseCaseTest {

    @Test
    @DisplayName("Deve criar e salvar feedback sem notificar quando a urgência for baixa")
    void deveCriarSalvarFeedbackERetornarResultado() {
        // Arrange
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FakeFeedbackNotificationLogRepository notificationLogRepository = new FakeFeedbackNotificationLogRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.BAIXA;
        List<Feedback> notificacoes = new ArrayList<>();
        Notification<Feedback> notification = notificacoes::add;

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                notificationLogRepository,
                classifier,
                List.of(notification));

        CreateFeedbackCommand command = new CreateFeedbackCommand("Aula muito boa", 9);

        // Act
        FeedbackCreatedResult result = useCase.execute(command);

        // Assert
        assertNotNull(result.id());
        assertEquals("Aula muito boa", result.descricao());
        assertEquals(9, result.nota());
        assertEquals(Urgencia.BAIXA, result.urgencia());
        assertNotNull(result.dataCriacao());

        assertNotNull(repository.feedbackSalvo);
        assertEquals(result.id(), repository.feedbackSalvo.id());

        assertTrue(notificacoes.isEmpty());
        assertTrue(notificationLogRepository.logs.isEmpty());
    }

    @Test
    @DisplayName("Deve notificar e registrar log ENVIADA quando o feedback for urgente")
    void deveNotificarQuandoFeedbackForUrgenteERegistrarLogEnviado() {
        // Arrange
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FakeFeedbackNotificationLogRepository notificationLogRepository = new FakeFeedbackNotificationLogRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.ALTA;
        List<Feedback> notificacoes = new ArrayList<>();
        Notification<Feedback> notification = notificacoes::add;

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                notificationLogRepository,
                classifier,
                List.of(notification));

        CreateFeedbackCommand command = new CreateFeedbackCommand("Sistema travando", 8);

        // Act
        FeedbackCreatedResult result = useCase.execute(command);

        // Assert
        assertEquals(Urgencia.ALTA, result.urgencia());
        assertEquals(1, notificacoes.size());
        assertEquals(result.id(), notificacoes.getFirst().id());

        assertEquals(1, notificationLogRepository.logs.size());
        assertEquals(result.id(), notificationLogRepository.logs.getFirst().feedbackId());
        assertEquals(FeedbackNotificationStatus.ENVIADA, notificationLogRepository.logs.getFirst().status());
        assertNull(notificationLogRepository.logs.getFirst().mensagemErro());
    }

    @Test
    @DisplayName("Não deve notificar nem registrar log quando o feedback não for urgente")
    void naoDeveNotificarNemRegistrarLogQuandoFeedbackNaoForUrgente() {
        // Arrange
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FakeFeedbackNotificationLogRepository notificationLogRepository = new FakeFeedbackNotificationLogRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.MEDIA;
        List<Feedback> notificacoes = new ArrayList<>();
        Notification<Feedback> notification = notificacoes::add;

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                notificationLogRepository,
                classifier,
                List.of(notification));

        CreateFeedbackCommand command = new CreateFeedbackCommand("Aula regular", 5);

        // Act
        FeedbackCreatedResult result = useCase.execute(command);

        // Assert
        assertEquals(Urgencia.MEDIA, result.urgencia());
        assertTrue(notificacoes.isEmpty());
        assertTrue(notificationLogRepository.logs.isEmpty());
    }

    @Test
    @DisplayName("Deve salvar feedback e registrar log FALHA quando a notificação falhar")
    void deveSalvarFeedbackMesmoQuandoNotificacaoFalharERegistrarLogDeFalha() {
        // Arrange
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FakeFeedbackNotificationLogRepository notificationLogRepository = new FakeFeedbackNotificationLogRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.ALTA;
        Notification<Feedback> notification = feedback -> {
            throw new RuntimeException("Falha simulada no envio da notificação");
        };

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                notificationLogRepository,
                classifier,
                List.of(notification));

        CreateFeedbackCommand command = new CreateFeedbackCommand("Sistema travando muito", 2);

        // Act
        FeedbackCreatedResult result = assertDoesNotThrow(() -> useCase.execute(command));

        // Assert
        assertEquals(Urgencia.ALTA, result.urgencia());

        assertNotNull(repository.feedbackSalvo);
        assertEquals(result.id(), repository.feedbackSalvo.id());
        assertEquals("Sistema travando muito", repository.feedbackSalvo.descricao());
        assertEquals(2, repository.feedbackSalvo.nota());

        assertEquals(1, notificationLogRepository.logs.size());
        assertEquals(result.id(), notificationLogRepository.logs.getFirst().feedbackId());
        assertEquals(FeedbackNotificationStatus.FALHA, notificationLogRepository.logs.getFirst().status());
        assertEquals("Falha simulada no envio da notificação", notificationLogRepository.logs.getFirst().mensagemErro());
    }

    @Test
    @DisplayName("Deve continuar notificando quando uma notificação falhar e registrar cada tentativa")
    void deveContinuarNotificandoQuandoUmaNotificacaoFalharERegistrarCadaTentativa() {
        // Arrange
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FakeFeedbackNotificationLogRepository notificationLogRepository = new FakeFeedbackNotificationLogRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.ALTA;
        List<String> notificacoesExecutadas = new ArrayList<>();

        Notification<Feedback> primeiraNotificacao = feedback -> notificacoesExecutadas.add("primeira");

        Notification<Feedback> segundaNotificacao = feedback -> {
            notificacoesExecutadas.add("segunda");
            throw new RuntimeException("Falha simulada na segunda notificação");
        };

        Notification<Feedback> terceiraNotificacao = feedback -> notificacoesExecutadas.add("terceira");

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                notificationLogRepository,
                classifier,
                List.of(primeiraNotificacao, segundaNotificacao, terceiraNotificacao));

        CreateFeedbackCommand command = new CreateFeedbackCommand("Sistema com erro crítico", 1);

        // Act
        FeedbackCreatedResult result = assertDoesNotThrow(() -> useCase.execute(command));

        // Assert
        assertEquals(Urgencia.ALTA, result.urgencia());

        assertNotNull(repository.feedbackSalvo);
        assertEquals(result.id(), repository.feedbackSalvo.id());

        assertEquals(List.of("primeira", "segunda", "terceira"), notificacoesExecutadas);

        assertEquals(3, notificationLogRepository.logs.size());
        assertEquals(FeedbackNotificationStatus.ENVIADA, notificationLogRepository.logs.get(0).status());
        assertEquals(FeedbackNotificationStatus.FALHA, notificationLogRepository.logs.get(1).status());
        assertEquals(FeedbackNotificationStatus.ENVIADA, notificationLogRepository.logs.get(2).status());
        assertEquals("Falha simulada na segunda notificação", notificationLogRepository.logs.get(1).mensagemErro());
    }

    @Test
    @DisplayName("Deve manter a criação mesmo quando o registro do log falhar")
    void deveManterCriacaoMesmoQuandoRegistroDoLogFalhar() {
        // Arrange
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FeedbackNotificationLogRepositoryPort notificationLogRepository = new FeedbackNotificationLogRepositoryPort() {
            @Override
            public FeedbackNotificationLog save(FeedbackNotificationLog notificationLog) {
                throw new RuntimeException("Falha simulada ao gravar log");
            }

            @Override
            public List<FeedbackNotificationLog> findByFeedbackId(UUID feedbackId) {
                return List.of();
            }
        };
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.ALTA;
        List<Feedback> notificacoes = new ArrayList<>();
        Notification<Feedback> notification = notificacoes::add;

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                notificationLogRepository,
                classifier,
                List.of(notification));

        CreateFeedbackCommand command = new CreateFeedbackCommand("Sistema com bug", 1);

        // Act
        FeedbackCreatedResult result = assertDoesNotThrow(() -> useCase.execute(command));

        // Assert
        assertEquals(Urgencia.ALTA, result.urgencia());
        assertNotNull(repository.feedbackSalvo);
        assertEquals(result.id(), repository.feedbackSalvo.id());
        assertEquals(1, notificacoes.size());
    }

    @Test
    @DisplayName("Deve falhar quando o comando for nulo")
    void deveFalharQuandoCommandForNulo() {
        // Arrange
        FakeFeedbackRepository repository = new FakeFeedbackRepository();
        FakeFeedbackNotificationLogRepository notificationLogRepository = new FakeFeedbackNotificationLogRepository();
        FeedbackUrgenciaClassifier classifier = (descricao, nota) -> Urgencia.BAIXA;

        CreateFeedbackUseCase useCase = new CreateFeedbackUseCase(
                repository,
                notificationLogRepository,
                classifier,
                List.of());

        // Act
        Executable action = () -> useCase.execute(null);

        // Assert
        assertThrows(NullPointerException.class, action);
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

    private static class FakeFeedbackNotificationLogRepository implements FeedbackNotificationLogRepositoryPort {

        private final List<FeedbackNotificationLog> logs = new ArrayList<>();

        @Override
        public FeedbackNotificationLog save(FeedbackNotificationLog notificationLog) {
            logs.add(notificationLog);
            return notificationLog;
        }

        @Override
        public List<FeedbackNotificationLog> findByFeedbackId(UUID feedbackId) {
            return logs.stream()
                    .filter(log -> log.feedbackId().equals(feedbackId))
                    .toList();
        }
    }
}
