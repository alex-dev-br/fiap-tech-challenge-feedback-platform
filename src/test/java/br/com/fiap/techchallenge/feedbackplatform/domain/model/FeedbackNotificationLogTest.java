package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Domínio de log de notificação de feedback")
class FeedbackNotificationLogTest {

    @Test
    @DisplayName("Deve criar log de notificação enviada")
    void deveCriarLogDeNotificacaoEnviada() {
        // Arrange
        UUID feedbackId = UUID.randomUUID();

        // Act
        FeedbackNotificationLog log = FeedbackNotificationLog.enviada(
                feedbackId,
                FeedbackNotificationType.EMAIL);

        // Assert
        assertNotNull(log.id());
        assertEquals(feedbackId, log.feedbackId());
        assertEquals(FeedbackNotificationType.EMAIL, log.tipo());
        assertEquals(FeedbackNotificationStatus.ENVIADA, log.status());
        assertNotNull(log.dataTentativa());
        assertNull(log.mensagemErro());
    }

    @Test
    @DisplayName("Deve criar log de notificação com falha")
    void deveCriarLogDeNotificacaoComFalha() {
        // Arrange
        UUID feedbackId = UUID.randomUUID();
        String mensagemErro = "Timeout ao enviar e-mail";

        // Act
        FeedbackNotificationLog log = FeedbackNotificationLog.falha(
                feedbackId,
                FeedbackNotificationType.EMAIL,
                mensagemErro);

        // Assert
        assertNotNull(log.id());
        assertEquals(feedbackId, log.feedbackId());
        assertEquals(FeedbackNotificationType.EMAIL, log.tipo());
        assertEquals(FeedbackNotificationStatus.FALHA, log.status());
        assertNotNull(log.dataTentativa());
        assertEquals(mensagemErro, log.mensagemErro());
    }

    @Test
    @DisplayName("Deve falhar quando o id for nulo")
    void deveFalharQuandoIdForNulo() {
        // Arrange
        UUID id = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new FeedbackNotificationLog(
                        id,
                        UUID.randomUUID(),
                        FeedbackNotificationType.EMAIL,
                        FeedbackNotificationStatus.ENVIADA,
                        OffsetDateTime.now(),
                        null));
    }

    @Test
    @DisplayName("Deve falhar quando o feedbackId for nulo")
    void deveFalharQuandoFeedbackIdForNulo() {
        // Arrange
        UUID feedbackId = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                FeedbackNotificationLog.enviada(feedbackId, FeedbackNotificationType.EMAIL));
    }

    @Test
    @DisplayName("Deve falhar quando o tipo for nulo")
    void deveFalharQuandoTipoForNulo() {
        // Arrange
        FeedbackNotificationType tipo = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                FeedbackNotificationLog.enviada(UUID.randomUUID(), tipo));
    }

    @Test
    @DisplayName("Deve falhar quando o status for nulo")
    void deveFalharQuandoStatusForNulo() {
        // Arrange
        FeedbackNotificationStatus status = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new FeedbackNotificationLog(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        FeedbackNotificationType.EMAIL,
                        status,
                        OffsetDateTime.now(),
                        null));
    }

    @Test
    @DisplayName("Deve falhar quando a data de tentativa for nula")
    void deveFalharQuandoDataTentativaForNula() {
        // Arrange
        OffsetDateTime dataTentativa = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new FeedbackNotificationLog(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        FeedbackNotificationType.EMAIL,
                        FeedbackNotificationStatus.ENVIADA,
                        dataTentativa,
                        null));
    }

    @Test
    @DisplayName("Deve falhar quando a mensagem de erro estiver vazia para status FALHA")
    void deveFalharQuandoMensagemErroForVaziaParaFalha() {
        // Arrange
        String mensagemErro = "   ";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                FeedbackNotificationLog.falha(
                        UUID.randomUUID(),
                        FeedbackNotificationType.EMAIL,
                        mensagemErro));
    }

    @Test
    @DisplayName("Deve normalizar mensagem vazia para nula quando status for ENVIADA")
    void deveNormalizarMensagemErroVaziaParaNullQuandoStatusForEnviada() {
        // Arrange
        String mensagemErro = "   ";

        // Act
        FeedbackNotificationLog log = new FeedbackNotificationLog(
                UUID.randomUUID(),
                UUID.randomUUID(),
                FeedbackNotificationType.EMAIL,
                FeedbackNotificationStatus.ENVIADA,
                OffsetDateTime.now(),
                mensagemErro);

        // Assert
        assertNull(log.mensagemErro());
    }

    @Test
    @DisplayName("Deve limitar mensagem de erro em dois mil caracteres")
    void deveLimitarMensagemErroEmDoisMilCaracteres() {
        // Arrange
        String mensagemLonga = "x".repeat(2500);

        // Act
        FeedbackNotificationLog log = FeedbackNotificationLog.falha(
                UUID.randomUUID(),
                FeedbackNotificationType.EMAIL,
                mensagemLonga);

        // Assert
        assertEquals(2000, log.mensagemErro().length());
    }

    @Test
    @DisplayName("Deve remover espaços da mensagem de erro")
    void deveRemoverEspacosDaMensagemErro() {
        // Arrange
        String mensagemErro = "   Erro ao enviar e-mail   ";

        // Act
        FeedbackNotificationLog log = FeedbackNotificationLog.falha(
                UUID.randomUUID(),
                FeedbackNotificationType.EMAIL,
                mensagemErro);

        // Assert
        assertEquals("Erro ao enviar e-mail", log.mensagemErro());
    }
}
