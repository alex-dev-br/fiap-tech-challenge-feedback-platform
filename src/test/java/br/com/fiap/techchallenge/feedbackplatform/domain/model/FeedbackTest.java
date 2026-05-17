package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackUrgenciaClassifier;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Deve indicar feedback urgente quando a urgência for ALTA")
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
    @DisplayName("Deve indicar feedback não urgente quando a urgência não for ALTA")
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
    @DisplayName("Deve falhar quando a descrição for nula")
    void deveFalharQuandoDescricaoForNula() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        Executable action = () -> new Feedback(
                id,
                null,
                8,
                Urgencia.BAIXA,
                OffsetDateTime.now());

        // Assert
        assertThrows(IllegalArgumentException.class, action);
    }

    @Test
    @DisplayName("Deve falhar quando a descrição estiver vazia")
    void deveFalharQuandoDescricaoForVazia() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        Executable action = () -> new Feedback(
                id,
                "   ",
                8,
                Urgencia.BAIXA,
                OffsetDateTime.now());

        // Assert
        assertThrows(IllegalArgumentException.class, action);
    }

    @Test
    @DisplayName("Deve falhar quando a nota for menor que zero")
    void deveFalharQuandoNotaForMenorQueZero() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        Executable action = () -> new Feedback(
                id,
                "Comentário válido",
                -1,
                Urgencia.BAIXA,
                OffsetDateTime.now());

        // Assert
        assertThrows(IllegalArgumentException.class, action);
    }

    @Test
    @DisplayName("Deve falhar quando a nota for maior que dez")
    void deveFalharQuandoNotaForMaiorQueDez() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        Executable action = () -> new Feedback(
                id,
                "Comentário válido",
                11,
                Urgencia.BAIXA,
                OffsetDateTime.now());

        // Assert
        assertThrows(IllegalArgumentException.class, action);
    }

    @Test
    @DisplayName("Deve falhar quando o classificador for nulo")
    void deveFalharQuandoClassifierForNulo() {
        // Arrange
        String descricao = "Comentário válido";
        int nota = 8;

        // Act
        Executable action = () -> Feedback.novo(descricao, nota, null);

        // Assert
        assertThrows(NullPointerException.class, action);
    }
}
