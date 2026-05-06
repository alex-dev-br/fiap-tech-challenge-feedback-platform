package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest;

import br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.repository.PanacheFeedbackRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
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
}
