package backend.academy.bot.event.stackoverflow;

import static org.mockito.Mockito.when;

import backend.academy.bot.configuration.updateLink.stackoverflow.StackoverflowAnswerProperties;
import backend.academy.bot.event.EventHandler;
import backend.academy.bot.event.EventHandlerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StackoverflowAnswerEventHandlerTest extends EventHandlerTest {

    @InjectMocks
    private StackoverflowAnswerEventHandler stackoverflowAnswerEventHandler;

    @Mock
    private StackoverflowAnswerProperties stackoverflowAnswerProperties;

    @Override
    protected EventHandler getEventHandler() {
        return stackoverflowAnswerEventHandler;
    }

    @BeforeEach
    public void setUp() {
        when(stackoverflowAnswerProperties.message()).thenReturn(MESSAGE);
    }
}
