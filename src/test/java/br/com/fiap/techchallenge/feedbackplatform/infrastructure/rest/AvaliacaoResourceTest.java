package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest;

import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository.PanacheFeedbackRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestSecurity(user = "testUser", roles = { "ALUNO" })
@JwtSecurity(claims = { @Claim(key = "groups", value = "ALUNO") })
class AvaliacaoResourceTest {

    @Inject
    PanacheFeedbackRepository feedbackRepository;

    @BeforeEach
    @Transactional
    void limparBanco() {
        feedbackRepository.deleteAll();
    }

    @Test
    void deveCriarAvaliacaoComSucesso() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                                "descricao": "A aula foi muito boa",
                                "nota": 9
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("descricao", equalTo("A aula foi muito boa"))
                .body("nota", equalTo(9))
                .body("urgencia", equalTo("BAIXA"))
                .body("dataCriacao", notNullValue());
    }

    @Test
    void deveCriarAvaliacaoUrgenteQuandoDescricaoContiverPalavraCritica() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "A plataforma está travando durante a aula",
                          "nota": 8
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("descricao", equalTo("A plataforma está travando durante a aula"))
                .body("nota", equalTo(8))
                .body("urgencia", equalTo("ALTA"))
                .body("dataCriacao", notNullValue());
    }

    @Test
    void deveRetornarBadRequestQuandoNotaForMaiorQueDez() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "A aula foi boa, mas o áudio estava ruim",
                          "nota": 11
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarBadRequestQuandoNotaForMenorQueZero() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "A aula foi boa, mas o áudio estava ruim",
                          "nota": -1
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarBadRequestQuandoDescricaoForVazia() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "   ",
                          "nota": 5
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarBadRequestQuandoNotaNaoForInformada() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "A aula foi boa, mas o áudio estava ruim"
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void deveRetornarBadRequestQuandoDescricaoNaoForInformada() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nota": 5
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "professorUser", roles = { "PROFESSOR" })
    @JwtSecurity(claims = { @Claim(key = "groups", value = "PROFESSOR") })
    void deveRetornarForbiddenQuandoUsuarioForProfessor() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "Tentando avaliar como professor",
                          "nota": 10
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "adminUser", roles = { "ADMIN" })
    @JwtSecurity(claims = { @Claim(key = "groups", value = "ADMIN") })
    void deveRetornarForbiddenQuandoUsuarioForAdmin() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "descricao": "Tentando avaliar como admin",
                            "nota": 5
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "", roles = {}) // Remove o usuário mockado
    void deveRetornarUnauthorizedQuandoNaoAutenticado() {
        // Ao removermos o mock, o Quarkus vai exigir o header Authorization de verdade
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                                "descricao": "Tentando avaliar sem token",
                                "nota": 5
                        }
                        """)
                .when()
                .post("/avaliacoes")
                .then()
                // Se a chamada real for feita sem token, retorna 401
                .statusCode(401);
    }
}
