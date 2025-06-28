package backend.academy.scrapper.linkTracker;

import backend.academy.scrapper.client.stackoverflow.dto.ItemDto;
import backend.academy.scrapper.client.stackoverflow.dto.StackoverflowResponseDto;
import backend.academy.scrapper.exception.link.LinkNotSupportedException;
import backend.academy.scrapper.linkTracker.dto.EventDto;
import backend.academy.scrapper.service.client.StackoverflowClientService;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StackoverflowLinkTracker extends LinkTracker {

    private static final Pattern URL_PATTERN = Pattern.compile("https://stackoverflow.com/questions/([0-9]+)");
    private static final String LINK_TYPE = "https://stackoverflow.com/questions/{id}";

    private static final String ANSWER_EVENT_TYPE = "Answer";
    private static final String COMMENT_EVENT_TYPE = "Comment";

    private static final long SECOND_TO_MILLISECONDS_RATIO = 1000;

    private final StackoverflowClientService stackoverflowServiceClient;

    @Override
    public Pattern getUrlPattern() {
        return URL_PATTERN;
    }

    @Override
    public String getLinkType() {
        return LINK_TYPE;
    }

    @Override
    public List<EventDto> track(URI url) {
        if (!isSupports(url)) {
            throw new LinkNotSupportedException(url);
        }

        Matcher matcher = URL_PATTERN.matcher(url.toString());

        if (!matcher.matches()) {
            return null;
        }

        Long questionId = Long.parseLong(matcher.group(1));

        String questionTitle = getQuestionTitle(
                stackoverflowServiceClient.getQuestionInformation(questionId).content());

        if (StringUtils.isBlank(questionTitle)) {
            return null;
        }

        StackoverflowResponseDto answers =
                stackoverflowServiceClient.getAnswers(questionId).content();

        if (answers == null) {
            return null;
        }

        StackoverflowResponseDto comments =
                stackoverflowServiceClient.getComments(questionId).content();

        if (comments == null) {
            return null;
        }

        List<EventDto> events = new ArrayList<>();

        events.addAll(getEvents(ANSWER_EVENT_TYPE, questionTitle, answers));
        events.addAll(getEvents(COMMENT_EVENT_TYPE, questionTitle, comments));

        return events;
    }

    private List<EventDto> getEvents(
            String type, String questionTitle, StackoverflowResponseDto stackoverflowResponseDto) {
        return stackoverflowResponseDto.items().stream()
                .filter(this::isValidItem)
                .map(item -> new EventDto(
                        type,
                        questionTitle,
                        item.owner().displayName(),
                        item.creationDate(),
                        item.lastActivityDate() != null
                                ? item.lastActivityDate() * SECOND_TO_MILLISECONDS_RATIO
                                : item.creationDate() * SECOND_TO_MILLISECONDS_RATIO,
                        item.body()))
                .toList();
    }

    private boolean isValidItem(ItemDto itemDto) {
        return itemDto != null
                && itemDto.creationDate() != null
                && itemDto.owner() != null
                && StringUtils.isNotBlank(itemDto.owner().displayName())
                && StringUtils.isNotBlank(itemDto.body());
    }

    private String getQuestionTitle(StackoverflowResponseDto questionInformation) {
        if (questionInformation == null || questionInformation.items().isEmpty()) {
            return null;
        }

        return questionInformation.items().getFirst().title();
    }
}
