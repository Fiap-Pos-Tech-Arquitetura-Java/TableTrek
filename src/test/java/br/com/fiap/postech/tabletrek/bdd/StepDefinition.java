package br.com.fiap.postech.tabletrek.bdd;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class StepDefinition {

    private Response response;
    private RestauranteDTO restauranteRespostaDTO;
    private static final String ENDPOINT_API_RESTAURANTE = "http://localhost:8080/tabletrek/restaurante";

    @Quando("registrar um novo restaurante")
    public RestauranteDTO registrar_um_novo_restaurante() {
        var restauranteRequisicao = RestauranteHelper.getRestauranteDTO(false);
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(restauranteRequisicao)
                .when()
                .post(ENDPOINT_API_RESTAURANTE);
        return response.then().extract().as(RestauranteDTO.class);
    }
    @Entao("o restaurante é registrado com sucesso")
    public void o_restaurante_é_registrado_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }
    @Entao("deve ser apresentado")
    public void deve_ser_apresentado() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/restaurante.schema.json"));
    }

    @Dado("que um restaurante já foi registrado")
    public void que_um_restaurante_já_foi_publicado() {
        restauranteRespostaDTO = registrar_um_novo_restaurante();
    }

    @Quando("efetuar a busca do restaurante")
    public void efetuar_a_busca_do_restaurante() {
        when()
                .get(ENDPOINT_API_RESTAURANTE + "/{id}", restauranteRespostaDTO.id());
    }
    @Entao("o restaurante é exibido com sucesso")
    public void o_restaurante_é_exibido_com_sucesso() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/restaurante.schema.json"));
    }

    @Quando("efetuar uma requisição para alterar restaurante")
    public void efetuar_uma_requisição_para_alterar_restaurante() {
        var novoRestauranteDTO = new RestauranteDTO(null, "Novo JoJo Ramen");
        response =
                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(novoRestauranteDTO)
                .when()
                        .put(ENDPOINT_API_RESTAURANTE + "/{id}", restauranteRespostaDTO.id());
    }
    @Entao("o restaurante é atualizado com sucesso")
    public void o_restaurante_é_atualizado_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.ACCEPTED.value());
    }

    @Quando("requisitar a remoção do restaurante")
    public void requisitar_a_remoção_do_restaurante() {
        response = when().delete(ENDPOINT_API_RESTAURANTE + "/{id}", restauranteRespostaDTO.id());
    }
    @Entao("o restaurante é removido com sucesso")
    public void o_restaurante_é_removido_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
