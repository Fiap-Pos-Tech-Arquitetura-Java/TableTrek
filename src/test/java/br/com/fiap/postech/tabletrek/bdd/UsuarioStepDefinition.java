package br.com.fiap.postech.tabletrek.bdd;

import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class UsuarioStepDefinition {

    private Response response;
    private UsuarioDTO usuarioRespostaDTO;
    private static final String ENDPOINT_API_USUARIO = "http://localhost:8080/tabletrek/usuario";

    @Quando("registrar um novo usuario")
    public UsuarioDTO registrar_um_novo_usuario() {
        var usuarioRequisicao = UsuarioHelper.getUsuarioDTO(false);
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(usuarioRequisicao)
                .when()
                .post(ENDPOINT_API_USUARIO);
        return response.then().extract().as(UsuarioDTO.class);
    }
    @Entao("o usuario é registrado com sucesso")
    public void o_usuario_é_registrado_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }
    @Entao("usuario deve ser apresentado")
    public void deve_ser_apresentado() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/usuario.schema.json"));
    }

    @Dado("que um usuario já foi registrado")
    public void que_um_usuario_já_foi_publicado() {
        usuarioRespostaDTO = registrar_um_novo_usuario();
    }

    @Quando("efetuar a busca do usuario")
    public void efetuar_a_busca_do_usuario() {
        when()
                .get(ENDPOINT_API_USUARIO + "/{id}", usuarioRespostaDTO.id());
    }
    @Entao("o usuario é exibido com sucesso")
    public void o_usuario_é_exibido_com_sucesso() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/usuario.schema.json"));
    }

    @Quando("efetuar uma requisição para alterar usuario")
    public void efetuar_uma_requisição_para_alterar_usuario() {
        var novoUsuarioDTO = new UsuarioDTO(
                null,
                "Daiane Golçalvez",
                usuarioRespostaDTO.email(),
                "654321",
                119912341234L
        );
        response =
                given()
                        .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(novoUsuarioDTO)
                .when()
                        .put(ENDPOINT_API_USUARIO + "/{id}", usuarioRespostaDTO.id());
    }
    @Entao("o usuario é atualizado com sucesso")
    public void o_usuario_é_atualizado_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.ACCEPTED.value());
    }

    @Quando("requisitar a remoção do usuario")
    public void requisitar_a_remoção_do_usuario() {
        response =
                given()
                        .header(HttpHeaders.AUTHORIZATION, UsuarioHelper.getToken(usuarioRespostaDTO.email()))
                .when()
                        .delete(ENDPOINT_API_USUARIO + "/{id}", usuarioRespostaDTO.id());
    }
    @Entao("o usuario é removido com sucesso")
    public void o_usuario_é_removido_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
