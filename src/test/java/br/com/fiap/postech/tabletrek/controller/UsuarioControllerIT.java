package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
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
public class UsuarioControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastrarUsuario {
        @Test
        void devePermitirCadastrarUsuario() {
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(false);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(usuarioDTO)
            .when()
                .post("/usuario")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/usuario.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarUsuario_RequisicaoXml() {
            /*
              Na aula o professor instanciou uma string e enviou no .body()
              Mas como o teste valida o contentType o body pode ser enviado com qualquer conteudo
              ou nem mesmo ser enviado como ficou no teste abaixo.
             */
            given()
                .contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .post("/usuario")
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarUsuario {
        @Test
        void devePermitirBuscarUsuarioPorId() {
            var id = "d32c6406-a4a2-4503-ac12-d14b8a3b788f";
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/usuario/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/usuario.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }
        @Test
        void deveGerarExcecao_QuandoBuscarUsuarioPorId_idNaoExiste() {
            var id = UsuarioHelper.getUsuarioDTO(true).id();
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/usuario/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosUsuario() {
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/usuario")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/usuario.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosUsuario_ComPaginacao() {
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .queryParam("page", "1")
                .queryParam("size", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/usuario")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/usuario.page.schema.json"));
        }
    }

    @Nested
    class AlterarUsuario {
        @Test
        void devePermitirAlterarUsuario(){
            var usuarioDTO = new UsuarioDTO(
                    UUID.fromString("a6df9ca4-09d7-41a1-bb5b-c8cb800f7452"),
                    "Kaiby o mestre do miro !!!",
                    "aaaa@bbb.com",
                    "123456",
                    11999999999L
            );
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .body(usuarioDTO).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/usuario/{id}", usuarioDTO.id())
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/usuario.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUsuario_RequisicaoXml() {
            var usuarioDTO = new UsuarioDTO(
                     UUID.fromString("ada8399b-44f0-499c-82d9-5ca9ed1670da"),
                    "Casa das Costelas!!!",
                    "Av. Min. Petrônio Portela, 1009 - Moinho Velho, São Paulo - SP, 02959-000",
                    "12:00–22:00",
                    11991733344L
            );
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .body(usuarioDTO).contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .put("/usuario/{id}", usuarioDTO.id())
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUsuarioPorId_idNaoExiste() {
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .body(usuarioDTO).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/usuario/{id}", usuarioDTO.id())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Usuario não encontrado com o ID: " + usuarioDTO.id()));
        }
    }

    @Nested
    class RemoverUsuario {
        @Test
        void devePermitirRemoverUsuario() {
            var usuarioDTO = new UsuarioDTO(
                    UUID.fromString("374e87c7-73b1-4ca8-81c6-43ed0381aa13"),
                    "Janaina",
                    "ccc@ddd.com",
                    "654321",
                    11988886731L
            );
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
            .when()
                .delete("/usuario/{id}", usuarioDTO.id())
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverUsuarioPorId_idNaoExiste() {

            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
            .when()
                .delete("/usuario/{id}", usuarioDTO.id())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Usuario não encontrado com o ID: " + usuarioDTO.id()));
        }
    }
}
