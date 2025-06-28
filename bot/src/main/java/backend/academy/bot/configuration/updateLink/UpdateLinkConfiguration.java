package backend.academy.bot.configuration.updateLink;

import backend.academy.bot.configuration.updateLink.github.GithubIssueProperties;
import backend.academy.bot.configuration.updateLink.github.GithubPullRequestProperties;
import backend.academy.bot.configuration.updateLink.stackoverflow.StackoverflowAnswerProperties;
import backend.academy.bot.configuration.updateLink.stackoverflow.StackoverflowCommentProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    GithubPullRequestProperties.class,
    GithubIssueProperties.class,
    StackoverflowAnswerProperties.class,
    StackoverflowCommentProperties.class,
    DefaultEventProperties.class,
    DigestProperties.class
})
public class UpdateLinkConfiguration {}
