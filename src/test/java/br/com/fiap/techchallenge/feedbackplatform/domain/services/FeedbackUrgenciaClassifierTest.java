package br.com.fiap.techchallenge.feedbackplatform.domain.services;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedbackUrgenciaClassifierTest {

    private final FeedbackUrgenciaClassifier classifier = new FeedbackUrgenciaClassifier();

    @Test
    void deveClassificarComoAltaQuandoNotaForEntreZeroETres() {
        Urgencia urgencia = classifier.classificar("A aula foi razoável", 2);

        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    void deveClassificarComoAltaQuandoDescricaoContiverPalavraCriticaMesmoComNotaAlta() {
        Urgencia urgencia = classifier.classificar("O sistema está travando muito", 8);

        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    void deveClassificarComoMediaQuandoNotaForEntreQuatroESeis() {
        Urgencia urgencia = classifier.classificar("A aula foi boa", 5);

        assertEquals(Urgencia.MEDIA, urgencia);
    }

    @Test
    void deveClassificarComoBaixaQuandoNotaForEntreSeteEDezSemPalavraCritica() {
        Urgencia urgencia = classifier.classificar("A experiência foi muito boa", 9);

        assertEquals(Urgencia.BAIXA, urgencia);
    }

    @Test
    void deveFalharQuandoNotaForInvalida() {
        assertThrows(IllegalArgumentException.class, () -> classifier.classificar("Teste", 11));
    }
}
