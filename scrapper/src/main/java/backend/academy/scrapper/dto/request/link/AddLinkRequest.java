package backend.academy.scrapper.dto.request.link;

import backend.academy.scrapper.dto.ChatLinkDto;
import backend.academy.scrapper.dto.TagDto;
import backend.academy.scrapper.parser.FilterParser;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record AddLinkRequest(@NotNull URI link, @NotNull List<String> tags, @NotNull List<String> filters) {

    public AddLinkRequest {
        if (tags != null) {
            tags = tags.stream().filter(StringUtils::isNotBlank).distinct().toList();
        }

        if (filters != null) {
            filters =
                    filters.stream().filter(StringUtils::isNotBlank).distinct().toList();
        }
    }

    public ChatLinkDto toChatLinkDto() {
        return new ChatLinkDto(
                null,
                link,
                tags.stream().map(TagDto::new).toList(),
                filters.stream().map(FilterParser::parse).toList());
    }
}
