package backend.academy.scrapper.linkTracker;

import backend.academy.common.utils.DateTimeUtils;
import backend.academy.scrapper.client.github.dto.GithubEventDto;
import backend.academy.scrapper.exception.link.LinkNotSupportedException;
import backend.academy.scrapper.linkTracker.dto.EventDto;
import backend.academy.scrapper.service.client.GithubClientService;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubLinkTracker extends LinkTracker {

    private static final Pattern URL_PATTERN =
            Pattern.compile("https://github.com/([-a-zA-Z0-9._]+)/([-a-zA-Z0-9._]+)");
    private static final String LINK_TYPE = "https://github.com/{owner}/{repo}";

    private final GithubClientService githubServiceClient;

    @Override
    public Pattern getUrlPattern() {
        return URL_PATTERN;
    }

    @Override
    public String getLinkType() {
        return LINK_TYPE;
    }

    @Override
    public List<EventDto> track(URI url) {
        if (!isSupports(url)) {
            throw new LinkNotSupportedException(url);
        }

        Matcher matcher = URL_PATTERN.matcher(url.toString());

        if (!matcher.matches()) {
            return null;
        }

        String owner = matcher.group(1);
        String repo = matcher.group(2);

        List<GithubEventDto> githubEvents =
                githubServiceClient.getRepositoryEvents(owner, repo).content();

        if (githubEvents == null) {
            return null;
        }

        return githubEvents.stream()
                .filter(this::isValidEvent)
                .map(event -> new EventDto(
                        event.type(),
                        getTitle(event),
                        event.actor().login(),
                        getCreatedAt(event),
                        getUpdatedAt(event),
                        getText(event)))
                .toList();
    }

    private boolean isValidEvent(GithubEventDto githubEventDto) {
        return githubEventDto != null
                && StringUtils.isNotBlank(githubEventDto.type())
                && githubEventDto.createdAt() != null
                && githubEventDto.actor() != null
                && StringUtils.isNotBlank(githubEventDto.actor().login());
    }

    private String getTitle(GithubEventDto event) {
        if (event.payload() != null) {
            if (event.payload().pullRequest() != null) {
                return event.payload().pullRequest().title();
            } else if (event.payload().issue() != null) {
                return event.payload().issue().title();
            }
        }

        return null;
    }

    private Long getCreatedAt(GithubEventDto event) {
        if (event.payload() != null) {
            if (event.payload().pullRequest() != null) {
                return DateTimeUtils.toEpochMillis(event.payload().pullRequest().createdAt());
            } else if (event.payload().issue() != null) {
                return DateTimeUtils.toEpochMillis(event.payload().issue().createdAt());
            }
        }

        return DateTimeUtils.toEpochMillis(event.createdAt());
    }

    private Long getUpdatedAt(GithubEventDto event) {
        if (event.payload() != null) {
            if (event.payload().pullRequest() != null) {
                return DateTimeUtils.toEpochMillis(event.payload().pullRequest().updatedAt());
            } else if (event.payload().issue() != null) {
                return DateTimeUtils.toEpochMillis(event.payload().issue().updatedAt());
            }
        }

        return DateTimeUtils.toEpochMillis(event.createdAt());
    }

    private String getText(GithubEventDto event) {
        String text = null;

        if (event.payload() != null) {
            if (event.payload().pullRequest() != null) {
                text = event.payload().pullRequest().body();
            } else if (event.payload().issue() != null) {
                text = event.payload().issue().body();
            }
        }

        return StringUtils.isNotBlank(text) ? text : null;
    }
}
