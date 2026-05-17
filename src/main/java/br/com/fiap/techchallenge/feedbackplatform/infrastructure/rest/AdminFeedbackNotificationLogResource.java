package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest;

import br.com.fiap.techchallenge.feedbackplatform.application.usecase.ListFeedbackNotificationLogsUseCase;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest.dto.FeedbackNotificationLogResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;

@Path("/admin/feedbacks")
@Produces(MediaType.APPLICATION_JSON)
public class AdminFeedbackNotificationLogResource {

    private final ListFeedbackNotificationLogsUseCase listFeedbackNotificationLogsUseCase;

    @Inject
    public AdminFeedbackNotificationLogResource(
            ListFeedbackNotificationLogsUseCase listFeedbackNotificationLogsUseCase) {
        this.listFeedbackNotificationLogsUseCase = listFeedbackNotificationLogsUseCase;
    }

    @GET
    @Path("/{feedbackId}/notificacoes")
    @RolesAllowed("ADMIN")
    public List<FeedbackNotificationLogResponse> listarNotificacoes(
            @PathParam("feedbackId") UUID feedbackId) {

        return listFeedbackNotificationLogsUseCase.execute(feedbackId)
                .stream()
                .map(FeedbackNotificationLogResponse::from)
                .toList();
    }
}
