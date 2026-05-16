package br.com.fiap.techchallenge.feedbackplatform.infrastructure.classifier;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackUrgenciaClassifier;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.classifier.rules.CriticalKeywordMatcher;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;

@ApplicationScoped
public class FeedbackUrgenciaClassifierAdapter implements FeedbackUrgenciaClassifier {

    private final CriticalKeywordMatcher criticalKeywordMatcher;

    public FeedbackUrgenciaClassifierAdapter() {
        this(new CriticalKeywordMatcher());
    }

    public FeedbackUrgenciaClassifierAdapter(CriticalKeywordMatcher criticalKeywordMatcher) {
        this.criticalKeywordMatcher = Objects.requireNonNull(criticalKeywordMatcher);
    }

    @Override
    public Urgencia classificar(String descricao, int nota) throws IllegalArgumentException {
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
