package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;

public record FeedbackNotificationLog(
        UUID id,
        UUID feedbackId,
        FeedbackNotificationType tipo,
        FeedbackNotificationStatus status,
        OffsetDateTime dataTentativa,
        String mensagemErro) {

    private static final int TAMANHO_MAXIMO_MENSAGEM_ERRO = 2000;

    public FeedbackNotificationLog {
        Objects.requireNonNull(id, "id é obrigatório");
        Objects.requireNonNull(feedbackId, "feedbackId é obrigatório");
        Objects.requireNonNull(tipo, "tipo é obrigatório");
        Objects.requireNonNull(status, "status é obrigatório");
        Objects.requireNonNull(dataTentativa, "dataTentativa é obrigatória");

        if (FeedbackNotificationStatus.FALHA.equals(status)
                && (mensagemErro == null || mensagemErro.isBlank())) {
            throw new IllegalArgumentException("mensagemErro é obrigatória para notificações com falha.");
        }

        mensagemErro = normalizarMensagemErro(mensagemErro);
    }

    public static FeedbackNotificationLog enviada(UUID feedbackId, FeedbackNotificationType tipo) {
        return new FeedbackNotificationLog(
                UUID.randomUUID(),
                feedbackId,
                tipo,
                FeedbackNotificationStatus.ENVIADA,
                OffsetDateTime.now(),
                null);
    }

    public static FeedbackNotificationLog falha(
            UUID feedbackId,
            FeedbackNotificationType tipo,
            String mensagemErro) {
        return new FeedbackNotificationLog(
                UUID.randomUUID(),
                feedbackId,
                tipo,
                FeedbackNotificationStatus.FALHA,
                OffsetDateTime.now(),
                mensagemErro);
    }

    private static String normalizarMensagemErro(String mensagemErro) {
        if (mensagemErro == null || mensagemErro.isBlank()) {
            return null;
        }

        String mensagemNormalizada = mensagemErro.trim();

        if (mensagemNormalizada.length() <= TAMANHO_MAXIMO_MENSAGEM_ERRO) {
            return mensagemNormalizada;
        }

        return mensagemNormalizada.substring(0, TAMANHO_MAXIMO_MENSAGEM_ERRO);
    }
}
