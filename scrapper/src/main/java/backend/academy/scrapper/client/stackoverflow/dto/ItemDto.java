package backend.academy.scrapper.client.stackoverflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemDto(
        @JsonProperty("creation_date") Long creationDate,
        @JsonProperty("last_activity_date") Long lastActivityDate,
        OwnerDto owner,
        String title,
        String body) {}
