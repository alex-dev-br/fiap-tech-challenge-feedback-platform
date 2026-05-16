package br.com.fiap.techchallenge.feedbackplatform.application.ports;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;

public interface FeedbackUrgenciaClassifier {
    Urgencia classificar(String descricao, int nota) throws IllegalArgumentException;
}
