package br.com.fiap.techchallenge.feedbackplatform.application.dto;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.FeedbackNotificationLog;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FeedbackNotificationLogResult(
        UUID id,
        UUID feedbackId,
        FeedbackNotificationType tipo,
        FeedbackNotificationStatus status,
        OffsetDateTime dataTentativa,
        String mensagemErro) {

    public static FeedbackNotificationLogResult from(FeedbackNotificationLog notificationLog) {
        return new FeedbackNotificationLogResult(
                notificationLog.id(),
                notificationLog.feedbackId(),
                notificationLog.tipo(),
                notificationLog.status(),
                notificationLog.dataTentativa(),
                notificationLog.mensagemErro());
    }
}
