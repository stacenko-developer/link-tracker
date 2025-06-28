package backend.academy.scrapper.linkTracker;

import backend.academy.common.utils.DateTimeUtils;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import backend.academy.scrapper.client.bot.dto.LinkInfo;
import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chatDigestState.service.ChatDigestStateService;
import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilter;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.link.service.LinkDaoService;
import backend.academy.scrapper.dto.FilterDto;
import backend.academy.scrapper.enums.NotificationMode;
import backend.academy.scrapper.linkTracker.dto.EventDto;
import backend.academy.scrapper.metric.ScrapeTimerProvider;
import backend.academy.scrapper.sender.primary.NotificationSender;
import io.micrometer.core.instrument.Timer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LinkHandler {

    private final LinkDaoService linkDaoService;
    private final ChatDigestStateService chatDigestStateService;

    private final LinkTrackerProvider linkTrackerProvider;
    private final ScrapeTimerProvider scrapeTimerProvider;

    private final NotificationSender notificationSender;

    @Transactional
    public void handleLinks(List<Link> links) {
        links.stream().filter(link -> !link.chats().isEmpty()).forEach((link) -> {
            Timer timer = scrapeTimerProvider.getScrapeTimer(link.url());

            if (timer != null) {
                timer.record(() -> handleSingleLink(link));
            } else {
                handleSingleLink(link);
            }
        });
    }

    private void handleSingleLink(Link link) {
        URI url = URI.create(link.url());

        LinkTracker linkTracker = linkTrackerProvider.getLinkTracker(url);
        List<EventDto> events = linkTracker.track(url);
        Long lastTrackedAt = DateTimeUtils.getNowUtc();
        List<EventDto> validEvents = getValidLinkEvents(events, link.lastUpdatedAt());

        if (validEvents == null || validEvents.isEmpty()) {
            linkDaoService.updateAfterTracking(url, link.lastUpdatedAt(), lastTrackedAt);
            return;
        }

        Map<EventDto, ImmediateLinkUpdate> eventsToSend = new HashMap<>();

        for (Chat chat : link.chats()) {
            List<EventDto> filteredEvents = linkTracker.getFilteredEvents(validEvents, getUserFilters(chat, link.id()));

            if (NotificationMode.DAILY_DIGEST.equals(chat.notificationMode())) {
                filteredEvents.forEach(
                        event -> chatDigestStateService.addLinkInfoToDigestState(chat.id(), new LinkInfo(url, event)));
                continue;
            }

            filteredEvents.forEach(event -> {
                ImmediateLinkUpdate update =
                        eventsToSend.computeIfAbsent(event, e -> createImmediatelyLinkUpdate(link, e));
                update.tgChatIds().add(chat.id());
            });

            eventsToSend.values().forEach(notificationSender::sendImmediateLinkUpdate);
        }

        linkDaoService.updateAfterTracking(url, getLastUpdatedTime(validEvents), lastTrackedAt);
    }

    private List<FilterDto> getUserFilters(Chat chat, Long linkId) {
        return chat.chatLinkFilters().stream()
                .filter(clf -> clf.link().id().equals(linkId))
                .map(ChatLinkFilter::filter)
                .map(filter -> new FilterDto(filter.key(), filter.value()))
                .toList();
    }

    private List<EventDto> getValidLinkEvents(List<EventDto> events, Long lastUpdatedAt) {
        return events == null
                ? null
                : events.stream()
                        .filter(event -> isValidEvent(event, lastUpdatedAt))
                        .toList();
    }

    private Long getLastUpdatedTime(List<EventDto> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }

        long lastUpdated = 0L;

        for (EventDto eventDto : events) {
            if (eventDto.createdAt() > lastUpdated) {
                lastUpdated = eventDto.createdAt();
            }
        }

        return lastUpdated;
    }

    private boolean isValidEvent(EventDto eventDto, Long lastUpdatedAt) {
        return eventDto != null && eventDto.createdAt() != null && eventDto.createdAt() > lastUpdatedAt;
    }

    private ImmediateLinkUpdate createImmediatelyLinkUpdate(Link link, EventDto eventDto) {
        return new ImmediateLinkUpdate(link.id(), URI.create(link.url()), eventDto, new ArrayList<>());
    }
}
