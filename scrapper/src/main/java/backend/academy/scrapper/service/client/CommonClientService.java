package backend.academy.scrapper.service.client;

import backend.academy.common.dto.ApiErrorResponse;
import backend.academy.common.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RequiredArgsConstructor
public abstract class CommonClientService {

    private static final String EXCEPTION_MESSAGE = "Exception occurred: ";

    private final ObjectMapper objectMapper;

    protected ApiErrorResponse getApiErrorResponse(Throwable t) {
        return new ApiErrorResponse(
                null,
                null,
                t.getClass().getSimpleName(),
                t.getMessage(),
                Arrays.stream(t.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList());
    }

    protected <T> ResponseDto<T> execute(Supplier<ResponseEntity<T>> request) {
        try {
            ResponseEntity<T> responseEntity = request.get();
            return new ResponseDto<>(responseEntity.getBody(), null);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                throw e;
            }

            try {
                ApiErrorResponse apiErrorResponse =
                        objectMapper.readValue(e.getResponseBodyAsString(), ApiErrorResponse.class);
                return isEmptyApiErrorResponse(apiErrorResponse)
                        ? new ResponseDto<>(null, getApiErrorResponse(e))
                        : new ResponseDto<>(null, apiErrorResponse);
            } catch (Exception ex) {
                log.error(EXCEPTION_MESSAGE, ex);
                return new ResponseDto<>(null, getApiErrorResponse(ex));
            }
        }
    }

    private boolean isEmptyApiErrorResponse(ApiErrorResponse apiErrorResponse) {
        return StringUtils.isBlank(apiErrorResponse.description())
                && StringUtils.isBlank(apiErrorResponse.code())
                && StringUtils.isBlank(apiErrorResponse.exceptionName())
                && StringUtils.isBlank(apiErrorResponse.exceptionMessage())
                && (apiErrorResponse.stackTrace() == null
                        || apiErrorResponse.stackTrace().isEmpty());
    }
}
