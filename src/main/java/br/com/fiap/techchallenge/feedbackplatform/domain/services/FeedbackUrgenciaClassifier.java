package br.com.fiap.techchallenge.feedbackplatform.domain.services;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.rules.CriticalKeywordMatcher;

import java.util.Objects;

public class FeedbackUrgenciaClassifier {

    private final CriticalKeywordMatcher criticalKeywordMatcher;

    public FeedbackUrgenciaClassifier() {
        this(new CriticalKeywordMatcher());
    }

    public FeedbackUrgenciaClassifier(CriticalKeywordMatcher criticalKeywordMatcher) {
        this.criticalKeywordMatcher = Objects.requireNonNull(criticalKeywordMatcher);
    }

    public Urgencia classificar(String descricao, int nota) {
        validarNota(nota);

        if (nota <= 3 || criticalKeywordMatcher.contemPalavraCritica(descricao)) {
            return Urgencia.ALTA;
        }

        if (nota <= 6) {
            return Urgencia.MEDIA;
        }

        return Urgencia.BAIXA;
    }

    private void validarNota(int nota) {
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10.");
        }
    }
}
