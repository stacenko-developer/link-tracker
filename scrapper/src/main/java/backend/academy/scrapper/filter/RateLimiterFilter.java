package backend.academy.scrapper.filter;

import static backend.academy.scrapper.constants.ConstValues.RATE_LIMIT_EXCEEDED_MESSAGE;

import backend.academy.scrapper.configuration.resilience.RateLimiterProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@WebFilter("/*")
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final RateLimiterProperties rateLimiterProperties;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String ip = request.getRemoteAddr();

        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(ip, () -> RateLimiterConfig.custom()
                .limitForPeriod(rateLimiterProperties.limitForPeriod())
                .limitRefreshPeriod(rateLimiterProperties.limitRefreshPeriod())
                .timeoutDuration(rateLimiterProperties.timeoutDuration())
                .build());

        if (!rateLimiter.acquirePermission()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write(RATE_LIMIT_EXCEEDED_MESSAGE);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
