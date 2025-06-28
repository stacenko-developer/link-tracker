package backend.academy.bot.dto.linkUpdate;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkInfo(@NotNull URI url, @NotNull EventDto eventDto) {}
