package backend.academy.bot.dto.linkUpdate;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public record DigestLinkUpdate(@NotNull Long tgChatId, @NotNull List<LinkInfo> linkInfos) {

    public DigestLinkUpdate {
        if (linkInfos != null) {
            linkInfos = linkInfos.stream().filter(Objects::nonNull).distinct().toList();
        }
    }
}
