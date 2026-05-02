package br.com.fiap.techchallenge.feedbackplatform.application.dto;

public record PublishOutboxEventsResult(
        int totalEncontrados,
        int publicados,
        int falhas
) {
}
