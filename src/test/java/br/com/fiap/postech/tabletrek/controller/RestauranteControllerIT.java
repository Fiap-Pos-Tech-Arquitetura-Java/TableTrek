package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestauranteControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastrarRestaurante {
        @Test
        void devePermitirCadastrarRestaurante() {
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(false);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(restauranteDTO)
            .when()
                .post("/restaurante")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/restaurante.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarRestaurante_RequisicaoXml() {
            /*
              Na aula o professor instanciou uma string e enviou no .body()
              Mas como o teste valida o contentType o body pode ser enviado com qualquer conteudo
              ou nem mesmo ser enviado como ficou no teste abaixo.
             */
            given()
                .contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .post("/restaurante")
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarRestaurante {
        @Test
        void devePermitirBuscarRestaurantePorId() {
            var id = "52a85f11-9f0f-4dc6-b92f-abc3881328a8";
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/restaurante/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/restaurante.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }
        @Test
        void deveGerarExcecao_QuandoBuscarRestaurantePorId_idNaoExiste() {
            var id = RestauranteHelper.getRestauranteDTO(true).id();
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/restaurante/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosRestaurante() {
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/restaurante")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/restaurante.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosRestaurante_ComPaginacao() {
            given()
                    .queryParam("page", "1")
                    .queryParam("size", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/restaurante")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/restaurante.page.schema.json"));
        }
    }

    @Nested
    class AlterarRestaurante {
        @Test
        void devePermitirAlterarRestaurante(){
            var restauranteDTO = new RestauranteDTO(UUID.fromString("ada8399b-44f0-499c-82d9-5ca9ed1670da"), "Casa das Costelas!!!");
            given()
                .body(restauranteDTO).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/restaurante/{id}", restauranteDTO.id())
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/restaurante.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarRestaurante_RequisicaoXml() {
            var restauranteDTO = new RestauranteDTO(UUID.fromString("ada8399b-44f0-499c-82d9-5ca9ed1670da"), "Casa das Costelas!!!");
            given()
                .body(restauranteDTO).contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .put("/restaurante/{id}", restauranteDTO.id())
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarRestaurantePorId_idNaoExiste() {
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            given()
                .body(restauranteDTO).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/restaurante/{id}", restauranteDTO.id())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Restaurante não encontrado com o ID: " + restauranteDTO.id()));
        }
    }

    @Nested
    class RemoverRestaurante {
        @Test
        void devePermitirRemoverRestaurante() {
            var restauranteDTO = new RestauranteDTO(UUID.fromString("b35d3a29-408a-4d1a-964c-2261cb0e252f"), "Casa das Costelas!!!");
            given()
            .when()
                .delete("/restaurante/{id}", restauranteDTO.id())
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverRestaurantePorId_idNaoExiste() {

            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            given()
            .when()
                .delete("/restaurante/{id}", restauranteDTO.id())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Restaurante não encontrado com o ID: " + restauranteDTO.id()));
        }
    }
}
