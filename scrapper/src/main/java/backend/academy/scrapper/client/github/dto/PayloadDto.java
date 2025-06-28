package backend.academy.scrapper.client.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PayloadDto(
        GithubMetaInformationDto issue, @JsonProperty("pull_request") GithubMetaInformationDto pullRequest) {}
