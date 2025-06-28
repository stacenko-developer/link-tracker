package backend.academy.scrapper.dto.response.link;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkResponse(
        @NotNull Long id, @NotNull URI url, @NotNull List<String> tags, @NotNull List<String> filters) {}
