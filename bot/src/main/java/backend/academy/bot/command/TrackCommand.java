package backend.academy.bot.command;

import backend.academy.bot.client.scrapper.dto.request.AddLinkRequest;
import backend.academy.bot.client.scrapper.dto.response.LinkResponse;
import backend.academy.bot.configuration.command.TrackCommandProperties;
import backend.academy.bot.constants.exception.ClientExceptionMessageValues;
import backend.academy.bot.dao.state.entity.State;
import backend.academy.bot.dto.UserStateDto;
import backend.academy.bot.dto.command.CommandRequestDto;
import backend.academy.bot.dto.command.CommandResponseDto;
import backend.academy.bot.service.StateMachineService;
import backend.academy.bot.service.client.ScrapperClientService;
import backend.academy.common.dto.ResponseDto;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackCommand extends Command {

    private static final int CORRECT_COMMAND_ARGUMENTS_COUNT = 2;
    private static final int TRACKING_URL_INDEX = 1;

    private static final String HTTP_URL_TYPE = "http://";
    private static final String HTTPS_URL_TYPE = "https://";

    private static final List<State> ACCEPTABLE_STATES = List.of(State.WAITING_FOR_TAGS, State.WAITING_FOR_FILTERS);

    private final StateMachineService stateMachineService;
    private final ScrapperClientService scrapperServiceClient;
    private final TrackCommandProperties trackCommandProperties;

    @Override
    public String getName() {
        return trackCommandProperties.name();
    }

    @Override
    public String getDescription() {
        return trackCommandProperties.description();
    }

    @Override
    public String getUsageInformation() {
        return trackCommandProperties.usageInformation();
    }

    @Override
    public CommandResponseDto process(CommandRequestDto commandRequestDto) {
        if (commandRequestDto.userStateDto() == null) {
            return processInputLink(commandRequestDto);
        } else if (commandRequestDto.userStateDto().currentState().equals(State.WAITING_FOR_TAGS)) {
            return processInputTags(commandRequestDto);
        }

        return processInputFilters(commandRequestDto);
    }

    private CommandResponseDto processInputLink(CommandRequestDto commandRequestDto) {
        if (commandRequestDto.getArguments().length != CORRECT_COMMAND_ARGUMENTS_COUNT) {
            return getResponse(
                    new SendMessage(commandRequestDto.chatId(), trackCommandProperties.incorrectCommandFormat()));
        }

        String url = commandRequestDto.getArguments()[TRACKING_URL_INDEX];

        if (!isValidUrl(url)) {
            return getResponse(
                    new SendMessage(commandRequestDto.chatId(), trackCommandProperties.incorrectUrlFormat()));
        }

        UserStateDto userStateDto = new UserStateDto();

        userStateDto.link(url);

        stateMachineService.transitionUserState(userStateDto, State.WAITING_FOR_TAGS);

        return new CommandResponseDto(
                new SendMessage(commandRequestDto.chatId(), String.format(trackCommandProperties.inputTags(), url))
                        .replyMarkup(skipKeyboard()),
                userStateDto);
    }

    private CommandResponseDto processInputTags(CommandRequestDto commandRequestDto) {
        UserStateDto userStateDto = commandRequestDto.userStateDto();

        if (!commandRequestDto.getArguments()[0].equals(trackCommandProperties.skipValue())) {
            userStateDto.tags(Arrays.stream(commandRequestDto.getArguments()).toList());
        }

        stateMachineService.transitionUserState(userStateDto, State.WAITING_FOR_FILTERS);

        return new CommandResponseDto(
                new SendMessage(commandRequestDto.chatId(), trackCommandProperties.inputFilters())
                        .replyMarkup(skipKeyboard()),
                userStateDto);
    }

    private CommandResponseDto processInputFilters(CommandRequestDto commandRequestDto) {
        UserStateDto userStateDto = commandRequestDto.userStateDto();

        if (!commandRequestDto.getArguments()[0].equals(trackCommandProperties.skipValue())) {
            userStateDto.filters(Arrays.stream(commandRequestDto.getArguments()).toList());
        }

        AddLinkRequest addLinkRequest =
                new AddLinkRequest(URI.create(userStateDto.link()), userStateDto.tags(), userStateDto.filters());
        ResponseDto<LinkResponse> responseDto =
                scrapperServiceClient.addLink(commandRequestDto.chatId(), addLinkRequest);

        if (responseDto.apiErrorResponse() != null) {
            return processErrorResponse(commandRequestDto.chatId(), responseDto.apiErrorResponse());
        }

        return getResponse(new SendMessage(commandRequestDto.chatId(), trackCommandProperties.success())
                .replyMarkup(new ReplyKeyboardRemove(true)));
    }

    private boolean isValidUrl(String url) {
        return url.startsWith(HTTP_URL_TYPE) || url.startsWith(HTTPS_URL_TYPE);
    }

    private ReplyKeyboardMarkup skipKeyboard() {
        return new ReplyKeyboardMarkup(new KeyboardButton(trackCommandProperties.skipValue()))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
    }

    @Override
    public List<State> getAcceptableStates() {
        return ACCEPTABLE_STATES;
    }

    @PostConstruct
    private void initWhiteListErrors() {
        whiteErrors.add(ClientExceptionMessageValues.FILTER_NOT_SUPPORTED_EXCEPTION_MESSAGE);
        whiteErrors.add(ClientExceptionMessageValues.INCORRECT_FILTER_FORMAT_EXCEPTION_MESSAGE);
        whiteErrors.add(ClientExceptionMessageValues.LINK_NOT_SUPPORTED_EXCEPTION_MESSAGE);
    }

    @PostConstruct
    private void initErrorsWithDefaultMessages() {
        errorsWithDefaultMessages.put(
                ClientExceptionMessageValues.CHAT_NOT_FOUND_EXCEPTION_MESSAGE,
                trackCommandProperties.unregisteredAccount());
        errorsWithDefaultMessages.put(
                ClientExceptionMessageValues.LINK_HAS_ALREADY_ADDED_EXCEPTION_MESSAGE,
                trackCommandProperties.repeatedAddingLink());
    }
}
