package br.com.fiap.techchallenge.feedbackplatform.domain.model;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.StatusProcessamentoFeedback;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.services.FeedbackUrgenciaClassifier;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record Feedback(
        UUID id,
        String descricao,
        int nota,
        Urgencia urgencia,
        OffsetDateTime dataCriacao,
        boolean alertaEnviado,
        OffsetDateTime dataEnvioAlerta,
        StatusProcessamentoFeedback statusProcessamento
) {

    public Feedback {
        Objects.requireNonNull(id, "id é obrigatório");
        validarDescricao(descricao);
        validarNota(nota);
        Objects.requireNonNull(urgencia, "urgencia é obrigatória");
        Objects.requireNonNull(dataCriacao, "dataCriacao é obrigatória");
        Objects.requireNonNull(statusProcessamento, "statusProcessamento é obrigatório");
    }

    public static Feedback novo(String descricao, int nota, FeedbackUrgenciaClassifier classifier) {
        Objects.requireNonNull(classifier, "classifier é obrigatório");

        Urgencia urgenciaCalculada = classifier.classificar(descricao, nota);

        return new Feedback(
                UUID.randomUUID(),
                descricao,
                nota,
                urgenciaCalculada,
                OffsetDateTime.now(),
                false,
                null,
                StatusProcessamentoFeedback.PENDENTE
        );
    }

    public Feedback marcarAlertaEnviado(OffsetDateTime dataEnvioAlerta) {
        Objects.requireNonNull(dataEnvioAlerta, "dataEnvioAlerta é obrigatória");

        return new Feedback(
                id,
                descricao,
                nota,
                urgencia,
                dataCriacao,
                true,
                dataEnvioAlerta,
                StatusProcessamentoFeedback.PROCESSADO
        );
    }

    public Feedback atualizarStatus(StatusProcessamentoFeedback novoStatus) {
        Objects.requireNonNull(novoStatus, "novoStatus é obrigatório");

        return new Feedback(
                id,
                descricao,
                nota,
                urgencia,
                dataCriacao,
                alertaEnviado,
                dataEnvioAlerta,
                novoStatus
        );
    }

    private static void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória.");
        }
    }

    private static void validarNota(int nota) {
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10.");
        }
    }
}
