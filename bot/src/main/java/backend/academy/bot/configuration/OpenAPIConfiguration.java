package backend.academy.bot.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    private static final String API_SPEC_VERSION = "3.1.0";
    private static final String API_NAME = "Bot API";
    private static final String API_VERSION = "1.0.0";

    private static final String API_AUTHOR = "Artem Stacenko";
    private static final String API_CONTACT_URL = "https://github.com";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .openapi(API_SPEC_VERSION)
                .info(new Info()
                        .title(API_NAME)
                        .version(API_VERSION)
                        .contact(new Contact().name(API_AUTHOR).url(API_CONTACT_URL)));
    }
}
