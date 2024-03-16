package br.com.fiap.postech.tabletrek.bdd;

import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.helper.ReservaMesaHelper;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ReservaMesaStepDefinition {

    private Response response;

    private Response usuarioResponse;

    private Response restauranteResponse;
    private ReservaMesaDTO reservaMesaRespostaDTO;
    private UsuarioDTO usuarioRespostaDTO;
    private RestauranteDTO restauranteRespostaDTO;
    private static final String ENDPOINT_API_RESERVA_MESA = "http://localhost:8080/tabletrek/reservaMesa";
    private static final String ENDPOINT_API_USUARIO = "http://localhost:8080/tabletrek/usuario";
    private static final String ENDPOINT_API_RESTAURANTE = "http://localhost:8080/tabletrek/restaurante";

    public UsuarioDTO registrar_um_novo_usuario() {
        var usuarioRequisicao = UsuarioHelper.getUsuarioDTO(false);
        usuarioResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(usuarioRequisicao)
                .when()
                .post(ENDPOINT_API_USUARIO);
        return usuarioResponse.then().extract().as(UsuarioDTO.class);
    }

    @Dado("que tenho um usuario registrado para reservar uma mesa")
    public void que_um_usuario_já_foi_publicado() {
        usuarioRespostaDTO = registrar_um_novo_usuario();
    }

    public RestauranteDTO registrar_um_novo_restaurante() {
        var restauranteRequisicao = RestauranteHelper.getRestauranteDTO(false, usuarioRespostaDTO.id().toString());
        restauranteResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .body(restauranteRequisicao)
                .when()
                .post(ENDPOINT_API_RESTAURANTE);
        return restauranteResponse.then().extract().as(RestauranteDTO.class);
    }

    @Dado("que tenho um restaurante registrado para reservar uma mesa")
    public void que_tenho_um_restaurante_registrado_para_reservar_uma_mesa() {
        restauranteRespostaDTO = registrar_um_novo_restaurante();

    }

    @Dado("registrar uma nova reserva de mesa")
    public ReservaMesaDTO registrar_uma_nova_reserva_de_mesa() {
        var reservaMesaRequisicao = ReservaMesaHelper.getReservaMesaDTO(false, restauranteRespostaDTO.id().toString(), usuarioRespostaDTO.id().toString());
        response = given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservaMesaRequisicao)
                .when()
                .post(ENDPOINT_API_RESERVA_MESA);
        return response.then().extract().as(ReservaMesaDTO.class);
    }
    @Entao("a reserva de mesa é registrada com sucesso")
    public void a_reserva_de_mesa_é_registrada_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }
    @Entao("a reserva de mesa deve ser apresentada")
    public void deve_ser_apresentado() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/reservaMesa.schema.json"));
    }

    @Dado("que uma reserva de mesa já foi registrada")
    public void que_uma_reserva_de_mesa_já_foi_registrada() {
        reservaMesaRespostaDTO = registrar_uma_nova_reserva_de_mesa();
    }

    @Quando("efetuar a busca de uma reserva de mesa")
    public void efetuar_a_busca_de_uma_reserva_de_mesa() {
        given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
        .when()
                .get(ENDPOINT_API_RESERVA_MESA + "/{id}", reservaMesaRespostaDTO.id());
    }
    @Entao("a reserva de mesa é exibida com sucesso")
    public void a_reserva_de_mesa_é_exibida_com_sucesso() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/reservaMesa.schema.json"));
    }

    @Quando("efetuar uma requisição para finalizar uma reserva de mesa")
    public void efetuar_uma_requisição_para_finalizar_uma_reserva_de_mesa() {
        response =
                given()
                        .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .when()
                        .put(ENDPOINT_API_RESERVA_MESA + "/{id}", reservaMesaRespostaDTO.id());
    }
    @Entao("a reserva de uma mesa é finalizada com sucesso")
    public void a_reserva_de_uma_mesa_é_finalizada_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.ACCEPTED.value());
    }

    @Quando("requisitar a remoção de uma reserva de mesa")
    public void requisitar_a_remoção_de_uma_reserva_de_mesa() {
        response =
                given()
                        .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .when()
                        .delete(ENDPOINT_API_RESERVA_MESA + "/{id}", reservaMesaRespostaDTO.id());
    }
    @Entao("a reserva de mesa é removida com sucesso")
    public void a_reserva_de_mesa_é_removida_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
