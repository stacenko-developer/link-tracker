package backend.academy.scrapper.client.bot.dto;

import backend.academy.scrapper.linkTracker.dto.EventDto;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkInfo(@NotNull URI url, @NotNull EventDto eventDto) {}
