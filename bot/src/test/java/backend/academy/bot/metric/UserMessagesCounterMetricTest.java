package backend.academy.bot.metric;

import static backend.academy.bot.constants.MetricConstValues.USER_MESSAGES_COUNTER_METRIC;

import backend.academy.bot.command.CommandHandler;
import backend.academy.bot.service.BotService;
import com.pengrad.telegrambot.model.Update;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class UserMessagesCounterMetricTest extends CommonMetricTest {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private BotService botService;

    @MockitoBean
    private CommandHandler commandHandler;

    @ParameterizedTest
    @MethodSource("getArgumentsForHandleUserCommand")
    public void handleUserCommand_ShouldIncrementUserMessagesCounter(List<Update> updates) {
        botService.processUpdates(updates);

        Counter userMessagesCounter =
                meterRegistry.find(USER_MESSAGES_COUNTER_METRIC).counter();

        Assertions.assertNotNull(userMessagesCounter);
        Assertions.assertEquals(updates.size(), userMessagesCounter.count());
    }

    @Test
    public void testUserMessagesCounterContainingInMeterRegistry() {
        testCounterContainingInMeterRegistry(USER_MESSAGES_COUNTER_METRIC);
    }

    private static List<List<Update>> getArgumentsForHandleUserCommand() {
        return List.of(List.of(), List.of(new Update()));
    }
}
