package backend.academy.scrapper.linkTracker;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.client.github.dto.ActorDto;
import backend.academy.scrapper.client.github.dto.GithubEventDto;
import backend.academy.scrapper.client.github.dto.GithubMetaInformationDto;
import backend.academy.scrapper.client.github.dto.PayloadDto;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import backend.academy.scrapper.exception.link.LinkNotSupportedException;
import backend.academy.scrapper.linkTracker.dto.EventDto;
import backend.academy.scrapper.service.client.GithubClientService;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GithubLinkTrackerTest {

    @InjectMocks
    private GithubLinkTracker githubLinkTracker;

    @Mock
    private GithubClientService githubServiceClient;

    @ParameterizedTest
    @MethodSource("getArgumentsForTrackGithubUrlWithCorrectArguments")
    public void trackGithubUrlWithCorrectArguments_ShouldCorrectlyTracking(String owner, String repo) {
        URI expectedUrl = URI.create(String.format("https://github.com/%s/%s", owner, repo));
        long expectedCreationDateMillis = 0L;
        long expectedLastActivityDateMillis = 0L;
        String expectedEventName = "PullRequestEvent";
        String expectedUserLogin = "stacenko-developer";
        String expectedPullRequestTitle = "Pull Request Title";
        String expectedPullRequestDescription = "Pull Request Description";

        OffsetDateTime expectedCreationDateTime =
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(expectedCreationDateMillis), ZoneOffset.UTC);
        OffsetDateTime expectedLastActivityDateTime =
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(expectedLastActivityDateMillis), ZoneOffset.UTC);

        List<GithubEventDto> githubEvents = List.of(new GithubEventDto(
                new PayloadDto(
                        null,
                        new GithubMetaInformationDto(
                                expectedPullRequestTitle,
                                expectedPullRequestDescription,
                                expectedCreationDateTime,
                                expectedLastActivityDateTime)),
                expectedCreationDateTime,
                expectedEventName,
                new ActorDto(expectedUserLogin)));

        when(githubServiceClient.getRepositoryEvents(owner, repo)).thenReturn(new ResponseDto<>(githubEvents, null));

        List<EventDto> expectedEvents = List.of(new EventDto(
                expectedEventName,
                expectedPullRequestTitle,
                expectedUserLogin,
                expectedCreationDateMillis,
                expectedLastActivityDateMillis,
                expectedPullRequestDescription));

        List<EventDto> actualEvents = githubLinkTracker.track(expectedUrl);

        Assertions.assertEquals(expectedEvents, actualEvents);
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForTrackGithubUrlWithIncorrectArguments")
    public void trackGithubUrlWithIncorrectArguments_ShouldThrowLinkNotSupportedException(String url) {
        assertThatThrownBy(() -> {
                    githubLinkTracker.track(URI.create(url));
                })
                .isInstanceOf(LinkNotSupportedException.class)
                .satisfies(exception -> {
                    final LinkNotSupportedException ex = (LinkNotSupportedException) exception;

                    String expectedDescription =
                            String.format(ExceptionDescriptionValues.LINK_NOT_SUPPORTED_EXCEPTION_DESCRIPTION, url);
                    String actualDescription = ex.description();

                    Assertions.assertEquals(expectedDescription, actualDescription);
                })
                .hasMessageContaining(ExceptionMessageValues.LINK_NOT_SUPPORTED_EXCEPTION_MESSAGE);
    }

    private static List<Object[]> getArgumentsForTrackGithubUrlWithCorrectArguments() {
        return List.of(
                new Object[] {"User123", "MyRepo"},
                new Object[] {"dev_user", "test-repo"},
                new Object[] {"cool.dev", "super_project"},
                new Object[] {"X-Y-Z", "Repo_2024"},
                new Object[] {"alpha_beta.gamma", "core-v1.2"},
                new Object[] {"a", "b"},
                new Object[] {"LongName_42", "MegaRepo-9000"},
                new Object[] {"Test.123-xyz", "a_b.c-d_e.f"},
                new Object[] {"dot_user-title", "_under.score-test"},
                new Object[] {"Final-Test", "1.2.3_final-release"});
    }

    private static List<String> getArgumentsForTrackGithubUrlWithIncorrectArguments() {
        return List.of(
                "https://github.com/user%20123/my%20repo",
                "https://github.com/user123/my%40repo",
                "https://github.com/user123/my%2Frepo",
                "https://github.com/user123/my%23repo",
                "https://github.com/user123/my%2Frepo",
                "https://github.com/user123/my%20repo%20test",
                "https://github.com/user@domain/my#repo",
                "https://github.com/user123/repo/repo",
                "https://github.com/user123/",
                "https://github.com/user123",
                "https://github.com/",
                "https://gitub.com/user/repo",
                "https://github.com//",
                "http://github.com/user/repo");
    }
}
