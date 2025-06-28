package backend.academy.bot.command;

import static backend.academy.bot.ConstValues.DEFAULT_TG_CHAT_ID;
import static org.mockito.Mockito.when;

import backend.academy.bot.configuration.command.ErrorMessageProperties;
import backend.academy.bot.dao.state.entity.State;
import backend.academy.bot.dto.UserStateDto;
import backend.academy.bot.dto.command.CommandRequestDto;
import backend.academy.bot.dto.command.CommandResponseDto;
import backend.academy.bot.service.UserStateService;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class CommandHandlerTest extends CommonCommandTest {

    private static final String DEFAULT_UNKNOWN_COMMAND = "unknown command";
    private static final String DEFAULT_UNKNOWN_COMMAND_MESSAGE = "unknown command message";

    private static final String DEFAULT_SUCCESS_MESSAGE = "success";

    @InjectMocks
    private CommandHandler commandHandler;

    @Mock
    private ErrorMessageProperties errorMessageProperties;

    @Mock
    private UserStateService userStateService;

    @Mock
    private TrackCommand trackCommand;

    @Mock
    private CommandProvider commandProvider;

    @Test
    public void handleUnknownCommand_ShouldReturnUnknownCommandMessage() {
        when(commandProvider.getCommandByUserInput(DEFAULT_UNKNOWN_COMMAND)).thenReturn(null);
        when(userStateService.findUserStateByUserId(DEFAULT_TG_CHAT_ID)).thenReturn(null);
        when(errorMessageProperties.unknownCommand()).thenReturn(DEFAULT_UNKNOWN_COMMAND_MESSAGE);

        SendMessage sendMessage = commandHandler.handle(DEFAULT_TG_CHAT_ID, DEFAULT_UNKNOWN_COMMAND);
        String actualMessage = getText(sendMessage);

        Assertions.assertEquals(DEFAULT_UNKNOWN_COMMAND_MESSAGE, actualMessage);
    }

    @Test
    public void handleUnknownCommandWithNotNullState_ShouldNotReturnUnknownCommandMessage() {
        UserStateDto userStateDto = new UserStateDto();
        userStateDto.currentState(State.WAITING_FOR_TAGS);

        when(commandProvider.getCommandByUserInput(DEFAULT_UNKNOWN_COMMAND)).thenReturn(null);
        when(userStateService.findUserStateByUserId(DEFAULT_TG_CHAT_ID)).thenReturn(userStateDto);
        when(commandProvider.getCommandByState(State.WAITING_FOR_TAGS)).thenReturn(trackCommand);

        when(trackCommand.process(new CommandRequestDto(DEFAULT_TG_CHAT_ID, DEFAULT_UNKNOWN_COMMAND, userStateDto)))
                .thenReturn(new CommandResponseDto(
                        new SendMessage(DEFAULT_TG_CHAT_ID, DEFAULT_SUCCESS_MESSAGE), userStateDto));

        SendMessage sendMessage = commandHandler.handle(DEFAULT_TG_CHAT_ID, DEFAULT_UNKNOWN_COMMAND);
        String actualMessage = getText(sendMessage);

        Assertions.assertNotEquals(DEFAULT_UNKNOWN_COMMAND_MESSAGE, actualMessage);
    }
}
