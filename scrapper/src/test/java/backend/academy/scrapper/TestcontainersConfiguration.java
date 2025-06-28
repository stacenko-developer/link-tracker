package backend.academy.scrapper;

import java.nio.file.Path;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import lombok.SneakyThrows;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

// isolated from the "bot" module's containers!
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @RestartScope
    @ServiceConnection(name = "redis")
    public GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);
    }

    @Bean
    @RestartScope
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17-alpine")
                .withDatabaseName("scrapper-service")
                .withUsername("postgres")
                .withPassword("postgres");

        postgreSQLContainer.start();
        runMigrations(postgreSQLContainer);

        return postgreSQLContainer;
    }

    @SneakyThrows
    private static void runMigrations(JdbcDatabaseContainer<?> container) {
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(container.createConnection("")));
        Liquibase liquibase =
                new Liquibase("master.xml", new DirectoryResourceAccessor(Path.of("migrations")), database);
        liquibase.update();
    }
}
