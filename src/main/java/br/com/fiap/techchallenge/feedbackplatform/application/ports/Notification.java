package br.com.fiap.techchallenge.feedbackplatform.application.ports;

public interface Notification<T> {
    void send(T body);
}
