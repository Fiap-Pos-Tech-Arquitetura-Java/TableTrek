package br.com.fiap.postech.tabletrek.performance;

import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class PerformanceSimulation extends Simulation {
    private final HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);

    private final AtomicInteger proximo = new AtomicInteger();

    ActionBuilder registrarUsuarioRequest = http("Registrar Usuario Para Registrar Restaurante")
            .post("/tabletrek/usuario")
            .body(StringBody(session -> {
                        return "{ \"nome\" : \"Anderson Wagner\", " +
                                "\"email\" : \"" + session.get("uniqueEmail") + "\", " +
                                "\"senha\" : \"654321\", " +
                                "\"telefone\" : 119912341234 } ";
                    }
            ))
            .asJson()
            .check(status().is(HttpStatus.CREATED.value()))
            .check(jsonPath("$.id").saveAs("idUsuarioParaRegistrarRestaurante"));

    ActionBuilder loginUsuarioRequest = http("Login Usuario Para Registrar Restaurante")
            .post("/tabletrek/usuario/login")
            .body(StringBody(session -> {
                return "{ \"email\" : \"" + session.get("uniqueEmail") + "\","
                + " \"senha\" : \"654321\" }";
            }))
            .asJson()
            .check(status().is(HttpStatus.CREATED.value()))
            .check(jsonPath("$.accessToken").saveAs("tokenParaRegistrarRestaurante"));

    ActionBuilder registrarRestauranteRequest = http("Registrar Restaurante")
            .post("/tabletrek/restaurante")
            .header(HttpHeaders.AUTHORIZATION, "Bearer #{tokenParaRegistrarRestaurante}")
            .body(StringBody(""" 
                     {
                        \"nome\" : \"OutBack\", 
                        \"idUsuario\" : \"#{idUsuarioParaRegistrarRestaurante}\",
                        \"localizacao\" : \"Shopping Bourbon\", 
                        \"horarioFuncionamento\" : \"24 horas\", 
                        \"capacidade\" : 217 , 
                        \"tipoCozinha\" : "TexMex" 
                     }"""
            ))
            .check(status().is(HttpStatus.CREATED.value()))
            .check(jsonPath("$.id").saveAs("restauranteId"));

    ActionBuilder buscarRestauranteRequest = http("Buscar Restaurante")
            .get("/tabletrek/restaurante/#{restauranteId}")
            .header(HttpHeaders.AUTHORIZATION, "Bearer #{tokenParaRegistrarRestaurante}")
            .check(status().is(HttpStatus.OK.value()));

    ActionBuilder removerRestauranteRequest = http("Remover Restaurante")
            .delete("/tabletrek/restaurante/#{restauranteId}")
            .header(HttpHeaders.AUTHORIZATION, "Bearer #{tokenParaRegistrarRestaurante}")
            .check(status().is(HttpStatus.NO_CONTENT.value()));

    ScenarioBuilder cenarioRegistrarRestaurante = scenario("Registrar Restaurante")
            .exec(session -> { return session.set("uniqueEmail", "dddd" + proximo.incrementAndGet() + "@teste.com.br"); })
            .exec(registrarUsuarioRequest)
            .exec(loginUsuarioRequest)
            .exec(registrarRestauranteRequest);

    ScenarioBuilder cenarioBuscarRestaurante = scenario("Buscar Restaurante")
            .exec(session -> { return session.set("uniqueEmail", "dddd" + proximo.incrementAndGet() + "@teste.com.br"); })
            .exec(registrarUsuarioRequest)
            .exec(loginUsuarioRequest)
            .exec(registrarRestauranteRequest)
            .exec(buscarRestauranteRequest);

    ScenarioBuilder cenarioRemoverRestaurante = scenario("Remover Restaurante")
            .exec(session -> { return session.set("uniqueEmail", "dddd" + proximo.incrementAndGet() + "@teste.com.br"); })
            .exec(registrarUsuarioRequest)
            .exec(loginUsuarioRequest)
            .exec(registrarRestauranteRequest)
            .exec(removerRestauranteRequest);

    {
        setUp(
                cenarioRegistrarRestaurante.injectOpen(
                        rampUsersPerSec(1).to(2).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(2).during(Duration.ofSeconds(30)),
                        rampUsersPerSec(2).to(1).during(Duration.ofSeconds(10))
                ),
                cenarioBuscarRestaurante.injectOpen(
                        rampUsersPerSec(1).to(5).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(30)),
                        rampUsersPerSec(5).to(1).during(Duration.ofSeconds(10))
                ),
                cenarioRemoverRestaurante.injectOpen(
                        rampUsersPerSec(1).to(10).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(10).during(Duration.ofSeconds(30)),
                        rampUsersPerSec(10).to(1).during(Duration.ofSeconds(10))
                )
        ).protocols(httpProtocol)
                .assertions(global().responseTime().percentile4().lt(200));
    }

    public PerformanceSimulation() throws JsonProcessingException {
    }
}
