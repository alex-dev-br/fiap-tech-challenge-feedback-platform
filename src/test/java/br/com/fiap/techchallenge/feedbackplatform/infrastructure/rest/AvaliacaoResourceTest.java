package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest;

import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository.PanacheFeedbackNotificationLogRepository;
import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository.PanacheFeedbackRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestSecurity(user = "testUser", roles = { "ALUNO" })
@JwtSecurity(claims = { @Claim(key = "groups", value = "ALUNO") })
@DisplayName("API de criação de avaliações")
class AvaliacaoResourceTest {

    @Inject
    PanacheFeedbackRepository feedbackRepository;

    @Inject
    PanacheFeedbackNotificationLogRepository notificationLogRepository;

    @BeforeEach
    @Transactional
    void limparBanco() {
        notificationLogRepository.deleteAll();
        feedbackRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar avaliação com sucesso quando usuário possuir role ALUNO")
    void deveCriarAvaliacaoComSucesso() {
        // Arrange
        String requestBody = """
                {
                    "descricao": "A aula foi muito boa",
                    "nota": 9
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("descricao", equalTo("A aula foi muito boa"))
                .body("nota", equalTo(9))
                .body("urgencia", equalTo("BAIXA"))
                .body("dataCriacao", notNullValue());
    }

    @Test
    @DisplayName("Deve criar avaliação urgente quando descrição contiver palavra crítica")
    void deveCriarAvaliacaoUrgenteQuandoDescricaoContiverPalavraCritica() {
        // Arrange
        String requestBody = """
                {
                    "descricao": "A plataforma está travando durante a aula",
                    "nota": 8
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("descricao", equalTo("A plataforma está travando durante a aula"))
                .body("nota", equalTo(8))
                .body("urgencia", equalTo("ALTA"))
                .body("dataCriacao", notNullValue());
    }

    @Test
    @DisplayName("Deve retornar 400 quando nota for maior que dez")
    void deveRetornarBadRequestQuandoNotaForMaiorQueDez() {
        // Arrange
        String requestBody = """
                {
                    "descricao": "A aula foi boa, mas o áudio estava ruim",
                    "nota": 11
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve retornar 400 quando nota for menor que zero")
    void deveRetornarBadRequestQuandoNotaForMenorQueZero() {
        // Arrange
        String requestBody = """
                {
                    "descricao": "A aula foi boa, mas o áudio estava ruim",
                    "nota": -1
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve retornar 400 quando descrição estiver vazia")
    void deveRetornarBadRequestQuandoDescricaoForVazia() {
        // Arrange
        String requestBody = """
                {
                    "descricao": "   ",
                    "nota": 5
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve retornar 400 quando nota não for informada")
    void deveRetornarBadRequestQuandoNotaNaoForInformada() {
        // Arrange
        String requestBody = """
                {
                    "descricao": "A aula foi boa, mas o áudio estava ruim"
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve retornar 400 quando descrição não for informada")
    void deveRetornarBadRequestQuandoDescricaoNaoForInformada() {
        // Arrange
        String requestBody = """
                {
                    "nota": 5
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário possuir role PROFESSOR")
    @TestSecurity(user = "professorUser", roles = { "PROFESSOR" })
    @JwtSecurity(claims = { @Claim(key = "groups", value = "PROFESSOR") })
    void deveRetornarForbiddenQuandoUsuarioForProfessor() {
        // Arrange
        String requestBody = """
                {
                    "descricao": "Tentando avaliar como professor",
                    "nota": 10
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário possuir role ADMIN")
    @TestSecurity(user = "adminUser", roles = { "ADMIN" })
    @JwtSecurity(claims = { @Claim(key = "groups", value = "ADMIN") })
    void deveRetornarForbiddenQuandoUsuarioForAdmin() {
        // Arrange
        String requestBody = """
                {
                    "descricao": "Tentando avaliar como admin",
                    "nota": 5
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Deve retornar 401 quando usuário não estiver autenticado")
    @TestSecurity(user = "", roles = {})
    void deveRetornarUnauthorizedQuandoNaoAutenticado() {
        // Arrange
        String requestBody = """
                {
                    "descricao": "Tentando avaliar sem token",
                    "nota": 5
                }
                """;

        // Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/avaliacoes");

        // Assert
        response.then()
                .statusCode(401);
    }
}
