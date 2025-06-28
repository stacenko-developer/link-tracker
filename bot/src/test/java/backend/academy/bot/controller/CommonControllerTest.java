package backend.academy.bot.controller;

import static backend.academy.bot.constants.ConstValues.RATE_LIMIT_EXCEEDED_MESSAGE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.bot.TestConfiguration;
import backend.academy.bot.configuration.resilience.RateLimiterProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

public abstract class CommonControllerTest extends TestConfiguration {

    private static final int MAX_VALUE_FOR_IP = 255;
    private static final String IP_FORMAT = "%s.%s.%s.%s";

    private final Set<String> generatedIps = new HashSet<>();
    private final Random random = new Random();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RateLimiterProperties rateLimiterProperties;

    protected ResultMatcher[] rateLimitExceededMatchers() {
        return new ResultMatcher[] {status().isTooManyRequests(), content().string(RATE_LIMIT_EXCEEDED_MESSAGE)};
    }

    @SneakyThrows
    protected void testRateLimitingProcess(Function<String, ResultActions> requestProvider) {
        String ip = generateRandomIp();

        for (int i = 0; i < rateLimiterProperties.limitForPeriod(); i++) {
            requestProvider.apply(ip).andExpect(status().isOk());
        }

        requestProvider.apply(ip).andExpectAll(rateLimitExceededMatchers());
    }

    protected String generateRandomIp() {
        String ip;

        do {
            ip = String.format(
                    IP_FORMAT,
                    random.nextInt(MAX_VALUE_FOR_IP + 1),
                    random.nextInt(MAX_VALUE_FOR_IP + 1),
                    random.nextInt(MAX_VALUE_FOR_IP + 1),
                    random.nextInt(MAX_VALUE_FOR_IP + 1));
        } while (generatedIps.contains(ip));

        generatedIps.add(ip);

        return ip;
    }
}
