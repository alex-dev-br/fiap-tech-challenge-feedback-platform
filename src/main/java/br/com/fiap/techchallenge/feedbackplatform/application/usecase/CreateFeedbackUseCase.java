package br.com.fiap.techchallenge.feedbackplatform.application.usecase;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.CreateFeedbackCommand;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedEvent;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.EventPayloadSerializerPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.FeedbackRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.application.ports.OutboxEventRepositoryPort;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.OutboxEvent;
import br.com.fiap.techchallenge.feedbackplatform.domain.services.FeedbackUrgenciaClassifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Objects;

@ApplicationScoped
public class CreateFeedbackUseCase {

    private static final String FEEDBACK_CREATED_EVENT_TYPE = "feedback.created";

    private final FeedbackRepositoryPort feedbackRepository;
    private final OutboxEventRepositoryPort outboxEventRepository;
    private final EventPayloadSerializerPort eventPayloadSerializer;
    private final FeedbackUrgenciaClassifier urgenciaClassifier;

    @Inject
    public CreateFeedbackUseCase(
            FeedbackRepositoryPort feedbackRepository,
            OutboxEventRepositoryPort outboxEventRepository,
            EventPayloadSerializerPort eventPayloadSerializer
    ) {
        this(
                feedbackRepository,
                outboxEventRepository,
                eventPayloadSerializer,
                new FeedbackUrgenciaClassifier()
        );
    }

    CreateFeedbackUseCase(
            FeedbackRepositoryPort feedbackRepository,
            OutboxEventRepositoryPort outboxEventRepository,
            EventPayloadSerializerPort eventPayloadSerializer,
            FeedbackUrgenciaClassifier urgenciaClassifier
    ) {
        this.feedbackRepository = Objects.requireNonNull(feedbackRepository);
        this.outboxEventRepository = Objects.requireNonNull(outboxEventRepository);
        this.eventPayloadSerializer = Objects.requireNonNull(eventPayloadSerializer);
        this.urgenciaClassifier = Objects.requireNonNull(urgenciaClassifier);
    }

    @Transactional
    public FeedbackCreatedResult execute(CreateFeedbackCommand command) {
        Objects.requireNonNull(command, "command é obrigatório");

        Feedback feedback = Feedback.novo(
                command.descricao(),
                command.nota(),
                urgenciaClassifier
        );

        Feedback feedbackSalvo = feedbackRepository.save(feedback);

        FeedbackCreatedEvent event = new FeedbackCreatedEvent(
                feedbackSalvo.id(),
                feedbackSalvo.descricao(),
                feedbackSalvo.nota(),
                feedbackSalvo.urgencia(),
                feedbackSalvo.dataCriacao()
        );

        String payload = eventPayloadSerializer.serialize(event);

        OutboxEvent outboxEvent = OutboxEvent.pendente(
                feedbackSalvo.id(),
                FEEDBACK_CREATED_EVENT_TYPE,
                payload
        );

        outboxEventRepository.save(outboxEvent);

        return FeedbackCreatedResult.from(feedbackSalvo);
    }
}
