package backend.academy.bot.client.scrapper.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record FindUserLinksRequest(@NotNull Long chatId, @NotNull List<String> tagNames) {}
