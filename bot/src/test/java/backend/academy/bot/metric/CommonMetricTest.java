package backend.academy.bot.metric;

import backend.academy.bot.TestConfiguration;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonMetricTest extends TestConfiguration {

    private static final int CORRECT_METRIC_COUNT = 1;

    @Autowired
    private MeterRegistry meterRegistry;

    protected void testCounterContainingInMeterRegistry(String counterMetricName) {
        Collection<Counter> counters = meterRegistry.get(counterMetricName).counters();

        Assertions.assertEquals(CORRECT_METRIC_COUNT, counters.size());
    }
}
