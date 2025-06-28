package backend.academy.bot.client.scrapper.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatSettingsRequest(@NotBlank String notificationModeCode) {}
