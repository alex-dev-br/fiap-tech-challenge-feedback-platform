package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackNotificationLogResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackNotificationLogRepositoryPort;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListFeedbackNotificationLogsUseCase {

    private final FeedbackNotificationLogRepositoryPort notificationLogRepository;

    public ListFeedbackNotificationLogsUseCase(
            FeedbackNotificationLogRepositoryPort notificationLogRepository) {
        this.notificationLogRepository = Objects.requireNonNull(notificationLogRepository);
    }

    public List<FeedbackNotificationLogResult> execute(UUID feedbackId) {
        Objects.requireNonNull(feedbackId, "feedbackId é obrigatório");

        return notificationLogRepository.findByFeedbackId(feedbackId)
                .stream()
                .map(FeedbackNotificationLogResult::from)
                .toList();
    }
}
