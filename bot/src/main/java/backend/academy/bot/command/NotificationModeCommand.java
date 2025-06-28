package backend.academy.bot.command;

import static backend.academy.bot.constants.ConstValues.REPLY_KEYBOARD_REMOVE;

import backend.academy.bot.client.scrapper.dto.NotificationModeDto;
import backend.academy.bot.client.scrapper.dto.request.ChatSettingsRequest;
import backend.academy.bot.client.scrapper.dto.response.ChatResponse;
import backend.academy.bot.configuration.command.NotificationModeProperties;
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
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationModeCommand extends Command {

    private static final String NOTIFICATION_MODE_MESSAGE_FORMAT = "%s: %s%n";
    private static final List<State> ACCEPTABLE_STATES = List.of(State.WAITING_FOR_NOTIFICATION_MODE);

    private final StateMachineService stateMachineService;
    private final ScrapperClientService scrapperServiceClient;
    private final NotificationModeProperties notificationModeProperties;

    @Override
    public String getName() {
        return notificationModeProperties.name();
    }

    @Override
    public String getDescription() {
        return notificationModeProperties.description();
    }

    @Override
    public String getUsageInformation() {
        return notificationModeProperties.usageInformation();
    }

    @Override
    public CommandResponseDto process(CommandRequestDto commandRequestDto) {
        if (commandRequestDto.userStateDto() == null) {
            return processInputNotificationModeCommand(commandRequestDto);
        }

        return processInputNotificationModeValue(commandRequestDto);
    }

    private CommandResponseDto processInputNotificationModeCommand(CommandRequestDto commandRequestDto) {
        ResponseDto<List<NotificationModeDto>> notificationsModesResponse =
                scrapperServiceClient.getNotificationModes();

        if (notificationsModesResponse.apiErrorResponse() != null) {
            return processErrorResponse(commandRequestDto.chatId(), notificationsModesResponse.apiErrorResponse());
        }

        ResponseDto<ChatResponse> chatInformationResponse =
                scrapperServiceClient.getChatById(commandRequestDto.chatId());

        if (chatInformationResponse.apiErrorResponse() != null) {
            return processErrorResponse(commandRequestDto.chatId(), chatInformationResponse.apiErrorResponse());
        }

        List<NotificationModeDto> notificationModes = notificationsModesResponse.content();
        String notificationModesString = getAvailableNotificationModesString(notificationModes);

        String resultMessage = String.format(
                notificationModeProperties.inputNotificationMode(),
                notificationModesString,
                chatInformationResponse.content().notificationModeDto().title());

        UserStateDto userStateDto = new UserStateDto();
        stateMachineService.transitionUserState(userStateDto, State.WAITING_FOR_NOTIFICATION_MODE);

        return new CommandResponseDto(
                new SendMessage(commandRequestDto.chatId(), resultMessage)
                        .replyMarkup(getNotificationModesKeyboard(notificationModes)),
                userStateDto);
    }

    private CommandResponseDto processInputNotificationModeValue(CommandRequestDto commandRequestDto) {
        long chatId = commandRequestDto.chatId();
        String notificationTitle = commandRequestDto.userInput();

        ResponseDto<List<NotificationModeDto>> notificationsModesResponse =
                scrapperServiceClient.getNotificationModes();

        if (notificationsModesResponse.apiErrorResponse() != null) {
            return processErrorResponse(chatId, notificationsModesResponse.apiErrorResponse());
        }

        NotificationModeDto notificationModeDto =
                findNotificationModeByTitle(notificationsModesResponse.content(), notificationTitle);

        if (notificationModeDto == null) {
            return getResponse(new SendMessage(chatId, notificationModeProperties.modeNotFound())
                    .replyMarkup(REPLY_KEYBOARD_REMOVE));
        }

        ResponseDto<ChatResponse> responseDto =
                scrapperServiceClient.updateChatSettings(chatId, new ChatSettingsRequest(notificationModeDto.code()));

        if (responseDto.apiErrorResponse() != null) {
            return processErrorResponse(chatId, responseDto.apiErrorResponse());
        }

        String successMessage = String.format(
                notificationModeProperties.success(),
                responseDto.content().notificationModeDto().title());

        return getResponse(new SendMessage(chatId, successMessage).replyMarkup(REPLY_KEYBOARD_REMOVE));
    }

    private NotificationModeDto findNotificationModeByTitle(
            List<NotificationModeDto> notificationModes, String notificationModeTitle) {
        return notificationModes.stream()
                .filter(nm -> nm.title().equalsIgnoreCase(notificationModeTitle))
                .findFirst()
                .orElse(null);
    }

    private ReplyKeyboardMarkup getNotificationModesKeyboard(List<NotificationModeDto> notificationModes) {
        List<KeyboardButton> keyboardButtons = new ArrayList<>(notificationModes.size());

        for (NotificationModeDto notificationMode : notificationModes) {
            keyboardButtons.add(new KeyboardButton(notificationMode.title()));
        }

        return new ReplyKeyboardMarkup(keyboardButtons.toArray(new KeyboardButton[0]))
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);
    }

    private String getAvailableNotificationModesString(List<NotificationModeDto> notificationModes) {
        StringBuilder message = new StringBuilder();

        for (NotificationModeDto notificationMode : notificationModes) {
            message.append(String.format(
                    NOTIFICATION_MODE_MESSAGE_FORMAT, notificationMode.title(), notificationMode.description()));
        }

        return message.toString();
    }

    @Override
    public List<State> getAcceptableStates() {
        return ACCEPTABLE_STATES;
    }

    @PostConstruct
    private void initErrorsWithDefaultMessages() {
        errorsWithDefaultMessages.put(
                ClientExceptionMessageValues.CHAT_NOT_FOUND_EXCEPTION_MESSAGE,
                notificationModeProperties.unregisteredAccount());
    }
}
