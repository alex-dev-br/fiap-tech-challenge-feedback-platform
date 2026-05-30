package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.mapper;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Mapper de persistência de feedback")
class FeedbackPersistenceMapperTest {

    private final FeedbackPersistenceMapper mapper = new FeedbackPersistenceMapper();

    @Test
    @DisplayName("Deve converter domínio para entidade")
    void deveConverterDominioParaEntidade() {
        // Arrange
        UUID id = UUID.randomUUID();
        OffsetDateTime dataCriacao = OffsetDateTime.parse("2026-05-16T15:30:00Z");

        Feedback feedback = new Feedback(
                id,
                "A plataforma está travando durante a aula",
                8,
                Urgencia.ALTA,
                dataCriacao);

        // Act
        FeedbackEntity entity = mapper.toEntity(feedback);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals("A plataforma está travando durante a aula", entity.getDescricao());
        assertEquals(8, entity.getNota());
        assertEquals(Urgencia.ALTA, entity.getUrgencia());
        assertEquals(dataCriacao, entity.getDataCriacao());
    }

    @Test
    @DisplayName("Deve converter entidade para domínio")
    void deveConverterEntidadeParaDominio() {
        // Arrange
        UUID id = UUID.randomUUID();
        OffsetDateTime dataCriacao = OffsetDateTime.parse("2026-05-16T15:30:00Z");

        FeedbackEntity entity = new FeedbackEntity();
        entity.setId(id);
        entity.setDescricao("Aula muito boa");
        entity.setNota(9);
        entity.setUrgencia(Urgencia.BAIXA);
        entity.setDataCriacao(dataCriacao);

        // Act
        Feedback feedback = mapper.toDomain(entity);

        // Assert
        assertEquals(id, feedback.id());
        assertEquals("Aula muito boa", feedback.descricao());
        assertEquals(9, feedback.nota());
        assertEquals(Urgencia.BAIXA, feedback.urgencia());
        assertEquals(dataCriacao, feedback.dataCriacao());
    }
}
