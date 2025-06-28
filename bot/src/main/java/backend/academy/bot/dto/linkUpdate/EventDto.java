package backend.academy.bot.dto.linkUpdate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventDto(
        @NotBlank String type,
        String title,
        @NotBlank String user,
        @NotNull Long createdAt,
        @NotNull Long updatedAt,
        String text) {}
