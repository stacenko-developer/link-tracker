package backend.academy.bot.client.scrapper.dto.request;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record AddLinkRequest(@NotNull URI link, @NotNull List<String> tags, @NotNull List<String> filters) {}
