package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.CreateFeedbackCommand;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackUrgenciaClassifier;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.Notification;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;

public class CreateFeedbackUseCase {

    private final FeedbackRepositoryPort feedbackRepository;
    private final FeedbackUrgenciaClassifier urgenciaClassifier;
    private final List<Notification<Feedback>> notification;

    public CreateFeedbackUseCase(FeedbackRepositoryPort feedbackRepository,
            FeedbackUrgenciaClassifier urgenciaClassifier, List<Notification<Feedback>> notification) {
        this.feedbackRepository = Objects.requireNonNull(feedbackRepository);
        this.urgenciaClassifier = Objects.requireNonNull(urgenciaClassifier);
        this.notification = Objects.requireNonNull(notification);
    }

    public FeedbackCreatedResult execute(CreateFeedbackCommand command) {
        Objects.requireNonNull(command, "command é obrigatório");

        Feedback feedback = Feedback.novo(
                command.descricao(),
                command.nota(),
                urgenciaClassifier);
        Feedback feedbackSalvo = feedbackRepository.save(feedback);

        if (feedbackSalvo.isUrgente()) {
            this.notification.forEach(notification -> notification.send(feedbackSalvo));
        }

        return FeedbackCreatedResult.from(feedbackSalvo);
    }
}
