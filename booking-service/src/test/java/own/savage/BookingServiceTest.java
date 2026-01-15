package own.savage;


import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = BookingServiceTest.WiremockInitializer.class)
public class BookingServiceTest {

    static class WiremockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        static WireMockServer wireMockServer = new WireMockServer(0);

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            wireMockServer.start();
            int port = wireMockServer.port();
            TestPropertyValues.of(
                    "hotelService.url=http://localhost:" + port,
                    "hotelService.timeout=100000",
                    "hotelService.retries=2"
            ).applyTo(context.getEnvironment());
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Value("${jwt.secret}")
    private String jwtSecret;


    @BeforeEach
    void setupWiremock() {
        WiremockInitializer.wireMockServer.resetAll();
    }

    @AfterAll
    static void shutdown() {
        WiremockInitializer.wireMockServer.stop();
    }

    @Test
    void register() {
        webTestClient.post().uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{" +
                        "\"username\": \"billy\"," +
                        "\"password\":\"billyeah\"," +
                        "\"role\":\"ROLE_ADMIN\"" +
                        "}")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }


    @Test
    void createBooking() {
        WiremockInitializer.wireMockServer.stubFor(post(urlPathMatching("/api/rooms/1/hold")).willReturn(okJson("{}")));

        webTestClient.post().uri("/api/bookings")
                .header("X-Internal-Auth", "eyJ1c2VybmFtZSI6ImlzZXJhIiwicm9sZXMiOlsiUk9MRV9VU0VSIl19")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{" +
                        "\"roomId\":1," +
                        "\"startDate\":\"2026-01-15\"," +
                        "\"endDate\":\"2026-01-22\"" +
                        "}")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("$.status").isEqualTo("CONFIRMED");
    }
}
