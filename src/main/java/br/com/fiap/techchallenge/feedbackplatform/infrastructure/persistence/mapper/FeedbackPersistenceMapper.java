package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.mapper;

import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FeedbackPersistenceMapper {

    public FeedbackEntity toEntity(Feedback feedback) {
        FeedbackEntity entity = new FeedbackEntity();
        entity.setId(feedback.id());
        entity.setDescricao(feedback.descricao());
        entity.setNota(feedback.nota());
        entity.setUrgencia(feedback.urgencia());
        entity.setDataCriacao(feedback.dataCriacao());
        entity.setAlertaEnviado(feedback.alertaEnviado());
        entity.setDataEnvioAlerta(feedback.dataEnvioAlerta());
        entity.setStatusProcessamento(feedback.statusProcessamento());
        return entity;
    }

    public Feedback toDomain(FeedbackEntity entity) {
        return new Feedback(
                entity.getId(),
                entity.getDescricao(),
                entity.getNota(),
                entity.getUrgencia(),
                entity.getDataCriacao(),
                entity.isAlertaEnviado(),
                entity.getDataEnvioAlerta(),
                entity.getStatusProcessamento()
        );
    }
}
