package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.mapper;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationStatus;
import br.com.fiap.techchallenge.feedbackplatform.domain.enums.FeedbackNotificationType;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.FeedbackNotificationLog;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity.FeedbackNotificationLogEntity;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FeedbackNotificationLogPersistenceMapperTest {

    private final FeedbackNotificationLogPersistenceMapper mapper = new FeedbackNotificationLogPersistenceMapper();

    @Test
    void deveConverterDominioParaEntidade() {
        UUID id = UUID.randomUUID();
        UUID feedbackId = UUID.randomUUID();
        OffsetDateTime dataTentativa = OffsetDateTime.now();

        FeedbackNotificationLog notificationLog = new FeedbackNotificationLog(
                id,
                feedbackId,
                FeedbackNotificationType.EMAIL,
                FeedbackNotificationStatus.FALHA,
                dataTentativa,
                "Falha ao enviar e-mail");

        FeedbackNotificationLogEntity entity = mapper.toEntity(notificationLog);

        assertEquals(id, entity.getId());
        assertEquals(feedbackId, entity.getFeedbackId());
        assertEquals(FeedbackNotificationType.EMAIL, entity.getTipo());
        assertEquals(FeedbackNotificationStatus.FALHA, entity.getStatus());
        assertEquals(dataTentativa, entity.getDataTentativa());
        assertEquals("Falha ao enviar e-mail", entity.getMensagemErro());
    }

    @Test
    void deveConverterEntidadeParaDominio() {
        UUID id = UUID.randomUUID();
        UUID feedbackId = UUID.randomUUID();
        OffsetDateTime dataTentativa = OffsetDateTime.now();

        FeedbackNotificationLogEntity entity = new FeedbackNotificationLogEntity();
        entity.setId(id);
        entity.setFeedbackId(feedbackId);
        entity.setTipo(FeedbackNotificationType.EMAIL);
        entity.setStatus(FeedbackNotificationStatus.ENVIADA);
        entity.setDataTentativa(dataTentativa);
        entity.setMensagemErro(null);

        FeedbackNotificationLog notificationLog = mapper.toDomain(entity);

        assertEquals(id, notificationLog.id());
        assertEquals(feedbackId, notificationLog.feedbackId());
        assertEquals(FeedbackNotificationType.EMAIL, notificationLog.tipo());
        assertEquals(FeedbackNotificationStatus.ENVIADA, notificationLog.status());
        assertEquals(dataTentativa, notificationLog.dataTentativa());
        assertNull(notificationLog.mensagemErro());
    }
}
