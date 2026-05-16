package br.com.fiap.techchallenge.feedbackplatform.infrastructure.config;

import java.util.List;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackUrgenciaClassifier;
import br.com.fiap.techchallenge.feedbackplatform.application.usecase.CreateFeedbackUseCase;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.notify.EmailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class Producers {

    @Produces
    @ApplicationScoped
    public CreateFeedbackUseCase createFeedbackUseCaseProducer(
            FeedbackRepositoryPort feedbackRepository,
            FeedbackUrgenciaClassifier urgenciaClassifier,
            EmailSender emailSender) {

        return new CreateFeedbackUseCase(
                feedbackRepository,
                urgenciaClassifier,
                List.of(emailSender::send));
    }
}
