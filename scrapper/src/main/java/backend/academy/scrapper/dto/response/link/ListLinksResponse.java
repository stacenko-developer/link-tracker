package backend.academy.scrapper.dto.response.link;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ListLinksResponse(@NotNull List<LinkResponse> links, @Min(value = 0) int size) {}
