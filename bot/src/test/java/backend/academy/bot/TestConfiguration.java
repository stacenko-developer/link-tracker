package backend.academy.bot;

import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
@AutoConfigureObservability
@Import(TestcontainersConfiguration.class)
public abstract class TestConfiguration {

    private static final KafkaContainer KAFKA_CONTAINER;

    static {
        KAFKA_CONTAINER = new KafkaContainer("apache/kafka-native:3.8.1").withExposedPorts(9092);
        KAFKA_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> "8080");
        registry.add("management.server.port", () -> "8000");
        registry.add("client.scrapper.base-url", () -> "http://localhost:8090");

        registry.add("spring.data.redis.port", () -> "6379");

        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }
}
