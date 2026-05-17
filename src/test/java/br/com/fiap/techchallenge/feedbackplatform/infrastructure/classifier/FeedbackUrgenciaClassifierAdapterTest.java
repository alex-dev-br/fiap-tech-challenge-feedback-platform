package br.com.fiap.techchallenge.feedbackplatform.infrastructure.classifier;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Classificador de urgência de feedback")
class FeedbackUrgenciaClassifierAdapterTest {

    private final FeedbackUrgenciaClassifierAdapter classifier = new FeedbackUrgenciaClassifierAdapter();

    @Test
    @DisplayName("Deve classificar como ALTA quando a nota for zero")
    void deveClassificarComoAltaQuandoNotaForZero() {
        // Arrange
        String descricao = "Aula ruim";
        int nota = 0;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como ALTA quando a nota for três")
    void deveClassificarComoAltaQuandoNotaForTres() {
        // Arrange
        String descricao = "Aula abaixo do esperado";
        int nota = 3;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como ALTA quando a descrição contiver palavra crítica")
    void deveClassificarComoAltaQuandoDescricaoContiverPalavraCritica() {
        // Arrange
        String descricao = "O sistema está travando muito";
        int nota = 8;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como ALTA mesmo com palavra crítica acentuada")
    void deveClassificarComoAltaMesmoComPalavraCriticaAcentuada() {
        // Arrange
        String descricao = "A experiência foi péssima";
        int nota = 9;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como ALTA ignorando diferenças entre maiúsculas e minúsculas")
    void deveClassificarComoAltaIgnorandoCaixaAltaECaixaBaixa() {
        // Arrange
        String descricao = "O sistema apresentou BUG durante a aula";
        int nota = 10;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como ALTA quando a descrição contiver 'não funciona' com acento")
    void deveClassificarComoAltaQuandoDescricaoContiverNaoFuncionaComAcento() {
        // Arrange
        String descricao = "A plataforma NÃO funciona corretamente";
        int nota = 9;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.ALTA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como MÉDIA quando a nota for quatro")
    void deveClassificarComoMediaQuandoNotaForQuatro() {
        // Arrange
        String descricao = "Aula regular";
        int nota = 4;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.MEDIA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como MÉDIA quando a nota for seis")
    void deveClassificarComoMediaQuandoNotaForSeis() {
        // Arrange
        String descricao = "Aula razoável";
        int nota = 6;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.MEDIA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como BAIXA quando a nota for sete e não houver palavra crítica")
    void deveClassificarComoBaixaQuandoNotaForSeteSemPalavraCritica() {
        // Arrange
        String descricao = "Aula boa";
        int nota = 7;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.BAIXA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como BAIXA quando a nota for dez e não houver palavra crítica")
    void deveClassificarComoBaixaQuandoNotaForDezSemPalavraCritica() {
        // Arrange
        String descricao = "Aula excelente";
        int nota = 10;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.BAIXA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como BAIXA quando a descrição for nula e a nota for alta")
    void deveClassificarComoBaixaQuandoDescricaoForNulaENotaAlta() {
        // Arrange
        String descricao = null;
        int nota = 9;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.BAIXA, urgencia);
    }

    @Test
    @DisplayName("Deve classificar como BAIXA quando a descrição for vazia e a nota for alta")
    void deveClassificarComoBaixaQuandoDescricaoForVaziaENotaAlta() {
        // Arrange
        String descricao = "   ";
        int nota = 9;

        // Act
        Urgencia urgencia = classifier.classificar(descricao, nota);

        // Assert
        assertEquals(Urgencia.BAIXA, urgencia);
    }

    @Test
    @DisplayName("Deve falhar quando a nota for menor que zero")
    void deveFalharQuandoNotaForMenorQueZero() {
        // Arrange
        int nota = -1;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> classifier.classificar("Teste", nota));
    }

    @Test
    @DisplayName("Deve falhar quando a nota for maior que dez")
    void deveFalharQuandoNotaForMaiorQueDez() {
        // Arrange
        int nota = 11;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> classifier.classificar("Teste", nota));
    }
}
