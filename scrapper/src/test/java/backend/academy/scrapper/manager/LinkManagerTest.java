package backend.academy.scrapper.manager;

import static backend.academy.scrapper.ConstValues.DEFAULT_ADD_LINK_REQUEST;
import static backend.academy.scrapper.ConstValues.DEFAULT_AVAILABLE_FILTERS;
import static backend.academy.scrapper.ConstValues.DEFAULT_AVAILABLE_URLS;
import static backend.academy.scrapper.ConstValues.DEFAULT_CHAT_ID;
import static backend.academy.scrapper.ConstValues.DEFAULT_EVENT_FILTER_KEY;
import static backend.academy.scrapper.ConstValues.DEFAULT_FILTERS;
import static backend.academy.scrapper.ConstValues.DEFAULT_TAGS;
import static backend.academy.scrapper.ConstValues.DEFAULT_URL;
import static backend.academy.scrapper.ConstValues.DEFAULT_USER_FILTER_KEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.cache.cleaner.CacheCleaner;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import backend.academy.scrapper.dto.ChatLinkDto;
import backend.academy.scrapper.dto.FilterDto;
import backend.academy.scrapper.dto.TagDto;
import backend.academy.scrapper.dto.request.link.AddLinkRequest;
import backend.academy.scrapper.dto.request.link.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.link.LinkResponse;
import backend.academy.scrapper.exception.filter.FilterNotSupportedException;
import backend.academy.scrapper.exception.link.LinkNotSupportedException;
import backend.academy.scrapper.linkTracker.GithubLinkTracker;
import backend.academy.scrapper.linkTracker.LinkTrackerProvider;
import backend.academy.scrapper.service.LinkService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LinkManagerTest {

    @InjectMocks
    private LinkManager linkManager;

    @Mock
    private LinkService linkService;

    @Mock
    private LinkTrackerProvider linkTrackerProvider;

    @Mock
    private GithubLinkTracker githubLinkTracker;

    @Mock
    private CacheCleaner cacheCleaner;

    @Test
    public void addLinkWithCorrectArguments_ShouldCorrectlyAdd() {
        ChatLinkDto linkToCreate = DEFAULT_ADD_LINK_REQUEST.toChatLinkDto();

        when(githubLinkTracker.isFilterSupport(DEFAULT_USER_FILTER_KEY)).thenReturn(true);
        when(githubLinkTracker.isFilterSupport(DEFAULT_EVENT_FILTER_KEY)).thenReturn(true);
        when(linkTrackerProvider.getLinkTracker(DEFAULT_URL)).thenReturn(githubLinkTracker);
        when(linkService.addLink(DEFAULT_CHAT_ID, linkToCreate)).thenReturn(linkToCreate);

        LinkResponse linkResponse = linkManager.addLink(DEFAULT_CHAT_ID, DEFAULT_ADD_LINK_REQUEST);

        URI expectedUrl = DEFAULT_URL;
        List<String> expectedTags = DEFAULT_TAGS;
        List<String> expectedFilters = DEFAULT_FILTERS;

        URI actualUrl = linkResponse.url();
        List<String> actualTags = linkResponse.tags();
        List<String> actualFilters = linkResponse.filters();

        Assertions.assertEquals(expectedUrl, actualUrl);
        Assertions.assertEquals(expectedTags, actualTags);
        Assertions.assertEquals(expectedFilters, actualFilters);
    }

    @Test
    public void addNotSupportedLink_ShouldThrowLinkNotSupportedException() {
        when(linkTrackerProvider.getLinkTracker(DEFAULT_URL)).thenReturn(null);
        when(linkTrackerProvider.getAvailableUrls()).thenReturn(DEFAULT_AVAILABLE_URLS);

        assertThatThrownBy(() -> {
                    linkManager.addLink(DEFAULT_CHAT_ID, DEFAULT_ADD_LINK_REQUEST);
                })
                .isInstanceOf(LinkNotSupportedException.class)
                .satisfies(exception -> {
                    final LinkNotSupportedException ex = (LinkNotSupportedException) exception;

                    String descriptionFormat = ExceptionDescriptionValues.LINK_NOT_SUPPORTED_EXCEPTION_DESCRIPTION
                            + "%n"
                            + ExceptionDescriptionValues.AVAILABLE_LINKS_DESCRIPTION;

                    String expectedDescription = String.format(
                            descriptionFormat, DEFAULT_URL, String.join("\n", linkTrackerProvider.getAvailableUrls()));
                    String actualDescription = ex.description();

                    Assertions.assertEquals(expectedDescription, actualDescription);
                })
                .hasMessageContaining(ExceptionMessageValues.LINK_NOT_SUPPORTED_EXCEPTION_MESSAGE);
    }

    @Test
    public void addLinkWithNotSupportedFilter_ShouldThrowFilterNotSupportedException() {
        String unsupportedFilterKey = "unsupported";
        String unsupportedFilter = String.format("%s:value", unsupportedFilterKey);
        AddLinkRequest addLinkRequest = new AddLinkRequest(DEFAULT_URL, DEFAULT_TAGS, List.of(unsupportedFilter));

        when(githubLinkTracker.isFilterSupport(unsupportedFilterKey)).thenReturn(false);
        when(githubLinkTracker.getFilters()).thenReturn(DEFAULT_AVAILABLE_FILTERS);
        when(linkTrackerProvider.getLinkTracker(DEFAULT_URL)).thenReturn(githubLinkTracker);

        assertThatThrownBy(() -> {
                    linkManager.addLink(DEFAULT_CHAT_ID, addLinkRequest);
                })
                .isInstanceOf(FilterNotSupportedException.class)
                .satisfies(exception -> {
                    final FilterNotSupportedException ex = (FilterNotSupportedException) exception;

                    String expectedDescription = String.format(
                            ExceptionDescriptionValues.FILTER_NOT_SUPPORTED_EXCEPTION_DESCRIPTION,
                            unsupportedFilterKey,
                            addLinkRequest.link(),
                            githubLinkTracker.getFilters());
                    String actualDescription = ex.description();

                    Assertions.assertEquals(expectedDescription, actualDescription);
                })
                .hasMessageContaining(ExceptionMessageValues.FILTER_NOT_SUPPORTED_EXCEPTION_MESSAGE);
    }

    @Test
    public void deleteLinkWithCorrectArguments_ShouldReturnDeletedLink() {
        long linkId = 1;
        List<TagDto> tagsToRemove = DEFAULT_TAGS.stream().map(TagDto::new).toList();

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(DEFAULT_URL);
        List<FilterDto> filters = List.of(new FilterDto(DEFAULT_USER_FILTER_KEY, DEFAULT_USER_FILTER_KEY));
        ChatLinkDto deletedLink = new ChatLinkDto(linkId, DEFAULT_URL, tagsToRemove, filters);

        when(linkService.deleteLink(DEFAULT_CHAT_ID, removeLinkRequest)).thenReturn(deletedLink);

        LinkResponse linkResponse = linkManager.deleteLink(DEFAULT_CHAT_ID, removeLinkRequest);

        URI expectedUrl = DEFAULT_URL;
        List<String> expectedTags = DEFAULT_TAGS;
        List<String> expectedFilters =
                List.of(String.format("%s:%s", DEFAULT_USER_FILTER_KEY, DEFAULT_USER_FILTER_KEY));

        URI actualUrl = linkResponse.url();
        List<String> actualTags = linkResponse.tags();
        List<String> actualFilters = linkResponse.filters();

        Assertions.assertEquals(expectedUrl, actualUrl);
        Assertions.assertEquals(expectedTags, actualTags);
        Assertions.assertEquals(expectedFilters, actualFilters);
    }
}
