package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackEntity;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackNotificationLogEntity;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository.PanacheFeedbackNotificationLogRepository;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository.PanacheFeedbackRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class AdminFeedbackNotificationLogResourceTest {

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
    @TestSecurity(user = "adminUser", roles = { "ADMIN" })
    @JwtSecurity(claims = { @Claim(key = "groups", value = "ADMIN") })
    void deveListarLogsDeNotificacaoDoFeedbackQuandoUsuarioForAdmin() {
        UUID feedbackId = criarFeedbackComLogs();

        given()
                .when()
                .get("/admin/feedbacks/{feedbackId}/notificacoes", feedbackId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].id", notNullValue())
                .body("[0].feedbackId", equalTo(feedbackId.toString()))
                .body("[0].tipo", equalTo("EMAIL"))
                .body("[0].status", equalTo("FALHA"))
                .body("[0].mensagemErro", equalTo("Timeout ao enviar e-mail"))
                .body("[1].id", notNullValue())
                .body("[1].feedbackId", equalTo(feedbackId.toString()))
                .body("[1].tipo", equalTo("EMAIL"))
                .body("[1].status", equalTo("ENVIADA"))
                .body("[1].mensagemErro", nullValue());
    }

    @Test
    @TestSecurity(user = "adminUser", roles = { "ADMIN" })
    @JwtSecurity(claims = { @Claim(key = "groups", value = "ADMIN") })
    void deveRetornarListaVaziaQuandoFeedbackNaoPossuirLogs() {
        UUID feedbackId = criarFeedbackSemLogs();

        given()
                .when()
                .get("/admin/feedbacks/{feedbackId}/notificacoes", feedbackId)
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @TestSecurity(user = "alunoUser", roles = { "ALUNO" })
    @JwtSecurity(claims = { @Claim(key = "groups", value = "ALUNO") })
    void deveRetornarForbiddenQuandoUsuarioForAluno() {
        UUID feedbackId = criarFeedbackComLogs();

        given()
                .when()
                .get("/admin/feedbacks/{feedbackId}/notificacoes", feedbackId)
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "", roles = {})
    void deveRetornarUnauthorizedQuandoUsuarioNaoEstiverAutenticado() {
        UUID feedbackId = criarFeedbackComLogs();

        given()
                .when()
                .get("/admin/feedbacks/{feedbackId}/notificacoes", feedbackId)
                .then()
                .statusCode(401);
    }

    private UUID criarFeedbackComLogs() {
        UUID feedbackId = criarFeedbackSemLogs();

        QuarkusTransaction.requiringNew().run(() -> {
            FeedbackNotificationLogEntity logEnviado = criarLog(
                    feedbackId,
                    FeedbackNotificationStatus.ENVIADA,
                    OffsetDateTime.parse("2026-05-16T10:00:00Z"),
                    null);

            FeedbackNotificationLogEntity logFalha = criarLog(
                    feedbackId,
                    FeedbackNotificationStatus.FALHA,
                    OffsetDateTime.parse("2026-05-16T11:00:00Z"),
                    "Timeout ao enviar e-mail");

            notificationLogRepository.persist(logEnviado);
            notificationLogRepository.persist(logFalha);
        });

        return feedbackId;
    }

    private UUID criarFeedbackSemLogs() {
        UUID feedbackId = UUID.randomUUID();

        QuarkusTransaction.requiringNew().run(() -> {
            FeedbackEntity feedback = new FeedbackEntity();
            feedback.setId(feedbackId);
            feedback.setDescricao("Sistema com erro crítico");
            feedback.setNota(2);
            feedback.setUrgencia(Urgencia.ALTA);
            feedback.setDataCriacao(OffsetDateTime.parse("2026-05-16T09:00:00Z"));

            feedbackRepository.persist(feedback);
        });

        return feedbackId;
    }

    private FeedbackNotificationLogEntity criarLog(
            UUID feedbackId,
            FeedbackNotificationStatus status,
            OffsetDateTime dataTentativa,
            String mensagemErro) {

        FeedbackNotificationLogEntity log = new FeedbackNotificationLogEntity();
        log.setId(UUID.randomUUID());
        log.setFeedbackId(feedbackId);
        log.setTipo(FeedbackNotificationType.EMAIL);
        log.setStatus(status);
        log.setDataTentativa(dataTentativa);
        log.setMensagemErro(mensagemErro);
        return log;
    }
}
