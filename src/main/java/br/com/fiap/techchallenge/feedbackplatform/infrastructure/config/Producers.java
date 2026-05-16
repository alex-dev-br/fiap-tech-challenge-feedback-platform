package br.com.fiap.techchallenge.feedbackplatform.infrastructure.config;

import java.util.List;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.usecase.CreateFeedbackUseCase;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.classifier.FeedbackUrgenciaClassifierAdapter;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.notify.EmailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class Producers {

    @Produces
    @ApplicationScoped
    public CreateFeedbackUseCase createFeedbackUseCaseProducer(
            FeedbackRepositoryPort feedbackRepository, EmailSender emailSender) {
        return new CreateFeedbackUseCase(feedbackRepository, new FeedbackUrgenciaClassifierAdapter(),
                List.of((feedback) -> emailSender.send(feedback)));
    }
}
