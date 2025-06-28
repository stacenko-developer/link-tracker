package backend.academy.bot.exception;

import static backend.academy.common.constants.ExceptionTextValues.INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION;

import backend.academy.common.dto.ApiErrorResponse;
import backend.academy.common.exception.LinkTrackerException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@Component
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String EXCEPTION_MESSAGE_FORMAT = "Exception occurred: ";

    @ExceptionHandler(LinkTrackerException.class)
    public ResponseEntity<?> handleLinkTrackerException(LinkTrackerException ex) {
        log.error(EXCEPTION_MESSAGE_FORMAT, ex);

        return new ResponseEntity<>(
                new ApiErrorResponse(
                        ex.description(),
                        String.valueOf(ex.getHttpStatus()),
                        ex.getClass().getSimpleName(),
                        ex.getMessage(),
                        Arrays.stream(ex.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()),
                ex.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NotNull MethodArgumentNotValidException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        return handleIncorrectArgumentsInRequest(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            @NotNull TypeMismatchException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        return handleIncorrectArgumentsInRequest(ex, status);
    }

    private ResponseEntity<Object> handleIncorrectArgumentsInRequest(Exception ex, HttpStatusCode status) {
        return new ResponseEntity<>(
                new ApiErrorResponse(
                        INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION,
                        String.valueOf(status.value()),
                        ex.getClass().getSimpleName(),
                        ex.getMessage(),
                        Arrays.stream(ex.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()),
                status);
    }
}
