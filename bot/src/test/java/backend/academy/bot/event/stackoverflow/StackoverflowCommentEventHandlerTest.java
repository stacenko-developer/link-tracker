package backend.academy.bot.event.stackoverflow;

import static org.mockito.Mockito.when;

import backend.academy.bot.configuration.updateLink.stackoverflow.StackoverflowCommentProperties;
import backend.academy.bot.event.EventHandler;
import backend.academy.bot.event.EventHandlerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StackoverflowCommentEventHandlerTest extends EventHandlerTest {

    @InjectMocks
    private StackoverflowCommentEventHandler stackoverflowCommentEventHandler;

    @Mock
    private StackoverflowCommentProperties stackoverflowCommentProperties;

    @Override
    protected EventHandler getEventHandler() {
        return stackoverflowCommentEventHandler;
    }

    @BeforeEach
    public void setUp() {
        when(stackoverflowCommentProperties.message()).thenReturn(MESSAGE);
    }
}
