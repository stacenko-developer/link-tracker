package backend.academy.bot.command;

import backend.academy.bot.configuration.command.HelpCommandProperties;
import backend.academy.bot.dto.command.CommandRequestDto;
import backend.academy.bot.dto.command.CommandResponseDto;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelpCommand extends Command {

    private static final String HEADER_MESSAGE_FORMAT = "%s%n";
    private static final String MESSAGE_FORMAT = "%s - %s%n%n%s%n%n";

    private final HelpCommandProperties helpCommandProperties;
    private final List<Command> commands;

    @Override
    public String getName() {
        return helpCommandProperties.name();
    }

    @Override
    public String getDescription() {
        return helpCommandProperties.description();
    }

    @Override
    public String getUsageInformation() {
        return helpCommandProperties.usageInformation();
    }

    @Override
    public CommandResponseDto process(CommandRequestDto commandRequestDto) {
        StringBuilder message = new StringBuilder(String.format(HEADER_MESSAGE_FORMAT, helpCommandProperties.header()));

        commands.forEach(command -> message.append(getFormattedMessage(command)));
        message.append(getFormattedMessage(this));

        return getResponse(new SendMessage(commandRequestDto.chatId(), message.toString()));
    }

    private String getFormattedMessage(Command command) {
        return String.format(
                MESSAGE_FORMAT, command.getName(), command.getDescription(), command.getUsageInformation());
    }
}
