package backend.academy.bot.command;

import backend.academy.bot.client.scrapper.dto.response.LinkResponse;
import backend.academy.bot.configuration.command.UntrackCommandProperties;
import backend.academy.bot.constants.exception.ClientExceptionMessageValues;
import backend.academy.bot.dto.command.CommandRequestDto;
import backend.academy.bot.dto.command.CommandResponseDto;
import backend.academy.bot.service.client.ScrapperClientService;
import backend.academy.common.dto.ResponseDto;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UntrackCommand extends Command {

    private static final int CORRECT_COMMAND_ARGUMENTS_COUNT = 2;
    private static final int TRACKING_URL_INDEX = 1;

    private final ScrapperClientService scrapperServiceClient;
    private final UntrackCommandProperties untrackCommandProperties;

    @Override
    public String getName() {
        return untrackCommandProperties.name();
    }

    @Override
    public String getDescription() {
        return untrackCommandProperties.description();
    }

    @Override
    public String getUsageInformation() {
        return untrackCommandProperties.usageInformation();
    }

    @Override
    public CommandResponseDto process(CommandRequestDto commandRequestDto) {
        if (commandRequestDto.getArguments().length != CORRECT_COMMAND_ARGUMENTS_COUNT) {
            return getResponse(
                    new SendMessage(commandRequestDto.chatId(), untrackCommandProperties.incorrectCommandFormat()));
        }

        String url = commandRequestDto.getArguments()[TRACKING_URL_INDEX];

        ResponseDto<LinkResponse> responseDto =
                scrapperServiceClient.deleteLink(commandRequestDto.chatId(), URI.create(url));

        if (responseDto.apiErrorResponse() != null) {
            return processErrorResponse(commandRequestDto.chatId(), responseDto.apiErrorResponse());
        }

        return getResponse(
                new SendMessage(commandRequestDto.chatId(), String.format(untrackCommandProperties.success(), url)));
    }

    @PostConstruct
    private void initErrorMessages() {
        errorsWithDefaultMessages.put(
                ClientExceptionMessageValues.CHAT_NOT_FOUND_EXCEPTION_MESSAGE,
                untrackCommandProperties.unregisteredAccount());
        errorsWithDefaultMessages.put(
                ClientExceptionMessageValues.LINK_NOT_FOUND_EXCEPTION_MESSAGE, untrackCommandProperties.notFoundLink());
    }
}
