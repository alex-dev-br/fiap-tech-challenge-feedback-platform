package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Deve falhar quando o feedbackId for nulo")
    void deveFalharQuandoFeedbackIdForNulo() {
        // Arrange
        FeedbackNotificationType tipo = FeedbackNotificationType.EMAIL;

        // Act
        Executable action = () -> FeedbackNotificationLog.enviada(null, tipo);

        // Assert
        assertThrows(NullPointerException.class, action);
    }

    @Test
    @DisplayName("Deve falhar quando o tipo for nulo")
    void deveFalharQuandoTipoForNulo() {
        // Arrange
        UUID feedbackId = UUID.randomUUID();

        // Act
        Executable action = () -> FeedbackNotificationLog.enviada(feedbackId, null);

        // Assert
        assertThrows(NullPointerException.class, action);
    }

    @Test
    @DisplayName("Deve falhar quando a mensagem de erro estiver vazia para falha")
    void deveFalharQuandoMensagemErroForVaziaParaFalha() {
        // Arrange
        UUID feedbackId = UUID.randomUUID();

        // Act
        Executable action = () -> FeedbackNotificationLog.falha(
                feedbackId,
                FeedbackNotificationType.EMAIL,
                "   ");

        // Assert
        assertThrows(IllegalArgumentException.class, action);
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
    @DisplayName("Deve remover espaços no início e no fim da mensagem de erro")
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
