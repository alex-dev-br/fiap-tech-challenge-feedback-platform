package br.com.fiap.techchallenge.feedbackplatform.application.ports;

import br.com.fiap.techchallenge.feedbackplatform.domain.model.FeedbackNotificationLog;

import java.util.List;
import java.util.UUID;

public interface FeedbackNotificationLogRepositoryPort {

    FeedbackNotificationLog save(FeedbackNotificationLog notificationLog);

    List<FeedbackNotificationLog> findByFeedbackId(UUID feedbackId);
}
