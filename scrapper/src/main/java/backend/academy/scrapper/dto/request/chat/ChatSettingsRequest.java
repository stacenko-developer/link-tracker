package backend.academy.scrapper.dto.request.chat;

import jakarta.validation.constraints.NotBlank;

public record ChatSettingsRequest(@NotBlank String notificationModeCode) {}
