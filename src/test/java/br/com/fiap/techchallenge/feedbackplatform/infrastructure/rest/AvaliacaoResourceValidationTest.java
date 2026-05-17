package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestSecurity(user = "testUser", roles = { "ALUNO" })
@JwtSecurity(claims = { @Claim(key = "groups", value = "ALUNO") })
@DisplayName("Validações da API de criação de avaliações")
class AvaliacaoResourceValidationTest {

    @Test
    @DisplayName("Deve retornar Bad Request quando descrição tiver menos de três caracteres")
    void deveRetornarBadRequestQuandoDescricaoTiverMenosDeTresCaracteres() {
        // Arrange
        String requestBody = """
                {
                  "descricao": "Oi",
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
    @DisplayName("Deve retornar Bad Request quando descrição tiver mais de dois mil caracteres")
    void deveRetornarBadRequestQuandoDescricaoTiverMaisDeDoisMilCaracteres() {
        // Arrange
        String descricaoLonga = "a".repeat(2001);
        String requestBody = """
                {
                  "descricao": "%s",
                  "nota": 5
                }
                """.formatted(descricaoLonga);

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
}
