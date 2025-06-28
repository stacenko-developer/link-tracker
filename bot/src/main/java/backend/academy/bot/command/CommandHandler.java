package backend.academy.bot.command;

import static backend.academy.bot.constants.ConstValues.REPLY_KEYBOARD_REMOVE;
import static backend.academy.bot.constants.ConstValues.SPACE_DELIMITER;

import backend.academy.bot.configuration.command.ErrorMessageProperties;
import backend.academy.bot.dto.UserStateDto;
import backend.academy.bot.dto.command.CommandRequestDto;
import backend.academy.bot.dto.command.CommandResponseDto;
import backend.academy.bot.service.UserStateService;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private static final String WHITESPACE_REGEX = "\\s+";

    private final CommandProvider commandProvider;
    private final ErrorMessageProperties errorMessageProperties;
    private final UserStateService userStateService;

    public SendMessage handle(long chatId, String userInput) {
        String trimUserInput = userInput.trim().replaceAll(WHITESPACE_REGEX, SPACE_DELIMITER);
        Command command = commandProvider.getCommandByUserInput(trimUserInput);
        UserStateDto userStateDto = userStateService.findUserStateByUserId(chatId);

        if (command != null && userStateDto != null) {
            userStateService.deleteUserState(chatId);
            userStateDto = null;
        }

        if (command == null && userStateDto != null) {
            command = commandProvider.getCommandByState(userStateDto.currentState());
        }

        if (command == null) {
            return new SendMessage(chatId, errorMessageProperties.unknownCommand());
        }

        CommandRequestDto commandRequestDto = new CommandRequestDto(chatId, trimUserInput, userStateDto);

        return processCommand(commandRequestDto, command);
    }

    private SendMessage processCommand(CommandRequestDto commandRequestDto, Command command) {
        try {
            CommandResponseDto commandResponseDto = command.process(commandRequestDto);

            if (commandResponseDto == null || commandResponseDto.sendMessage() == null) {
                userStateService.deleteUserState(commandRequestDto.chatId());

                return new SendMessage(commandRequestDto.chatId(), errorMessageProperties.serverError())
                        .replyMarkup(REPLY_KEYBOARD_REMOVE);
            }

            handleUserState(commandRequestDto, commandResponseDto);

            return commandResponseDto.sendMessage();
        } catch (Exception ex) {
            userStateService.deleteUserState(commandRequestDto.chatId());
            return new SendMessage(commandRequestDto.chatId(), errorMessageProperties.serverError())
                    .replyMarkup(REPLY_KEYBOARD_REMOVE);
        }
    }

    private void handleUserState(CommandRequestDto commandRequestDto, CommandResponseDto commandResponseDto) {
        if (commandResponseDto.userStateDto() != null) {
            userStateService.upgradeUserState(commandRequestDto.chatId(), commandResponseDto.userStateDto());
        } else {
            userStateService.deleteUserState(commandRequestDto.chatId());
        }
    }
}
