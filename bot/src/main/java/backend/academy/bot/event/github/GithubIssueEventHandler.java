package backend.academy.bot.event.github;

import backend.academy.bot.configuration.updateLink.github.GithubIssueProperties;
import backend.academy.bot.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubIssueEventHandler extends EventHandler {

    private final GithubIssueProperties githubIssueProperties;

    @Override
    public String getMessageFormat() {
        return githubIssueProperties.message();
    }

    @Override
    public String getName() {
        return githubIssueProperties.name();
    }
}
