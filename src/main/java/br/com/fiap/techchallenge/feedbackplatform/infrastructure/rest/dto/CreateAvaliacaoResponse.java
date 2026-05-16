package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest.dto;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record CreateAvaliacaoResponse(
        UUID id,
        String descricao,
        int nota,
        Urgencia urgencia,
        OffsetDateTime dataCriacao) {

    private static final ZoneId ZONE_ID_SP = ZoneId.of("America/Sao_Paulo");

    public static CreateAvaliacaoResponse from(FeedbackCreatedResult result) {
        return new CreateAvaliacaoResponse(
                result.id(),
                result.descricao(),
                result.nota(),
                result.urgencia(),
                result.dataCriacao().atZoneSameInstant(ZONE_ID_SP).toOffsetDateTime());
    }
}
