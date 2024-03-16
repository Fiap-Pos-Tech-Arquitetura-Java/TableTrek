package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.dto.AvaliacaoDTO;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.helper.AvaliacaoHelper;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class AvaliacaoControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastrarAvaliacao {
        @Test
        void devePermitirCadastrarAvaliacao() {
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(false,"c055e2a7-4871-4408-aeb5-cbf2e3d31eaa");
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(avaliacaoDTO)
            .when()
                .post("/avaliacao")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/avaliacao.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarAvaliacao_RequisicaoXml() {
            /*
              Na aula o professor instanciou uma string e enviou no .body()
              Mas como o teste valida o contentType o body pode ser enviado com qualquer conteudo
              ou nem mesmo ser enviado como ficou no teste abaixo.
             */
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .post("/avaliacao")
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarAvaliacao {
        @Test
        void devePermitirBuscarAvaliacaoPorId() {
            var id = "384efca3-1b34-4022-b74b-9da5cbe0157d";
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/avaliacao/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/avaliacao.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }
        @Test
        void deveGerarExcecao_QuandoBuscarAvaliacaoPorId_idNaoExiste() {
            var id = AvaliacaoHelper.getAvaliacaoDTO(true).id();
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/avaliacao/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosAvaliacao() {
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/avaliacao")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/avaliacao.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosAvaliacao_ComPaginacao() {
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .queryParam("page", "1")
                .queryParam("size", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/avaliacao")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/avaliacao.page.schema.json"));
        }
    }

    @Nested
    class AlterarAvaliacao {
        @Test
        void devePermitirAlterarAvaliacao(){
            var avaliacaoDTO = new AvaliacaoDTO(
                    UUID.fromString("384efca3-1b34-4022-b74b-9da5cbe0157d"),
                    UUID.fromString("15dc1918-9e48-4beb-9b63-4aad3914c8a7"),
                    5,
                    "a comida estava otima mas o atendimento foi demorado, não vá com pressa." +
                            "Depois nos ligarão pedindo desculpas e ofereceram um cupom"
            );

            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .body(avaliacaoDTO).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/avaliacao/{id}", avaliacaoDTO.id())
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/avaliacao.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarAvaliacaoPorId_idNaoExiste() {
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(true);
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .body(avaliacaoDTO).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/avaliacao/{id}", avaliacaoDTO.id())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Avaliacao não encontrado com o ID: " + avaliacaoDTO.id()));
        }
    }

    @Nested
    class RemoverAvaliacao {
        @Test
        void devePermitirRemoverAvaliacao() {
            var idAvaliacao = UUID.fromString("31971864-2c43-44cd-9cf8-943992981f54");
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
            .when()
                .delete("/avaliacao/{id}", idAvaliacao)
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverAvaliacaoPorId_idNaoExiste() {
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(true);
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
            .when()
                .delete("/avaliacao/{id}", avaliacaoDTO.id())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Avaliacao não encontrado com o ID: " + avaliacaoDTO.id()));
        }
    }
}
