package br.com.fiap.techchallenge.feedbackplatform.infrastructure.notify;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Envio de e-mail de notificação")
class EmailSenderTest {

    @Test
    @DisplayName("Deve extrair e-mails separados por ponto e vírgula e vírgula aplicando trim")
    void deveExtrairEmailsSeparadosPorPontoEVirgulaEVirgulaAplicandoTrim() {
        // Arrange
        String emailsConfigurados = "admin1@example.com; admin2@example.com, admin3@example.com";

        // Act
        List<String> emails = EmailSender.extrairEmailsAdministradores(emailsConfigurados);

        // Assert
        assertEquals(
                List.of(
                        "admin1@example.com",
                        "admin2@example.com",
                        "admin3@example.com"),
                emails);
    }

    @Test
    @DisplayName("Deve ignorar e-mails vazios")
    void deveIgnorarEmailsVazios() {
        // Arrange
        String emailsConfigurados = "admin1@example.com; ; , admin2@example.com,, ";

        // Act
        List<String> emails = EmailSender.extrairEmailsAdministradores(emailsConfigurados);

        // Assert
        assertEquals(
                List.of(
                        "admin1@example.com",
                        "admin2@example.com"),
                emails);
    }

    @Test
    @DisplayName("Deve remover e-mails duplicados")
    void deveRemoverEmailsDuplicados() {
        // Arrange
        String emailsConfigurados = "admin@example.com; admin@example.com, outro@example.com";

        // Act
        List<String> emails = EmailSender.extrairEmailsAdministradores(emailsConfigurados);

        // Assert
        assertEquals(
                List.of(
                        "admin@example.com",
                        "outro@example.com"),
                emails);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando e-mails não forem configurados")
    void deveRetornarListaVaziaQuandoEmailsNaoForemConfigurados() {
        // Arrange
        String emailsNulos = null;
        String emailsVazios = "";
        String emailsEmBranco = "   ";

        // Act
        List<String> resultadoNulo = EmailSender.extrairEmailsAdministradores(emailsNulos);
        List<String> resultadoVazio = EmailSender.extrairEmailsAdministradores(emailsVazios);
        List<String> resultadoEmBranco = EmailSender.extrairEmailsAdministradores(emailsEmBranco);

        // Assert
        assertEquals(List.of(), resultadoNulo);
        assertEquals(List.of(), resultadoVazio);
        assertEquals(List.of(), resultadoEmBranco);
    }

    @Test
    @DisplayName("Deve escapar conteúdo HTML")
    void deveEscaparConteudoHtml() {
        // Arrange
        String descricao = "<script>alert('x')</script> & \"teste\"";

        // Act
        String descricaoEscapada = EmailSender.escaparHtml(descricao);

        // Assert
        assertEquals(
                "&lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt; &amp; &quot;teste&quot;",
                descricaoEscapada);
    }

    @Test
    @DisplayName("Deve retornar string vazia quando valor HTML for nulo")
    void deveRetornarStringVaziaQuandoValorHtmlForNulo() {
        // Arrange
        String valor = null;

        // Act
        String resultado = EmailSender.escaparHtml(valor);

        // Assert
        assertEquals("", resultado);
    }

    @Test
    @DisplayName("Deve montar corpo do e-mail com descrição escapada")
    void deveMontarCorpoDoEmailComDescricaoEscapada() {
        // Arrange
        Feedback feedback = new Feedback(
                UUID.randomUUID(),
                "<b>Erro crítico</b> & problema no sistema",
                2,
                Urgencia.ALTA,
                OffsetDateTime.parse("2026-05-16T15:30:00Z"));

        // Act
        String body = EmailSender.montarCorpoEmail(feedback);

        // Assert
        assertFalse(body.contains("<b>Erro crítico</b>"));
        assertFalse(body.contains("& problema no sistema"));

        assertTrue(body.contains("&lt;b&gt;Erro crítico&lt;/b&gt; &amp; problema no sistema"));
        assertTrue(body.contains("ALTA"));
        assertTrue(body.contains("16/05/2026"));
    }
}
