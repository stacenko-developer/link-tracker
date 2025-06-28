package backend.academy.scrapper.scheduler;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.configuration.scheduler.LinkTrackingSchedulerProperties;
import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.link.service.LinkDaoService;
import backend.academy.scrapper.enums.NotificationMode;
import backend.academy.scrapper.linkTracker.LinkHandler;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LinkTrackerSchedulerTest {

    @InjectMocks
    private LinkTrackerScheduler linkTrackerScheduler;

    @Mock
    private LinkDaoService linkDaoService;

    @Mock
    private LinkTrackingSchedulerProperties linkTrackingSchedulerProperties;

    @Mock
    private LinkHandler linkHandler;

    @Test
    public void trackUpdatingLinksWithCorrectArguments_ShouldSendNotificationsToRightUsers() {
        long retryDelayMinutes = 10;
        int linksLimit = 100;
        int threadsCount = 1;

        when(linkTrackingSchedulerProperties.retryDelayMinutes()).thenReturn(Duration.ofMinutes(retryDelayMinutes));
        when(linkTrackingSchedulerProperties.linksLimitPerTrack()).thenReturn(linksLimit);
        when(linkTrackingSchedulerProperties.threadsCount()).thenReturn(threadsCount);

        Long firstChatId = 1L;
        Long secondChatId = 2L;

        NotificationMode firstChatNotificationMode = NotificationMode.IMMEDIATE;
        NotificationMode secondChatNotificationMode = NotificationMode.IMMEDIATE;

        Chat firstChat = new Chat();
        firstChat.id(firstChatId);
        firstChat.notificationMode(firstChatNotificationMode);

        Chat secondChat = new Chat();
        secondChat.id(secondChatId);
        secondChat.notificationMode(secondChatNotificationMode);

        URI firstUrl = URI.create("https://github.com/owner1/repo1");
        URI secondUrl = URI.create("https://github.com/owner2/repo2");

        Long firstLinkId = 1L;
        Long secondLinkId = 2L;
        Long lastUpdated = 0L;

        Link firstLink = new Link();
        firstLink.id(firstLinkId);
        firstLink.chats(List.of(firstChat));
        firstLink.url(firstUrl.toString());
        firstLink.chatLinkFilters(new ArrayList<>());
        firstLink.lastUpdatedAt(lastUpdated);

        Link secondLink = new Link();
        secondLink.id(secondLinkId);
        secondLink.chats(List.of(secondChat));
        secondLink.url(secondUrl.toString());
        secondLink.chatLinkFilters(new ArrayList<>());
        secondLink.lastUpdatedAt(lastUpdated);

        when(linkDaoService.getAllOldLinks(anyLong(), anyInt())).thenReturn(List.of(firstLink, secondLink));

        linkTrackerScheduler.trackUpdatingLinks();

        verify(linkHandler).handleLinks(List.of(firstLink, secondLink));
    }
}
