package br.com.fiap.techchallenge.feedbackplatform.infrastructure.classifier;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedbackUrgenciaClassifierAdapterTest {

    private final FeedbackUrgenciaClassifierAdapter classifier = new FeedbackUrgenciaClassifierAdapter();

    @Test
    void deveClassificarComoAltaQuandoNotaForZero() {
        Urgencia urgencia = classifier.classificar("Aula ruim", 0);

        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    void deveClassificarComoAltaQuandoNotaForTres() {
        Urgencia urgencia = classifier.classificar("Aula abaixo do esperado", 3);

        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    void deveClassificarComoAltaQuandoDescricaoContiverPalavraCritica() {
        Urgencia urgencia = classifier.classificar("O sistema está travando muito", 8);

        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    void deveClassificarComoAltaMesmoComPalavraCriticaAcentuada() {
        Urgencia urgencia = classifier.classificar("A experiência foi péssima", 9);

        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    void deveClassificarComoMediaQuandoNotaForQuatro() {
        Urgencia urgencia = classifier.classificar("Aula regular", 4);

        assertEquals(Urgencia.MEDIA, urgencia);
    }

    @Test
    void deveClassificarComoMediaQuandoNotaForSeis() {
        Urgencia urgencia = classifier.classificar("Aula razoável", 6);

        assertEquals(Urgencia.MEDIA, urgencia);
    }

    @Test
    void deveClassificarComoBaixaQuandoNotaForSeteSemPalavraCritica() {
        Urgencia urgencia = classifier.classificar("Aula boa", 7);

        assertEquals(Urgencia.BAIXA, urgencia);
    }

    @Test
    void deveClassificarComoBaixaQuandoNotaForDezSemPalavraCritica() {
        Urgencia urgencia = classifier.classificar("Aula excelente", 10);

        assertEquals(Urgencia.BAIXA, urgencia);
    }

    @Test
    void deveFalharQuandoNotaForMenorQueZero() {
        assertThrows(IllegalArgumentException.class,
                () -> classifier.classificar("Teste", -1));
    }

    @Test
    void deveFalharQuandoNotaForMaiorQueDez() {
        assertThrows(IllegalArgumentException.class,
                () -> classifier.classificar("Teste", 11));
    }
}
