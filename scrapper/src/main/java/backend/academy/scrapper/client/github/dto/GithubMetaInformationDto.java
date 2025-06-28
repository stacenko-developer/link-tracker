package backend.academy.scrapper.client.github.dto;

import java.time.OffsetDateTime;

public record GithubMetaInformationDto(String title, String body, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
