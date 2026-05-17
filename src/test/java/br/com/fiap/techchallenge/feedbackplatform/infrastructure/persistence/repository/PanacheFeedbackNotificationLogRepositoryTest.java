package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackEntity;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackNotificationLogEntity;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@DisplayName("Repository Panache de logs de notificação")
class PanacheFeedbackNotificationLogRepositoryTest {

    @Inject
    PanacheFeedbackRepository feedbackRepository;

    @Inject
    PanacheFeedbackNotificationLogRepository notificationLogRepository;

    @BeforeEach
    void limparBanco() {
        QuarkusTransaction.requiringNew().run(() -> {
            notificationLogRepository.deleteAll();
            feedbackRepository.deleteAll();
        });
    }

    @Test
    @DisplayName("Deve listar logs do feedback ordenados por data de tentativa decrescente")
    void deveListarLogsDoFeedbackOrdenadosPorDataTentativaDecrescente() {
        // Arrange
        UUID feedbackId = UUID.randomUUID();
        UUID outroFeedbackId = UUID.randomUUID();

        UUID logMaisAntigoId = UUID.randomUUID();
        UUID logMaisRecenteId = UUID.randomUUID();
        UUID logIntermediarioId = UUID.randomUUID();

        QuarkusTransaction.requiringNew().run(() -> {
            feedbackRepository.persist(criarFeedback(feedbackId, "Sistema com erro crítico"));
            feedbackRepository.persist(criarFeedback(outroFeedbackId, "Outro feedback crítico"));

            notificationLogRepository.persist(criarLog(
                    logMaisAntigoId,
                    feedbackId,
                    FeedbackNotificationStatus.ENVIADA,
                    OffsetDateTime.parse("2026-05-16T09:00:00Z"),
                    null));

            notificationLogRepository.persist(criarLog(
                    logMaisRecenteId,
                    feedbackId,
                    FeedbackNotificationStatus.FALHA,
                    OffsetDateTime.parse("2026-05-16T11:00:00Z"),
                    "Timeout ao enviar e-mail"));

            notificationLogRepository.persist(criarLog(
                    logIntermediarioId,
                    feedbackId,
                    FeedbackNotificationStatus.ENVIADA,
                    OffsetDateTime.parse("2026-05-16T10:00:00Z"),
                    null));

            notificationLogRepository.persist(criarLog(
                    UUID.randomUUID(),
                    outroFeedbackId,
                    FeedbackNotificationStatus.ENVIADA,
                    OffsetDateTime.parse("2026-05-16T12:00:00Z"),
                    null));
        });

        // Act
        List<FeedbackNotificationLogEntity> logs = notificationLogRepository.findByFeedbackId(feedbackId);

        // Assert
        assertEquals(3, logs.size());
        assertEquals(logMaisRecenteId, logs.get(0).getId());
        assertEquals(OffsetDateTime.parse("2026-05-16T11:00:00Z"), logs.get(0).getDataTentativa());
        assertEquals(FeedbackNotificationStatus.FALHA, logs.get(0).getStatus());

        assertEquals(logIntermediarioId, logs.get(1).getId());
        assertEquals(OffsetDateTime.parse("2026-05-16T10:00:00Z"), logs.get(1).getDataTentativa());
        assertEquals(FeedbackNotificationStatus.ENVIADA, logs.get(1).getStatus());

        assertEquals(logMaisAntigoId, logs.get(2).getId());
        assertEquals(OffsetDateTime.parse("2026-05-16T09:00:00Z"), logs.get(2).getDataTentativa());
        assertEquals(FeedbackNotificationStatus.ENVIADA, logs.get(2).getStatus());
    }

    private FeedbackEntity criarFeedback(UUID id, String descricao) {
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setId(id);
        feedback.setDescricao(descricao);
        feedback.setNota(2);
        feedback.setUrgencia(Urgencia.ALTA);
        feedback.setDataCriacao(OffsetDateTime.parse("2026-05-16T08:00:00Z"));
        return feedback;
    }

    private FeedbackNotificationLogEntity criarLog(
            UUID id,
            UUID feedbackId,
            FeedbackNotificationStatus status,
            OffsetDateTime dataTentativa,
            String mensagemErro) {

        FeedbackNotificationLogEntity log = new FeedbackNotificationLogEntity();
        log.setId(id);
        log.setFeedbackId(feedbackId);
        log.setTipo(FeedbackNotificationType.EMAIL);
        log.setStatus(status);
        log.setDataTentativa(dataTentativa);
        log.setMensagemErro(mensagemErro);
        return log;
    }
}
