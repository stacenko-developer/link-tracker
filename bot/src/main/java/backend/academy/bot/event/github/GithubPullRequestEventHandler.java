package backend.academy.bot.event.github;

import backend.academy.bot.configuration.updateLink.github.GithubPullRequestProperties;
import backend.academy.bot.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubPullRequestEventHandler extends EventHandler {

    private final GithubPullRequestProperties githubPullRequestProperties;

    @Override
    public String getMessageFormat() {
        return githubPullRequestProperties.message();
    }

    @Override
    public String getName() {
        return githubPullRequestProperties.name();
    }
}
