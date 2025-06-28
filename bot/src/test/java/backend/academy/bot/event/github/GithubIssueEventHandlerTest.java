package backend.academy.bot.event.github;

import static org.mockito.Mockito.when;

import backend.academy.bot.configuration.updateLink.github.GithubIssueProperties;
import backend.academy.bot.event.EventHandler;
import backend.academy.bot.event.EventHandlerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GithubIssueEventHandlerTest extends EventHandlerTest {

    @InjectMocks
    private GithubIssueEventHandler githubIssueEventHandler;

    @Mock
    private GithubIssueProperties githubIssueProperties;

    @Override
    protected EventHandler getEventHandler() {
        return githubIssueEventHandler;
    }

    @BeforeEach
    public void setUp() {
        when(githubIssueProperties.message()).thenReturn(MESSAGE);
    }
}
