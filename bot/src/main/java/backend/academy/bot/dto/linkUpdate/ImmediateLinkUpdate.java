package backend.academy.bot.dto.linkUpdate;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Objects;

public record ImmediateLinkUpdate(
        @NotNull Long id, @NotNull URI url, @NotNull EventDto eventDto, @NotNull List<Long> tgChatIds) {
    public ImmediateLinkUpdate {
        if (tgChatIds != null) {
            tgChatIds = tgChatIds.stream().filter(Objects::nonNull).distinct().toList();
        }
    }
}
