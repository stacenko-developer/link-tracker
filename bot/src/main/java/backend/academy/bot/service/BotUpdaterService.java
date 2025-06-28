package backend.academy.bot.service;

import backend.academy.bot.configuration.updateLink.DefaultEventProperties;
import backend.academy.bot.configuration.updateLink.DigestProperties;
import backend.academy.bot.dto.linkUpdate.DigestLinkUpdate;
import backend.academy.bot.dto.linkUpdate.EventDto;
import backend.academy.bot.dto.linkUpdate.ImmediateLinkUpdate;
import backend.academy.bot.event.EventHandler;
import backend.academy.bot.event.EventHandlerProvider;
import backend.academy.bot.exception.event.InvalidTimestampException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotUpdaterService {

    private static final String MESSAGE_FORMAT = "%s%n%s";
    private static final String MESSAGE_SEPARATOR = "%s%n%n";

    private final BotService botService;
    private final EventHandlerProvider eventHandlerProvider;
    private final DefaultEventProperties defaultEventProperties;
    private final DigestProperties digestProperties;

    public void immediatelyUpdate(ImmediateLinkUpdate immediateLinkUpdate) {
        validateTimestamps(immediateLinkUpdate.eventDto());

        String baseMessage = String.format(defaultEventProperties.message(), immediateLinkUpdate.url());

        immediateLinkUpdate.tgChatIds().forEach(tgChat -> {
            String message = buildMessage(baseMessage, immediateLinkUpdate.eventDto());
            botService.sendMessage(tgChat, message);
        });
    }

    public void digestUpdate(DigestLinkUpdate digestLinkUpdate) {
        StringBuilder digestResult = new StringBuilder(String.format(MESSAGE_SEPARATOR, digestProperties.header()));

        digestLinkUpdate.linkInfos().forEach(linkInfo -> {
            validateTimestamps(linkInfo.eventDto());
            String baseMessage = String.format(defaultEventProperties.message(), linkInfo.url());
            String message = buildMessage(baseMessage, linkInfo.eventDto());
            digestResult.append(String.format(MESSAGE_SEPARATOR, message));
        });

        botService.sendMessage(digestLinkUpdate.tgChatId(), digestResult.toString());
    }

    private void validateTimestamps(EventDto eventDto) {
        if (eventDto.updatedAt() < eventDto.createdAt()) {
            throw new InvalidTimestampException();
        }
    }

    private String buildMessage(String baseMessage, EventDto eventDto) {
        EventHandler eventHandler = eventHandlerProvider.getEventHandler(eventDto.type());
        return eventHandler != null
                ? String.format(MESSAGE_FORMAT, baseMessage, eventHandler.getMessage(eventDto))
                : baseMessage;
    }
}
