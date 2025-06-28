package backend.academy.scrapper.client.bot.dto;

import backend.academy.scrapper.linkTracker.dto.EventDto;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record ImmediateLinkUpdate(
        @NotNull Long id, @NotNull URI url, @NotNull EventDto eventDto, @NotNull List<Long> tgChatIds) {}
