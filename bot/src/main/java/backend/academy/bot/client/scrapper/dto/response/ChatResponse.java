package backend.academy.bot.client.scrapper.dto.response;

import backend.academy.bot.client.scrapper.dto.NotificationModeDto;
import jakarta.validation.constraints.NotNull;

public record ChatResponse(@NotNull Long id, @NotNull NotificationModeDto notificationModeDto) {}
