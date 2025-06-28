package backend.academy.bot.configuration;

import static backend.academy.bot.constants.MetricConstValues.USER_MESSAGES_COUNTER_METRIC;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfiguration {

    @Bean
    public Counter userMessagesCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter(USER_MESSAGES_COUNTER_METRIC);
    }
}
