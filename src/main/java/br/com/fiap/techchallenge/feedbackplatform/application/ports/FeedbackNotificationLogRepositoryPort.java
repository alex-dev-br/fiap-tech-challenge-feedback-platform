package br.com.fiap.techchallenge.feedbackplatform.application.ports;

import br.com.fiap.techchallenge.feedbackplatform.domain.model.FeedbackNotificationLog;

public interface FeedbackNotificationLogRepositoryPort {

    FeedbackNotificationLog save(FeedbackNotificationLog notificationLog);
}
