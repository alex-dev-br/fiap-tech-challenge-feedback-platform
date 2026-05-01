package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.StatusOutboxEvent;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record OutboxEvent(
        UUID id,
        UUID aggregateId,
        String eventType,
        String payload,
        StatusOutboxEvent status,
        OffsetDateTime createdAt,
        OffsetDateTime publishedAt,
        int retryCount
) {

    public OutboxEvent {
        Objects.requireNonNull(id, "id é obrigatório");
        Objects.requireNonNull(aggregateId, "aggregateId é obrigatório");
        validarTexto(eventType, "eventType");
        validarTexto(payload, "payload");
        Objects.requireNonNull(status, "status é obrigatório");
        Objects.requireNonNull(createdAt, "createdAt é obrigatório");

        if (retryCount < 0) {
            throw new IllegalArgumentException("retryCount não pode ser negativo.");
        }
    }

    public static OutboxEvent pendente(UUID aggregateId, String eventType, String payload) {
        return new OutboxEvent(
                UUID.randomUUID(),
                aggregateId,
                eventType,
                payload,
                StatusOutboxEvent.PENDENTE,
                OffsetDateTime.now(),
                null,
                0
        );
    }

    public OutboxEvent marcarComoPublicado(OffsetDateTime publishedAt) {
        Objects.requireNonNull(publishedAt, "publishedAt é obrigatório");

        return new OutboxEvent(
                id,
                aggregateId,
                eventType,
                payload,
                StatusOutboxEvent.PUBLICADO,
                createdAt,
                publishedAt,
                retryCount
        );
    }

    public OutboxEvent registrarFalha() {
        return new OutboxEvent(
                id,
                aggregateId,
                eventType,
                payload,
                StatusOutboxEvent.FALHA,
                createdAt,
                publishedAt,
                retryCount + 1
        );
    }

    private static void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
    }
}
