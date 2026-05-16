package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackNotificationLogTest {

    @Test
    void deveCriarLogDeNotificacaoEnviada() {
        UUID feedbackId = UUID.randomUUID();

        FeedbackNotificationLog log = FeedbackNotificationLog.enviada(
                feedbackId,
                FeedbackNotificationType.EMAIL);

        assertNotNull(log.id());
        assertEquals(feedbackId, log.feedbackId());
        assertEquals(FeedbackNotificationType.EMAIL, log.tipo());
        assertEquals(FeedbackNotificationStatus.ENVIADA, log.status());
        assertNotNull(log.dataTentativa());
        assertNull(log.mensagemErro());
    }

    @Test
    void deveCriarLogDeNotificacaoComFalha() {
        UUID feedbackId = UUID.randomUUID();

        FeedbackNotificationLog log = FeedbackNotificationLog.falha(
                feedbackId,
                FeedbackNotificationType.EMAIL,
                "Timeout ao enviar e-mail");

        assertNotNull(log.id());
        assertEquals(feedbackId, log.feedbackId());
        assertEquals(FeedbackNotificationType.EMAIL, log.tipo());
        assertEquals(FeedbackNotificationStatus.FALHA, log.status());
        assertNotNull(log.dataTentativa());
        assertEquals("Timeout ao enviar e-mail", log.mensagemErro());
    }

    @Test
    void deveFalharQuandoFeedbackIdForNulo() {
        assertThrows(NullPointerException.class, () ->
                FeedbackNotificationLog.enviada(null, FeedbackNotificationType.EMAIL));
    }

    @Test
    void deveFalharQuandoTipoForNulo() {
        assertThrows(NullPointerException.class, () ->
                FeedbackNotificationLog.enviada(UUID.randomUUID(), null));
    }

    @Test
    void deveFalharQuandoMensagemErroForVaziaParaFalha() {
        assertThrows(IllegalArgumentException.class, () ->
                FeedbackNotificationLog.falha(
                        UUID.randomUUID(),
                        FeedbackNotificationType.EMAIL,
                        "   "));
    }

    @Test
    void deveLimitarMensagemErroEmDoisMilCaracteres() {
        String mensagemLonga = "x".repeat(2500);

        FeedbackNotificationLog log = FeedbackNotificationLog.falha(
                UUID.randomUUID(),
                FeedbackNotificationType.EMAIL,
                mensagemLonga);

        assertEquals(2000, log.mensagemErro().length());
    }

    @Test
    void deveRemoverEspacosDaMensagemErro() {
        FeedbackNotificationLog log = FeedbackNotificationLog.falha(
                UUID.randomUUID(),
                FeedbackNotificationType.EMAIL,
                "   Erro ao enviar e-mail   ");

        assertEquals("Erro ao enviar e-mail", log.mensagemErro());
    }
}
