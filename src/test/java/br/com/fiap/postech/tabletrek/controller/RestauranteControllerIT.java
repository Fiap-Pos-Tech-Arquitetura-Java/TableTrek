package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import io.restassured.RestAssured;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;
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
        void devePermitirCadastrarRestaurante() throws Exception {
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(false);
            given().contentType(MediaType.APPLICATION_JSON_VALUE).body(restauranteDTO)
                    .when().post("/restaurante")
                    .then().statusCode(HttpStatus.CREATED.value());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarRestaurante_RequisicaoXml() throws Exception {
            /**
             * Na aula o professor instanciou uma string e enviou no .body()
             * Mas como o teste valida o contentType o body pode ser enviado com qualquer conteudo
             * ou nem mesmo ser enviado como ficou no teste abaixo.
             */
            given().contentType(MediaType.APPLICATION_XML_VALUE)
                    .when().post("/restaurante")
                    .then().statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }
    }

    @Nested
    class BuscarRestaurante {
        @Test
        void devePermitirBuscarRestaurantePorId() throws Exception {
            var id = "52a85f11-9f0f-4dc6-b92f-abc3881328a8";
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/restaurante/{id}", id)
                    .then().statusCode(HttpStatus.OK.value());
        }
        @Test
        void deveGerarExcecao_QuandoBuscarRestaurantePorId_idNaoExiste() throws Exception {
            var id = RestauranteHelper.getRestauranteDTO(true).id();
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/restaurante/{id}", id)
                    .then().statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosRestaurante() throws Exception {
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/restaurante")
                    .then().statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    class AlterarRestaurante {
        @Test
        void devePermitirAlterarRestaurante() throws Exception {
            fail("ainda não implementado");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarRestaurante_RequisicaoXml() throws Exception {
            fail("ainda não implementado");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarRestaurantePorId_idNaoExiste() throws Exception {
            fail("ainda não implementado");
        }
    }

    @Nested
    class RemoverRestaurante {
        @Test
        void devePermitirRemoverRestaurante() throws Exception {
            fail("ainda não implementado");
        }

        @Test
        void deveGerarExcecao_QuandoRemoverRestaurantePorId_idNaoExiste() throws Exception {
            fail("ainda não implementado");
        }
    }
}
