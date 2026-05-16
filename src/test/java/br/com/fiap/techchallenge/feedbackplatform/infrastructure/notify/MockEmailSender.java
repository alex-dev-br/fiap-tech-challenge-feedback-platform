package br.com.fiap.techchallenge.feedbackplatform.infrastructure.notify;

import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mock
@ApplicationScoped
public class MockEmailSender extends EmailSender {

    private static final Logger LOG = LoggerFactory.getLogger(MockEmailSender.class);

    @Override
    public void send(Feedback feedback) {
        LOG.info("[MOCK] Simulação de envio de e-mail para: {}", feedback.descricao());
    }
}
