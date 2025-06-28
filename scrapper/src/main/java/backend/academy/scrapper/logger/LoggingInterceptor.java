package backend.academy.scrapper.logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String LOG_MESSAGE_FORMAT = "Вызван метод: {}.{} с параметрами: {}";

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            String className = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();
            Object[] parameters = handlerMethod.getMethod().getParameters();

            log.info(LOG_MESSAGE_FORMAT, className, methodName, Arrays.toString(parameters));
        }
        return true;
    }
}
