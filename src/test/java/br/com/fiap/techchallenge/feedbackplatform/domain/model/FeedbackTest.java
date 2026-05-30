package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackUrgenciaClassifier;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Domínio de feedback")
class FeedbackTest {

    private final FeedbackUrgenciaClassifier baixaClassifier = (descricao, nota) -> Urgencia.BAIXA;

    @Test
    @DisplayName("Deve criar novo feedback com dados válidos")
    void deveCriarNovoFeedbackComDadosValidos() {
        // Arrange
        String descricao = "A aula foi muito boa";
        int nota = 9;

        // Act
        Feedback feedback = Feedback.novo(descricao, nota, baixaClassifier);

        // Assert
        assertNotNull(feedback.id());
        assertEquals(descricao, feedback.descricao());
        assertEquals(nota, feedback.nota());
        assertEquals(Urgencia.BAIXA, feedback.urgencia());
        assertNotNull(feedback.dataCriacao());
    }

    @Test
    @DisplayName("Deve indicar que o feedback é urgente quando a urgência for ALTA")
    void deveIndicarQueFeedbackEhUrgenteQuandoUrgenciaForAlta() {
        // Arrange
        Feedback feedback = new Feedback(
                UUID.randomUUID(),
                "Sistema travando",
                2,
                Urgencia.ALTA,
                OffsetDateTime.now());

        // Act
        boolean urgente = feedback.isUrgente();

        // Assert
        assertTrue(urgente);
    }

    @Test
    @DisplayName("Deve indicar que o feedback não é urgente quando a urgência não for ALTA")
    void deveIndicarQueFeedbackNaoEhUrgenteQuandoUrgenciaNaoForAlta() {
        // Arrange
        Feedback feedback = new Feedback(
                UUID.randomUUID(),
                "Aula razoável",
                6,
                Urgencia.MEDIA,
                OffsetDateTime.now());

        // Act
        boolean urgente = feedback.isUrgente();

        // Assert
        assertFalse(urgente);
    }

    @Test
    @DisplayName("Deve falhar quando o id for nulo")
    void deveFalharQuandoIdForNulo() {
        // Arrange
        UUID id = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new Feedback(
                        id,
                        "Comentário válido",
                        8,
                        Urgencia.BAIXA,
                        OffsetDateTime.now()));
    }

    @Test
    @DisplayName("Deve falhar quando a descrição for nula")
    void deveFalharQuandoDescricaoForNula() {
        // Arrange
        String descricao = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        descricao,
                        8,
                        Urgencia.BAIXA,
                        OffsetDateTime.now()));
    }

    @Test
    @DisplayName("Deve falhar quando a descrição estiver vazia")
    void deveFalharQuandoDescricaoForVazia() {
        // Arrange
        String descricao = "   ";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        descricao,
                        8,
                        Urgencia.BAIXA,
                        OffsetDateTime.now()));
    }

    @Test
    @DisplayName("Deve falhar quando a nota for menor que zero")
    void deveFalharQuandoNotaForMenorQueZero() {
        // Arrange
        int nota = -1;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        "Comentário válido",
                        nota,
                        Urgencia.BAIXA,
                        OffsetDateTime.now()));
    }

    @Test
    @DisplayName("Deve falhar quando a nota for maior que dez")
    void deveFalharQuandoNotaForMaiorQueDez() {
        // Arrange
        int nota = 11;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        "Comentário válido",
                        nota,
                        Urgencia.BAIXA,
                        OffsetDateTime.now()));
    }

    @Test
    @DisplayName("Deve falhar quando a urgência for nula")
    void deveFalharQuandoUrgenciaForNula() {
        // Arrange
        Urgencia urgencia = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        "Comentário válido",
                        8,
                        urgencia,
                        OffsetDateTime.now()));
    }

    @Test
    @DisplayName("Deve falhar quando a data de criação for nula")
    void deveFalharQuandoDataCriacaoForNula() {
        // Arrange
        OffsetDateTime dataCriacao = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new Feedback(
                        UUID.randomUUID(),
                        "Comentário válido",
                        8,
                        Urgencia.BAIXA,
                        dataCriacao));
    }

    @Test
    @DisplayName("Deve falhar quando o classificador for nulo")
    void deveFalharQuandoClassifierForNulo() {
        // Arrange
        FeedbackUrgenciaClassifier classifier = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                Feedback.novo("Comentário válido", 8, classifier));
    }
}
