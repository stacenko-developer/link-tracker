package backend.academy.scrapper.linkTracker.dto;

public record EventDto(String type, String title, String user, Long createdAt, Long updatedAt, String text) {}
