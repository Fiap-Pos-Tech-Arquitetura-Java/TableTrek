package br.com.fiap.postech.tabletrek.bdd;

import br.com.fiap.postech.tabletrek.dto.AvaliacaoDTO;
import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.helper.AvaliacaoHelper;
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

public class AvaliacaoStepDefinition {

    private Response response;

    private Response usuarioResponse;

    private Response restauranteResponse;

    private Response reservaMesaResponse;
    private AvaliacaoDTO avaliacaoRespostaDTO;
    private UsuarioDTO usuarioRespostaDTO;
    private RestauranteDTO restauranteRespostaDTO;
    private ReservaMesaDTO reservaMesaRespostaDTO;
    private static final String ENDPOINT_API_AVALIACAO = "http://localhost:8080/tabletrek/avaliacao";
    private static final String ENDPOINT_API_USUARIO = "http://localhost:8080/tabletrek/usuario";
    private static final String ENDPOINT_API_RESTAURANTE = "http://localhost:8080/tabletrek/restaurante";
    private static final String ENDPOINT_API_RESERVA_MESA = "http://localhost:8080/tabletrek/reservaMesa";

    public UsuarioDTO registrar_um_novo_usuario() {
        var usuarioRequisicao = UsuarioHelper.getUsuarioDTO(false);
        usuarioResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(usuarioRequisicao)
                .when()
                .post(ENDPOINT_API_USUARIO);
        return usuarioResponse.then().extract().as(UsuarioDTO.class);
    }

    @Dado("que tenho um usuario registrado para fazer uma avaliacao")
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

    @Dado("que tenho um restaurante registrado para fazer uma avaliacao")
    public void que_tenho_um_restaurante_registrado_para_reservar_uma_mesa() {
        restauranteRespostaDTO = registrar_um_novo_restaurante();
    }

    public ReservaMesaDTO registrar_uma_nova_reserva_de_mesa() {
        var reservaMesaRequisicao = ReservaMesaHelper.getReservaMesaDTO(false, restauranteRespostaDTO.id().toString(), usuarioRespostaDTO.id().toString());
        reservaMesaResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .body(reservaMesaRequisicao)
                .when()
                .post(ENDPOINT_API_RESERVA_MESA);
        return reservaMesaResponse.then().extract().as(ReservaMesaDTO.class);
    }

    @Dado("que tenho uma reserva de mesa registrada para fazer uma avaliacao")
    public void que_tenho_uma_reserva_de_mesa_registrada_para_fazer_uma_avaliacao() {
        reservaMesaRespostaDTO = registrar_uma_nova_reserva_de_mesa();
    }

    @Dado("registrar uma nova avaliacao")
    public AvaliacaoDTO registrar_uma_nova_avaliacao() {
        var avaliacaoRequisicao = AvaliacaoHelper.getAvaliacaoDTO(false, reservaMesaRespostaDTO.id().toString());
        response = given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(avaliacaoRequisicao)
                .when()
                .post(ENDPOINT_API_AVALIACAO);
        return response.then().extract().as(AvaliacaoDTO.class);
    }
    @Entao("a avaliacao é registrada com sucesso")
    public void a_avaliacao_é_registrada_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }
    @Entao("a avaliacao deve ser apresentada")
    public void deve_ser_apresentado() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/avaliacao.schema.json"));
    }

    @Dado("que uma avaliacao já foi registrada")
    public void que_uma_avaliacao_já_foi_registrada() {
        avaliacaoRespostaDTO = registrar_uma_nova_avaliacao();
    }

    @Quando("efetuar a busca de uma avaliacao")
    public void efetuar_a_busca_de_uma_avaliacao() {
        given()
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken())
        .when()
                .get(ENDPOINT_API_RESERVA_MESA + "/{id}", avaliacaoRespostaDTO.id());
    }
    @Entao("a avaliacao é exibida com sucesso")
    public void a_avaliacao_é_exibida_com_sucesso() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/avaliacao.schema.json"));
    }

    @Quando("efetuar uma requisição para alterar uma avaliacao")
    public void efetuar_uma_requisição_para_alterar_uma_avaliacao() {
        var avaliacaoDTO = new AvaliacaoDTO(
                null,
                reservaMesaRespostaDTO.id(),
                2,
                "mais um comentario de restaurante que eu fui"
        );
        response =
                given()
                        .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(avaliacaoDTO)
                .when()
                        .put(ENDPOINT_API_AVALIACAO + "/{id}", avaliacaoRespostaDTO.id());
    }
    @Entao("a avaliacao é alterada com sucesso")
    public void a_avaliacao_é_finalizada_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.ACCEPTED.value());
    }

    @Quando("requisitar a remoção de uma avaliacao")
    public void requisitar_a_remoção_de_uma_avaliacao() {
        response =
                given()
                        .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .when()
                        .delete(ENDPOINT_API_AVALIACAO + "/{id}", avaliacaoRespostaDTO.id());
    }
    @Entao("a avaliacao é removida com sucesso")
    public void a_avaliacao_é_removida_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
