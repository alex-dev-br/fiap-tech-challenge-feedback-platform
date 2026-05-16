package br.com.fiap.techchallenge.feedbackplatform.application.ports;

import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;

import java.util.Optional;
import java.util.UUID;

public interface FeedbackRepositoryPort {

    Feedback save(Feedback feedback);

    Optional<Feedback> findById(UUID id);
}
