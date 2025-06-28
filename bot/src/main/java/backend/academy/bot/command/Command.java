package backend.academy.bot.command;

import static backend.academy.bot.constants.ConstValues.REPLY_KEYBOARD_REMOVE;

import backend.academy.bot.dao.state.entity.State;
import backend.academy.bot.dto.command.CommandRequestDto;
import backend.academy.bot.dto.command.CommandResponseDto;
import backend.academy.common.dto.ApiErrorResponse;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public abstract class Command {

    protected final Map<String, String> errorsWithDefaultMessages = new HashMap<>();
    protected final List<String> whiteErrors = new ArrayList<>();

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getUsageInformation();

    public abstract CommandResponseDto process(CommandRequestDto commandRequestDto);

    public BotCommand toDescriptionBotCommand() {
        return new BotCommand(getName(), getDescription());
    }

    public List<State> getAcceptableStates() {
        return Collections.emptyList();
    }

    protected CommandResponseDto processErrorResponse(long chatId, ApiErrorResponse apiErrorResponse) {
        String errorMessage = errorsWithDefaultMessages.get(apiErrorResponse.exceptionMessage());

        if (StringUtils.isNotBlank(errorMessage)) {
            return getResponse(new SendMessage(chatId, errorMessage).replyMarkup(REPLY_KEYBOARD_REMOVE));
        }

        if (whiteErrors.contains(apiErrorResponse.exceptionMessage())) {
            return getResponse(
                    new SendMessage(chatId, apiErrorResponse.description()).replyMarkup(REPLY_KEYBOARD_REMOVE));
        }

        return null;
    }

    protected CommandResponseDto getResponse(SendMessage sendMessage) {
        return new CommandResponseDto(sendMessage, null);
    }
}
