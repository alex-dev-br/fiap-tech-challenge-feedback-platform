package br.com.fiap.techchallenge.feedbackplatform.application.dto;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FeedbackCreatedEvent(
        UUID id,
        String descricao,
        int nota,
        Urgencia urgencia,
        OffsetDateTime dataCriacao
) {
}
