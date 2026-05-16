package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.CreateFeedbackCommand;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackUrgenciaClassifier;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.Notification;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;

public class CreateFeedbackUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(CreateFeedbackUseCase.class);

    private final FeedbackRepositoryPort feedbackRepository;
    private final FeedbackUrgenciaClassifier urgenciaClassifier;
    private final List<Notification<Feedback>> notifications;

    public CreateFeedbackUseCase(
            FeedbackRepositoryPort feedbackRepository,
            FeedbackUrgenciaClassifier urgenciaClassifier,
            List<Notification<Feedback>> notifications) {
        this.feedbackRepository = Objects.requireNonNull(feedbackRepository);
        this.urgenciaClassifier = Objects.requireNonNull(urgenciaClassifier);
        this.notifications = Objects.requireNonNull(notifications);
    }

    public FeedbackCreatedResult execute(CreateFeedbackCommand command) {
        Objects.requireNonNull(command, "command é obrigatório");

        Feedback feedback = Feedback.novo(
                command.descricao(),
                command.nota(),
                urgenciaClassifier);

        Feedback feedbackSalvo = feedbackRepository.save(feedback);

        if (feedbackSalvo.isUrgente()) {
            enviarNotificacoesSemInterromperCriacao(feedbackSalvo);
        }

        return FeedbackCreatedResult.from(feedbackSalvo);
    }

    private void enviarNotificacoesSemInterromperCriacao(Feedback feedback) {
        for (Notification<Feedback> notification : notifications) {
            try {
                notification.send(feedback);
            } catch (RuntimeException exception) {
                LOG.error(
                        "Falha ao enviar notificação do feedback {}. A criação do feedback será mantida.",
                        feedback.id(),
                        exception);
            }
        }
    }
}
