package br.com.fiap.techchallenge.feedbackplatform.application.dto;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FeedbackCreatedResult(
        UUID id,
        String descricao,
        int nota,
        Urgencia urgencia,
        OffsetDateTime dataCriacao
) {

    public static FeedbackCreatedResult from(Feedback feedback) {
        return new FeedbackCreatedResult(
                feedback.id(),
                feedback.descricao(),
                feedback.nota(),
                feedback.urgencia(),
                feedback.dataCriacao()
        );
    }
}
