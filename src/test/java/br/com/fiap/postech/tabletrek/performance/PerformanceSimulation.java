package br.com.fiap.postech.tabletrek.performance;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class PerformanceSimulation extends Simulation {
    private final HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);

    ActionBuilder registrarRestauranteRequest = http("Registrar Restaurante")
            .post("/tabletrek/restaurante")
            .body(StringBody("{ \"nome\" : \"OutBack\" }"))
            .check(status().is(HttpStatus.CREATED.value()))
            .check(jsonPath("$.id").saveAs("restauranteId"));

    ActionBuilder buscarRestauranteRequest = http("Buscar Restaurante")
            .get("/tabletrek/restaurante/#{restauranteId}")
            .check(status().is(HttpStatus.OK.value()));

    ActionBuilder removerRestauranteRequest = http("Remover Restaurante")
            .delete("/tabletrek/restaurante/#{restauranteId}")
            .check(status().is(HttpStatus.NO_CONTENT.value()));

    ScenarioBuilder cenarioRegistrarRestaurante = scenario("Registrar Restaurante")
            .exec(registrarRestauranteRequest);

    ScenarioBuilder cenarioBuscarRegistrarRestaurante = scenario("Buscar Restaurante")
            .exec(registrarRestauranteRequest)
            .exec(buscarRestauranteRequest);

    ScenarioBuilder cenarioRemoverRegistrarRestaurante = scenario("Remover Restaurante")
            .exec(registrarRestauranteRequest)
            .exec(removerRestauranteRequest);

    {
        setUp(
                cenarioRegistrarRestaurante.injectOpen(
                        rampUsersPerSec(1).to(2).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(10).during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10).to(1).during(Duration.ofSeconds(10))
                ),
                cenarioBuscarRegistrarRestaurante.injectOpen(
                        rampUsersPerSec(1).to(10).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(10).during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10).to(1).during(Duration.ofSeconds(10))
                ),
                cenarioRemoverRegistrarRestaurante.injectOpen(
                        rampUsersPerSec(1).to(5).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(20)),
                        rampUsersPerSec(5).to(1).during(Duration.ofSeconds(10))
                )
        ).protocols(httpProtocol)
                .assertions(global().responseTime().max().lt(50));
    }
}
