package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.helper.ReservaMesaHelper;
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
public class ReservaMesaControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastrarReservaMesa {
        @Test
        void devePermitirCadastrarReservaMesa() {
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(false,"52a85f11-9f0f-4dc6-b92f-abc3881328a8");
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(reservaMesaDTO)
            .when()
                .post("/reservaMesa")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/reservaMesa.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarReservaMesa_RequisicaoXml() {
            /*
              Na aula o professor instanciou uma string e enviou no .body()
              Mas como o teste valida o contentType o body pode ser enviado com qualquer conteudo
              ou nem mesmo ser enviado como ficou no teste abaixo.
             */
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .post("/reservaMesa")
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarReservaMesa {
        @Test
        void devePermitirBuscarReservaMesaPorId() {
            var id = "c055e2a7-4871-4408-aeb5-cbf2e3d31eaa";
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/reservaMesa/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/reservaMesa.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }
        @Test
        void deveGerarExcecao_QuandoBuscarReservaMesaPorId_idNaoExiste() {
            var id = ReservaMesaHelper.getReservaMesaDTO(true).id();
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/reservaMesa/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosReservaMesa() {
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/reservaMesa")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/reservaMesa.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosReservaMesa_ComPaginacao() {
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                .queryParam("page", "1")
                .queryParam("size", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/reservaMesa")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/reservaMesa.page.schema.json"));
        }
    }

    @Nested
    class AlterarReservaMesa {
        @Test
        void devePermitirAlterarReservaMesa(){
            var idReservaMesa = UUID.fromString("15dc1918-9e48-4beb-9b63-4aad3914c8a7");
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken("aaaa@bbb.com"))
            .when()
                .put("/reservaMesa/{id}", idReservaMesa)
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/reservaMesa.schema.json"));
        }
        @Test
        void deveGerarExcecao_QuandoAlterarReservaMesa_OutroUsuarioSemSerOQueCadastrou(){
            var idReservaMesa = UUID.fromString("15dc1918-9e48-4beb-9b63-4aad3914c8a7");
            given()
                    .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                    .when()
                    .put("/reservaMesa/{id}", idReservaMesa)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Somente o usuario que fez a reserva ou " +
                            "o dono do restaurante podem finalizar uma reserva de uma mesa. ID: " + idReservaMesa));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarReservaMesaPorId_idNaoExiste() {
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
            .when()
                .put("/reservaMesa/{id}", reservaMesaDTO.id())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id()));
        }
    }

    @Nested
    class RemoverReservaMesa {
        @Test
        void devePermitirRemoverReservaMesa() {
            var idReservaMesa = UUID.fromString("86d6f0bb-3dd8-48f3-9078-4fb8c8e2c7c1");
            given()
                    .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken("ccc@ddd.com"))
                    .when()
                    .delete("/reservaMesa/{id}", idReservaMesa)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
        @Test
        void deveGerarExcecao_QuandoRemoverReservaMesa_OutroUsuarioSemSerOQueCadastrou() {
            var idReservaMesa = UUID.fromString("86d6f0bb-3dd8-48f3-9078-4fb8c8e2c7c1");
            given()
                    .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
                    .when()
                    .delete("/reservaMesa/{id}", idReservaMesa)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Somente o usuario que fez a reserva ou " +
                            "o dono do restaurante podem deletar uma reserva de uma mesa. ID: " + idReservaMesa));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverReservaMesaPorId_idNaoExiste() {
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
            .when()
                .delete("/reservaMesa/{id}", reservaMesaDTO.id())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id()));
        }
    }
}
