package backend.academy.scrapper;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class TestConfiguration {

    private static final KafkaContainer KAFKA_CONTAINER;

    static {
        KAFKA_CONTAINER = new KafkaContainer("apache/kafka-native:3.8.1").withExposedPorts(9092);
        KAFKA_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> "8081");
        registry.add("management.server.port", () -> "8000");

        registry.add("client.bot.base-url", () -> "http://localhost:8090");
        registry.add("client.github.base-url", () -> "http://localhost:8090");
        registry.add("client.stackoverflow.base-url", () -> "http://localhost:8090");

        registry.add("app.stackoverflow.key", () -> "key");
        registry.add("app.stackoverflow.access_token", () -> "access_token");

        registry.add("spring.data.redis.port", () -> "6379");

        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }
}
