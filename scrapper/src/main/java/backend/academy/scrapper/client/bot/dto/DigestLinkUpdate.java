package backend.academy.scrapper.client.bot.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DigestLinkUpdate(@NotNull Long tgChatId, @NotNull List<LinkInfo> linkInfos) {}
