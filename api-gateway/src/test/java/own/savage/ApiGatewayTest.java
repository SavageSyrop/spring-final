package own.savage;


import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = ApiGatewayTest.WiremockInitializer.class)
public class ApiGatewayTest {

    static class WiremockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        static WireMockServer wireMockServer = new WireMockServer(0);

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            wireMockServer.start();
            int port = wireMockServer.port();
            TestPropertyValues.of(
                    "spring.cloud.gateway.routes[0].id=hotel-service-mock",
                    "spring.cloud.gateway.routes[0].uri=http://localhost:" + port,
                    "spring.cloud.gateway.routes[0].predicates[0]=Path=/api/hotels/**",
                    "spring.cloud.gateway.routes[1].id=booking-service-mock",
                    "spring.cloud.gateway.routes[1].uri=http://localhost:" + port,
                    "spring.cloud.gateway.routes[1].predicates[0]=Path=/api/users/**"
            ).applyTo(context.getEnvironment());
        }
    }

    @Autowired
    private WebClient.Builder builder;

    @BeforeEach
    void setup() {
        WiremockInitializer.wireMockServer.resetAll();
        WiremockInitializer.wireMockServer.stubFor(post(urlEqualTo("/api/users/login"))
                .withHeader("Authorization", matching("Bearer .*"))
                .withHeader("X-Correlation-Id", matching(".*"))
                .willReturn(okJson("{\"ok\":true}")));
    }

    @AfterAll
    static void shutdown() {
        WiremockInitializer.wireMockServer.stop();
    }

    @Test
    void forwardingTest() {
        WebClient client = builder.baseUrl("http://localhost:8880").build();
        String body = client.post()
                .uri("/api/users/login")
                .header(HttpHeaders.AUTHORIZATION, "Bearer sdf125ajghiHAUYSFGu==")
                .header("X-Correlation-Id", UUID.randomUUID().toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        assertTrue(body.contains("ok"));
    }
}


