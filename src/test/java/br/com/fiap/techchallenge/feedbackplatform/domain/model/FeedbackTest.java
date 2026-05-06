package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackUrgenciaClassifier;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackTest {

    private final FeedbackUrgenciaClassifier baixaClassifier = (descricao, nota) -> Urgencia.BAIXA;

    @Test
    void deveCriarNovoFeedbackComDadosValidos() {
        Feedback feedback = Feedback.novo("A aula foi muito boa", 9, baixaClassifier);

        assertNotNull(feedback.id());
        assertEquals("A aula foi muito boa", feedback.descricao());
        assertEquals(9, feedback.nota());
        assertEquals(Urgencia.BAIXA, feedback.urgencia());
        assertNotNull(feedback.dataCriacao());
    }

    @Test
    void deveIndicarQueFeedbackEhUrgenteQuandoUrgenciaForAlta() {
        Feedback feedback = new Feedback(
                UUID.randomUUID(),
                "Sistema travando",
                2,
                Urgencia.ALTA,
                OffsetDateTime.now()
        );

        assertTrue(feedback.isUrgente());
    }

    @Test
    void deveIndicarQueFeedbackNaoEhUrgenteQuandoUrgenciaNaoForAlta() {
        Feedback feedback = new Feedback(
                UUID.randomUUID(),
                "Aula razoável",
                6,
                Urgencia.MEDIA,
                OffsetDateTime.now()
        );

        assertFalse(feedback.isUrgente());
    }

    @Test
    void deveFalharQuandoDescricaoForNula() {
        assertThrows(IllegalArgumentException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        null,
                        8,
                        Urgencia.BAIXA,
                        OffsetDateTime.now()
                )
        );
    }

    @Test
    void deveFalharQuandoDescricaoForVazia() {
        assertThrows(IllegalArgumentException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        "   ",
                        8,
                        Urgencia.BAIXA,
                        OffsetDateTime.now()
                )
        );
    }

    @Test
    void deveFalharQuandoNotaForMenorQueZero() {
        assertThrows(IllegalArgumentException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        "Comentário válido",
                        -1,
                        Urgencia.BAIXA,
                        OffsetDateTime.now()
                )
        );
    }

    @Test
    void deveFalharQuandoNotaForMaiorQueDez() {
        assertThrows(IllegalArgumentException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        "Comentário válido",
                        11,
                        Urgencia.BAIXA,
                        OffsetDateTime.now()
                )
        );
    }

    @Test
    void deveFalharQuandoClassifierForNulo() {
        assertThrows(NullPointerException.class, () ->
                Feedback.novo("Comentário válido", 8, null)
        );
    }
}
