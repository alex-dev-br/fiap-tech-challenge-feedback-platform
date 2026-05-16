package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest;

import br.com.fiap.techchallenge.feedbackplatform.application.dto.CreateFeedbackCommand;
import br.com.fiap.techchallenge.feedbackplatform.application.dto.FeedbackCreatedResult;
import jakarta.annotation.security.RolesAllowed;
import br.com.fiap.techchallenge.feedbackplatform.application.usecase.CreateFeedbackUseCase;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest.dto.CreateAvaliacaoRequest;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest.dto.CreateAvaliacaoResponse;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/avaliacoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AvaliacaoResource {

    private static final Logger LOG = LoggerFactory.getLogger(AvaliacaoResource.class);

    @Inject
    private CreateFeedbackUseCase createFeedbackUseCase;

    @Inject
    public AvaliacaoResource(CreateFeedbackUseCase createFeedbackUseCase) {
        this.createFeedbackUseCase = createFeedbackUseCase;
    }

    @POST
    @RolesAllowed("ALUNO")
    @Transactional
    @Counted(value = "criacoes.avaliacoes", description = "Contador de avaliações criadas")
    @Timed(value = "criacao.avaliacao.time", description = "Tempo de execução da criação de avaliação")
    public Response criar(@Valid CreateAvaliacaoRequest request, @Context UriInfo uriInfo) {
        LOG.info("Criando avaliacao: {}", request);
        CreateFeedbackCommand command = new CreateFeedbackCommand(request.descricao(), request.nota());
        FeedbackCreatedResult result = createFeedbackUseCase.execute(command);
        CreateAvaliacaoResponse response = CreateAvaliacaoResponse.from(result);
        URI location = uriInfo.getAbsolutePathBuilder().path(result.id().toString()).build();
        LOG.info("Avaliacao criada com sucesso: {}", response);
        return Response.created(location).entity(response).build();
    }
}
