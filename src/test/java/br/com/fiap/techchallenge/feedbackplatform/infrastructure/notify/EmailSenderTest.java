package br.com.fiap.techchallenge.feedbackplatform.infrastructure.notify;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailSenderTest {

    @Test
    void deveExtrairEmailsSeparadosPorPontoEVirgulaEVirgulaAplicandoTrim() {
        List<String> emails = EmailSender.extrairEmailsAdministradores(
                "admin1@example.com; admin2@example.com, admin3@example.com");

        assertEquals(
                List.of(
                        "admin1@example.com",
                        "admin2@example.com",
                        "admin3@example.com"),
                emails);
    }

    @Test
    void deveIgnorarEmailsVazios() {
        List<String> emails = EmailSender.extrairEmailsAdministradores(
                "admin1@example.com; ; , admin2@example.com,, ");

        assertEquals(
                List.of(
                        "admin1@example.com",
                        "admin2@example.com"),
                emails);
    }

    @Test
    void deveRemoverEmailsDuplicados() {
        List<String> emails = EmailSender.extrairEmailsAdministradores(
                "admin@example.com; admin@example.com, outro@example.com");

        assertEquals(
                List.of(
                        "admin@example.com",
                        "outro@example.com"),
                emails);
    }

    @Test
    void deveRetornarListaVaziaQuandoEmailsNaoForemConfigurados() {
        assertEquals(List.of(), EmailSender.extrairEmailsAdministradores(null));
        assertEquals(List.of(), EmailSender.extrairEmailsAdministradores(""));
        assertEquals(List.of(), EmailSender.extrairEmailsAdministradores("   "));
    }

    @Test
    void deveEscaparConteudoHtml() {
        String descricao = "<script>alert('x')</script> & \"teste\"";

        String descricaoEscapada = EmailSender.escaparHtml(descricao);

        assertEquals(
                "&lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt; &amp; &quot;teste&quot;",
                descricaoEscapada);
    }

    @Test
    void deveRetornarStringVaziaQuandoValorHtmlForNulo() {
        assertEquals("", EmailSender.escaparHtml(null));
    }

    @Test
    void deveMontarCorpoDoEmailComDescricaoEscapada() {
        Feedback feedback = new Feedback(
                UUID.randomUUID(),
                "<b>Erro crítico</b> & problema no sistema",
                2,
                Urgencia.ALTA,
                OffsetDateTime.parse("2026-05-16T15:30:00Z"));

        String body = EmailSender.montarCorpoEmail(feedback);

        assertFalse(body.contains("<b>Erro crítico</b>"));
        assertFalse(body.contains("& problema no sistema"));

        assertTrue(body.contains("&lt;b&gt;Erro crítico&lt;/b&gt; &amp; problema no sistema"));
        assertTrue(body.contains("ALTA"));
        assertTrue(body.contains("16/05/2026"));
    }
}
