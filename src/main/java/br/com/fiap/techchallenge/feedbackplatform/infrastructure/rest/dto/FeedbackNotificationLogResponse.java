package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest.dto;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackNotificationLogResult;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FeedbackNotificationLogResponse(
        UUID id,
        UUID feedbackId,
        FeedbackNotificationType tipo,
        FeedbackNotificationStatus status,
        OffsetDateTime dataTentativa,
        String mensagemErro) {

    public static FeedbackNotificationLogResponse from(FeedbackNotificationLogResult result) {
        return new FeedbackNotificationLogResponse(
                result.id(),
                result.feedbackId(),
                result.tipo(),
                result.status(),
                result.dataTentativa(),
                result.mensagemErro());
    }
}
