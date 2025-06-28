package backend.academy.scrapper.client.stackoverflow;

import static backend.academy.scrapper.constants.APIConstValues.STACKOVERFLOW_ANSWERS_URL;
import static backend.academy.scrapper.constants.APIConstValues.STACKOVERFLOW_COMMENTS_URL;
import static backend.academy.scrapper.constants.APIConstValues.STACKOVERFLOW_QUESTION_INFORMATION_URL;

import backend.academy.scrapper.client.stackoverflow.dto.StackoverflowResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackoverflowClient {

    @GetExchange(STACKOVERFLOW_ANSWERS_URL)
    ResponseEntity<StackoverflowResponseDto> getAnswers(
            @PathVariable Long questionId, @PathVariable String key, @PathVariable String accessToken);

    @GetExchange(STACKOVERFLOW_COMMENTS_URL)
    ResponseEntity<StackoverflowResponseDto> getComments(
            @PathVariable Long questionId, @PathVariable String key, @PathVariable String accessToken);

    @GetExchange(STACKOVERFLOW_QUESTION_INFORMATION_URL)
    ResponseEntity<StackoverflowResponseDto> getQuestionInformation(
            @PathVariable Long questionId, @PathVariable String key, @PathVariable String accessToken);
}
