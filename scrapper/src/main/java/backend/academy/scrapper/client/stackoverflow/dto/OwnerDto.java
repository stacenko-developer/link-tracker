package backend.academy.scrapper.client.stackoverflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OwnerDto(@JsonProperty("display_name") String displayName) {}
