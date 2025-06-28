package backend.academy.bot.client.scrapper.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationModeDto(@NotBlank String code, @NotBlank String title, @NotBlank String description) {}
