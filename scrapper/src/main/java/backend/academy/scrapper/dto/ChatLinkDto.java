package backend.academy.scrapper.dto;

import backend.academy.scrapper.dto.response.link.LinkResponse;
import java.net.URI;
import java.util.List;

public record ChatLinkDto(Long id, URI url, List<TagDto> tags, List<FilterDto> filters) {

    private static final String FILTER_FORMAT = "%s:%s";

    public LinkResponse toLinkResponse() {
        return new LinkResponse(
                id,
                url,
                tags.stream().map(TagDto::name).toList(),
                filters.stream()
                        .map(filter -> String.format(FILTER_FORMAT, filter.key(), filter.value()))
                        .toList());
    }
}
