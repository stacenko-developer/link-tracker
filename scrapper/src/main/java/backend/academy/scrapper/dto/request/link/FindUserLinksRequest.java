package backend.academy.scrapper.dto.request.link;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public record FindUserLinksRequest(@NotNull Long chatId, List<String> tagNames) {

    public FindUserLinksRequest {
        if (tagNames != null) {
            tagNames =
                    tagNames.stream().filter(StringUtils::isNotBlank).distinct().toList();
        }
    }
}
