package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackNotificationLogResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackNotificationLogRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.FeedbackNotificationLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Caso de uso de consulta de logs de notificação")
class ListFeedbackNotificationLogsUseCaseTest {

    @Test
    @DisplayName("Deve listar logs de notificação do feedback informado")
    void deveListarLogsDoFeedbackInformado() {
        // Arrange
        UUID feedbackId = UUID.randomUUID();
        FakeFeedbackNotificationLogRepository repository = new FakeFeedbackNotificationLogRepository();

        FeedbackNotificationLog logEnviado = new FeedbackNotificationLog(
                UUID.randomUUID(),
                feedbackId,
                FeedbackNotificationType.EMAIL,
                FeedbackNotificationStatus.ENVIADA,
                OffsetDateTime.parse("2026-05-16T10:00:00Z"),
                null);

        FeedbackNotificationLog logFalha = new FeedbackNotificationLog(
                UUID.randomUUID(),
                feedbackId,
                FeedbackNotificationType.EMAIL,
                FeedbackNotificationStatus.FALHA,
                OffsetDateTime.parse("2026-05-16T11:00:00Z"),
                "Timeout ao enviar e-mail");

        repository.logs.add(logEnviado);
        repository.logs.add(logFalha);

        ListFeedbackNotificationLogsUseCase useCase = new ListFeedbackNotificationLogsUseCase(repository);

        // Act
        List<FeedbackNotificationLogResult> result = useCase.execute(feedbackId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(logEnviado.id(), result.get(0).id());
        assertEquals(feedbackId, result.get(0).feedbackId());
        assertEquals(FeedbackNotificationType.EMAIL, result.get(0).tipo());
        assertEquals(FeedbackNotificationStatus.ENVIADA, result.get(0).status());
        assertEquals(logEnviado.dataTentativa(), result.get(0).dataTentativa());
        assertEquals(null, result.get(0).mensagemErro());

        assertEquals(logFalha.id(), result.get(1).id());
        assertEquals(feedbackId, result.get(1).feedbackId());
        assertEquals(FeedbackNotificationType.EMAIL, result.get(1).tipo());
        assertEquals(FeedbackNotificationStatus.FALHA, result.get(1).status());
        assertEquals(logFalha.dataTentativa(), result.get(1).dataTentativa());
        assertEquals("Timeout ao enviar e-mail", result.get(1).mensagemErro());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver logs para o feedback")
    void deveRetornarListaVaziaQuandoNaoHouverLogsParaOFeedback() {
        // Arrange
        UUID feedbackId = UUID.randomUUID();
        FakeFeedbackNotificationLogRepository repository = new FakeFeedbackNotificationLogRepository();
        ListFeedbackNotificationLogsUseCase useCase = new ListFeedbackNotificationLogsUseCase(repository);

        // Act
        List<FeedbackNotificationLogResult> result = useCase.execute(feedbackId);

        // Assert
        assertEquals(List.of(), result);
    }

    @Test
    @DisplayName("Deve falhar quando feedbackId for nulo")
    void deveFalharQuandoFeedbackIdForNulo() {
        // Arrange
        FakeFeedbackNotificationLogRepository repository = new FakeFeedbackNotificationLogRepository();
        ListFeedbackNotificationLogsUseCase useCase = new ListFeedbackNotificationLogsUseCase(repository);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));
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
