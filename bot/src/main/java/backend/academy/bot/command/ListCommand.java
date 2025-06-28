package backend.academy.bot.command;

import backend.academy.bot.client.scrapper.dto.response.LinkResponse;
import backend.academy.bot.client.scrapper.dto.response.ListLinksResponse;
import backend.academy.bot.configuration.command.ListCommandProperties;
import backend.academy.bot.constants.exception.ClientExceptionMessageValues;
import backend.academy.bot.dto.command.CommandRequestDto;
import backend.academy.bot.dto.command.CommandResponseDto;
import backend.academy.bot.service.client.ScrapperClientService;
import backend.academy.common.dto.ResponseDto;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListCommand extends Command {

    private static final int BEGIN_TAGS_INDEX = 1;
    private static final int INPUT_WITH_TAGS_MIN_SIZE = BEGIN_TAGS_INDEX + 1;

    private final ScrapperClientService scrapperServiceClient;
    private final ListCommandProperties listCommandProperties;

    @Override
    public String getName() {
        return listCommandProperties.name();
    }

    @Override
    public String getDescription() {
        return listCommandProperties.description();
    }

    @Override
    public String getUsageInformation() {
        return listCommandProperties.usageInformation();
    }

    @Override
    public CommandResponseDto process(CommandRequestDto commandRequestDto) {
        List<String> tagNames = getTags(commandRequestDto.getArguments());

        ResponseDto<ListLinksResponse> responseDto =
                scrapperServiceClient.getAllUserTrackingLinks(commandRequestDto.chatId(), tagNames);

        if (responseDto.apiErrorResponse() != null) {
            return processErrorResponse(commandRequestDto.chatId(), responseDto.apiErrorResponse());
        }

        List<LinkResponse> links = responseDto.content().links();

        if (links == null || links.isEmpty()) {
            if (tagNames == null) {
                return getResponse(
                        new SendMessage(commandRequestDto.chatId(), listCommandProperties.trackingLinksNotFound()));
            } else {
                return getResponse(
                        new SendMessage(commandRequestDto.chatId(), listCommandProperties.linksByTagsNotFound()));
            }
        }

        return getResponse(new SendMessage(commandRequestDto.chatId(), getMessage(links)));
    }

    private String getMessage(List<LinkResponse> links) {
        StringBuilder message = new StringBuilder(listCommandProperties.header());

        for (LinkResponse link : links) {
            message.append(String.format(listCommandProperties.format(), link.url(), link.tags(), link.filters()));
        }

        return message.toString();
    }

    private List<String> getTags(String[] arguments) {
        return isInputWithoutTags(arguments)
                ? null
                : List.of(Arrays.copyOfRange(arguments, BEGIN_TAGS_INDEX, arguments.length));
    }

    private boolean isInputWithoutTags(String[] arguments) {
        return arguments.length < INPUT_WITH_TAGS_MIN_SIZE;
    }

    @PostConstruct
    private void initErrorsWithDefaultMessages() {
        errorsWithDefaultMessages.put(
                ClientExceptionMessageValues.CHAT_NOT_FOUND_EXCEPTION_MESSAGE,
                listCommandProperties.unregisteredAccount());
    }
}
