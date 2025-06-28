package backend.academy.bot.event.github;

import static org.mockito.Mockito.when;

import backend.academy.bot.configuration.updateLink.github.GithubPullRequestProperties;
import backend.academy.bot.event.EventHandler;
import backend.academy.bot.event.EventHandlerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GithubPullRequestEventHandlerTest extends EventHandlerTest {

    @InjectMocks
    private GithubPullRequestEventHandler githubPullRequestEventHandler;

    @Mock
    private GithubPullRequestProperties githubPullRequestProperties;

    @Override
    protected EventHandler getEventHandler() {
        return githubPullRequestEventHandler;
    }

    @BeforeEach
    public void setUp() {
        when(githubPullRequestProperties.message()).thenReturn(MESSAGE);
    }
}
