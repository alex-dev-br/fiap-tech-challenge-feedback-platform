package br.com.fiap.techchallenge.feedbackplatform.infrastructure.messaging;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.EventPayloadSerializerPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JacksonEventPayloadSerializerAdapter implements EventPayloadSerializerPort {

    private final ObjectMapper objectMapper;

    public JacksonEventPayloadSerializerAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível serializar o payload do evento.", exception);
        }
    }
}
