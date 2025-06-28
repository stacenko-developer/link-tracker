package backend.academy.bot.logger;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@Configuration
@ControllerAdvice
public class ResponseLogging implements ResponseBodyAdvice<Object> {

    private static final String LOG_MESSAGE_FORMAT = "Метод: {}.{} возвращает результат: {}";

    @Override
    public boolean supports(
            @NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NotNull MethodParameter returnType,
            @NotNull MediaType selectedContentType,
            @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response) {
        String methodName = Objects.requireNonNull(returnType.getMethod()).getName();
        String className = returnType.getDeclaringClass().getSimpleName();

        log.info(LOG_MESSAGE_FORMAT, className, methodName, body);

        return body;
    }
}
