package backend.academy.scrapper.manager;

import static backend.academy.scrapper.constants.CacheConstValues.SCRAPPER_TRACKING_LINKS_CACHE_NAME;

import backend.academy.scrapper.cache.cleaner.CacheCleaner;
import backend.academy.scrapper.dto.ChatLinkDto;
import backend.academy.scrapper.dto.FilterDto;
import backend.academy.scrapper.dto.request.link.AddLinkRequest;
import backend.academy.scrapper.dto.request.link.FindUserLinksRequest;
import backend.academy.scrapper.dto.request.link.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.link.LinkResponse;
import backend.academy.scrapper.dto.response.link.ListLinksResponse;
import backend.academy.scrapper.exception.filter.FilterNotSupportedException;
import backend.academy.scrapper.exception.link.LinkNotSupportedException;
import backend.academy.scrapper.linkTracker.LinkTracker;
import backend.academy.scrapper.linkTracker.LinkTrackerProvider;
import backend.academy.scrapper.service.LinkService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkManager {

    private final LinkService linkService;
    private final LinkTrackerProvider linkTrackerProvider;
    private final CacheCleaner cacheCleaner;

    @Cacheable(
            value = SCRAPPER_TRACKING_LINKS_CACHE_NAME,
            key = "T(backend.academy.scrapper.cache.CacheKeyGenerator).generateKey(#findUserLinksRequest)")
    public ListLinksResponse getAllUserTrackingLinks(FindUserLinksRequest findUserLinksRequest) {
        List<LinkResponse> links =
                linkService
                        .getAllUserTrackingLinks(findUserLinksRequest.chatId(), findUserLinksRequest.tagNames())
                        .stream()
                        .map(ChatLinkDto::toLinkResponse)
                        .toList();

        return new ListLinksResponse(links, links.size());
    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        LinkTracker linkTracker = linkTrackerProvider.getLinkTracker(addLinkRequest.link());
        ChatLinkDto chatLinkDto = addLinkRequest.toChatLinkDto();

        if (linkTracker == null) {
            throw new LinkNotSupportedException(addLinkRequest.link(), linkTrackerProvider.getAvailableUrls());
        }

        for (FilterDto filterDto : chatLinkDto.filters()) {
            if (!linkTracker.isFilterSupport(filterDto.key())) {
                throw new FilterNotSupportedException(filterDto.key(), addLinkRequest.link(), linkTracker.getFilters());
            }
        }

        ChatLinkDto createdLink = linkService.addLink(tgChatId, chatLinkDto);

        cacheCleaner.cleanLinkTrackingCache(tgChatId, addLinkRequest.tags());

        return createdLink.toLinkResponse();
    }

    public LinkResponse deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        ChatLinkDto chatLinkDto = linkService.deleteLink(tgChatId, removeLinkRequest);

        LinkResponse result = chatLinkDto.toLinkResponse();

        cacheCleaner.cleanLinkTrackingCache(tgChatId, result.tags());

        return result;
    }
}
