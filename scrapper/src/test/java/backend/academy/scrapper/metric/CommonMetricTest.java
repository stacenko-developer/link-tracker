package backend.academy.scrapper.metric;

import backend.academy.scrapper.TestConfiguration;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommonMetricTest extends TestConfiguration {

    private static final int CORRECT_METRIC_COUNT = 1;

    @Autowired
    protected MeterRegistry meterRegistry;

    protected void testGaugeContainingInMeterRegistry(String gaugeMetricName) {
        Collection<Gauge> gauges = meterRegistry.get(gaugeMetricName).gauges();

        Assertions.assertEquals(CORRECT_METRIC_COUNT, gauges.size());
    }

    protected void testTimerContainingInMeterRegistry(String timerMetricName) {
        Collection<Timer> timers = meterRegistry.get(timerMetricName).timers();

        Assertions.assertEquals(CORRECT_METRIC_COUNT, timers.size());
    }
}
