package backend.academy.scrapper.linkTracker;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.client.stackoverflow.dto.ItemDto;
import backend.academy.scrapper.client.stackoverflow.dto.OwnerDto;
import backend.academy.scrapper.client.stackoverflow.dto.StackoverflowResponseDto;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import backend.academy.scrapper.exception.link.LinkNotSupportedException;
import backend.academy.scrapper.linkTracker.dto.EventDto;
import backend.academy.scrapper.service.client.StackoverflowClientService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StackoverflowLinkTrackerTest {

    @InjectMocks
    private StackoverflowLinkTracker stackoverflowLinkTracker;

    @Mock
    private StackoverflowClientService stackoverflowServiceClient;

    @ParameterizedTest
    @MethodSource("getArgumentsForTrackStackoverflowUrlWithCorrectArguments")
    public void trackStackoverflowUrlWithCorrectArguments_ShouldCorrectlyTracking(Long questionId) {
        URI expectedUrl = URI.create(String.format("https://stackoverflow.com/questions/%d", questionId));

        long expectedCreationDateMillis = 0L;
        long expectedLastActivityTimeMillis = 0L;
        String expectedUserLogin = "viktor";
        String expectedQuestionTitle = "Question title";
        String expectedQuestionBody = "Question body";

        String expectedAnswerTitle = "Answer title";
        String expectedAnswerBody = "Answer body";
        String expectedCommentTitle = "Comment title";
        String expectedCommentBody = "Comment body";

        List<ItemDto> questionInformation = List.of(new ItemDto(
                expectedCreationDateMillis,
                expectedLastActivityTimeMillis,
                new OwnerDto(expectedUserLogin),
                expectedQuestionTitle,
                expectedQuestionBody));

        List<ItemDto> answers = List.of(new ItemDto(
                expectedCreationDateMillis,
                expectedLastActivityTimeMillis,
                new OwnerDto(expectedUserLogin),
                expectedAnswerTitle,
                expectedAnswerBody));

        List<ItemDto> comments = List.of(new ItemDto(
                expectedCreationDateMillis,
                expectedLastActivityTimeMillis,
                new OwnerDto(expectedUserLogin),
                expectedCommentTitle,
                expectedCommentBody));

        when(stackoverflowServiceClient.getQuestionInformation(questionId))
                .thenReturn(new ResponseDto<>(new StackoverflowResponseDto(questionInformation), null));
        when(stackoverflowServiceClient.getAnswers(questionId))
                .thenReturn(new ResponseDto<>(new StackoverflowResponseDto(answers), null));
        when(stackoverflowServiceClient.getComments(questionId))
                .thenReturn(new ResponseDto<>(new StackoverflowResponseDto(comments), null));

        List<EventDto> expectedEvents = List.of(
                new EventDto(
                        "Answer",
                        expectedQuestionTitle,
                        expectedUserLogin,
                        expectedCreationDateMillis,
                        expectedLastActivityTimeMillis,
                        expectedAnswerBody),
                new EventDto(
                        "Comment",
                        expectedQuestionTitle,
                        expectedUserLogin,
                        expectedCreationDateMillis,
                        expectedLastActivityTimeMillis,
                        expectedCommentBody));
        List<EventDto> actualEvents = stackoverflowLinkTracker.track(expectedUrl);

        Assertions.assertEquals(expectedEvents, actualEvents);
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForTrackStackoverflowUrlWithIncorrectArguments")
    public void trackStackoverflowUrlWithIncorrectArguments_ShouldThrowLinkNotSupportedException(String url) {
        assertThatThrownBy(() -> {
                    stackoverflowLinkTracker.track(URI.create(url));
                })
                .isInstanceOf(LinkNotSupportedException.class)
                .satisfies(exception -> {
                    final LinkNotSupportedException ex = (LinkNotSupportedException) exception;

                    String expectedDescription =
                            String.format(ExceptionDescriptionValues.LINK_NOT_SUPPORTED_EXCEPTION_DESCRIPTION, url);
                    String actualDescription = ex.description();

                    Assertions.assertEquals(expectedDescription, actualDescription);
                })
                .hasMessageContaining(ExceptionMessageValues.LINK_NOT_SUPPORTED_EXCEPTION_MESSAGE);
    }

    private static List<Long> getArgumentsForTrackStackoverflowUrlWithCorrectArguments() {
        return List.of(1L, 124L, 5422546L, 123542L, 96322L, 358842L, 234773L, 45323L, 786543L, 25633326323L);
    }

    private static List<String> getArgumentsForTrackStackoverflowUrlWithIncorrectArguments() {
        return List.of(
                "https://stackoverflow.com/questions/123d42",
                "https://stackoverflow.com/questions/d1",
                "https://stackoverflow.com/questions/1s",
                "https://stackoverflow.com/questions/1t5",
                "https://stackoverflow.com/question/2",
                "https://stackoverflow.com/2",
                "https://stackoverflow.com/",
                "https://stackoverflow.com/3L",
                "http://stackoverflow.com/questions/3",
                "https://stackoverfow.com/questions/5");
    }
}
