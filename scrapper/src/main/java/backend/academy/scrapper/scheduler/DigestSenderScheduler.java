package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.configuration.scheduler.SendDigestNotificationsProperties;
import backend.academy.scrapper.dao.chatDigestState.service.ChatDigestStateService;
import backend.academy.scrapper.sender.primary.NotificationSender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DigestSenderScheduler {

    private final ChatDigestStateService chatDigestStateService;
    private final NotificationSender notificationSender;
    private final SendDigestNotificationsProperties sendDigestNotificationsProperties;

    @Scheduled(cron = "${scheduler.send-digest-notifications.start-time-cron}")
    public void sendDigestNotifications() {
        List<DigestLinkUpdate> digestLinkUpdates =
                chatDigestStateService.getAllDigestStates(sendDigestNotificationsProperties.batchSize());

        while (!digestLinkUpdates.isEmpty()) {
            for (DigestLinkUpdate digestLinkUpdate : digestLinkUpdates) {
                notificationSender.sendDigestLinkUpdate(digestLinkUpdate);

                chatDigestStateService.deleteDigestState(digestLinkUpdate.tgChatId());
            }

            digestLinkUpdates =
                    chatDigestStateService.getAllDigestStates(sendDigestNotificationsProperties.batchSize());
        }
    }
}
