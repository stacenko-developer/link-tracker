package backend.academy.scrapper.client.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GithubEventDto(
        PayloadDto payload, @JsonProperty("created_at") OffsetDateTime createdAt, String type, ActorDto actor) {}
