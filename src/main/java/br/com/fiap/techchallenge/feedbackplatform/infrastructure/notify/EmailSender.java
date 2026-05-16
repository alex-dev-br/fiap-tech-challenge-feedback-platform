package br.com.fiap.techchallenge.feedbackplatform.infrastructure.notify;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;

import br.com.fiap.techchallenge.feedbackplatform.application.ports.Notification;
import br.com.fiap.techchallenge.feedbackplatform.domain.model.Feedback;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailSender implements Notification<Feedback> {

    private static final Logger LOG = LoggerFactory.getLogger(EmailSender.class);

    private static final ZoneId ZONE_ID_SP = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Inject
    @ConfigProperty(name = "app.email.connection-string")
    String connectionString;

    @Inject
    @ConfigProperty(name = "app.admin-emails")
    String adminEmails;

    @Inject
    @ConfigProperty(name = "app.email.sender-address")
    String senderAddress;

    @Inject
    @ConfigProperty(name = "app.email.subject")
    String subject;

    @Override
    public void send(Feedback feedback) {
        EmailClient emailClient = new EmailClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        List<EmailAddress> addresses = extrairEmailsAdministradores(adminEmails)
                .stream()
                .map(EmailAddress::new)
                .toList();

        if (addresses.isEmpty()) {
            throw new IllegalStateException("Nenhum e-mail de administrador configurado em app.admin-emails.");
        }

        String bodyFormatted = montarCorpoEmail(feedback);

        EmailMessage emailMessage = new EmailMessage()
                .setSenderAddress(senderAddress)
                .setToRecipients(addresses)
                .setSubject(subject)
                .setBodyHtml(bodyFormatted);

        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
        PollResponse<EmailSendResult> result = poller.waitForCompletion(java.time.Duration.ofSeconds(10));

        LOG.info("Email sent with message Id: {}", result.getValue().getId());
    }

    static String montarCorpoEmail(Feedback feedback) {
        String body = """
                <html>
                    <body>
                        <h1>Feedback crítico recebido</h1>
                        <p><strong>Descrição:</strong> %s</p>
                        <p><strong>Urgência:</strong> %s</p>
                        <p><strong>Data de envio:</strong> %s</p>
                    </body>
                </html>
                """;

        String descricaoSegura = escaparHtml(feedback.descricao());
        String urgenciaSegura = escaparHtml(feedback.urgencia().name());
        String dataFormatada = feedback.dataCriacao()
                .atZoneSameInstant(ZONE_ID_SP)
                .format(FORMATTER);

        return String.format(body, descricaoSegura, urgenciaSegura, dataFormatada);
    }

    static List<String> extrairEmailsAdministradores(String emailsConfigurados) {
        if (emailsConfigurados == null || emailsConfigurados.isBlank()) {
            return List.of();
        }

        return Stream.of(emailsConfigurados.split("[;,]"))
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .distinct()
                .toList();
    }

    static String escaparHtml(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}