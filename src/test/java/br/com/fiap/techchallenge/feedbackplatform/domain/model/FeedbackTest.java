package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.StatusProcessamentoFeedback;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.services.FeedbackUrgenciaClassifier;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackTest {

    private final FeedbackUrgenciaClassifier classifier = new FeedbackUrgenciaClassifier();

    @Test
    void deveCriarFeedbackComValoresIniciaisEsperados() {
        Feedback feedback = Feedback.novo("A plataforma está travando", 8, classifier);

        assertNotNull(feedback.id());
        assertEquals("A plataforma está travando", feedback.descricao());
        assertEquals(8, feedback.nota());
        assertEquals(Urgencia.ALTA, feedback.urgencia());
        assertNotNull(feedback.dataCriacao());
        assertFalse(feedback.alertaEnviado());
        assertNull(feedback.dataEnvioAlerta());
        assertEquals(StatusProcessamentoFeedback.PENDENTE, feedback.statusProcessamento());
    }

    @Test
    void deveFalharAoCriarFeedbackComDescricaoVazia() {
        assertThrows(IllegalArgumentException.class,
                () -> Feedback.novo("   ", 5, classifier));
    }

    @Test
    void deveMarcarAlertaComoEnviado() {
        Feedback feedback = Feedback.novo("Erro crítico na aula", 3, classifier);
        OffsetDateTime dataEnvio = OffsetDateTime.now();

        Feedback atualizado = feedback.marcarAlertaEnviado(dataEnvio);

        assertTrue(atualizado.alertaEnviado());
        assertEquals(dataEnvio, atualizado.dataEnvioAlerta());
        assertEquals(StatusProcessamentoFeedback.PROCESSADO, atualizado.statusProcessamento());
    }
}
