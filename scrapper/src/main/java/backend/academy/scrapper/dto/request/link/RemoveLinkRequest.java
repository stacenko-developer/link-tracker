package backend.academy.scrapper.dto.request.link;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record RemoveLinkRequest(@NotNull URI link) {}
