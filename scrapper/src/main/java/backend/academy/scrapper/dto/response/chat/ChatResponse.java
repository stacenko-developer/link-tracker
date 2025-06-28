package backend.academy.scrapper.dto.response.chat;

import backend.academy.scrapper.dto.NotificationModeDto;
import jakarta.validation.constraints.NotNull;

public record ChatResponse(@NotNull Long id, @NotNull NotificationModeDto notificationModeDto) {}
