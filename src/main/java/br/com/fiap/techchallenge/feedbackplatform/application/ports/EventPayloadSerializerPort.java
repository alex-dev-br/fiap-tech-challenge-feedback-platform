package br.com.fiap.techchallenge.feedbackplatform.application.ports;

public interface EventPayloadSerializerPort {

    String serialize(Object payload);
}
