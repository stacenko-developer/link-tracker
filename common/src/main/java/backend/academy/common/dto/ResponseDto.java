package backend.academy.common.dto;

public record ResponseDto<T>(T content, ApiErrorResponse apiErrorResponse) {}
