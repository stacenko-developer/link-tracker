package backend.academy.bot.command;

import static backend.academy.bot.constants.ConstValues.SPACE_DELIMITER;

import backend.academy.bot.dao.state.entity.State;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandProvider {

    private final List<Command> commands;

    public Command getCommandByUserInput(String userInput) {
        return commands.stream()
                .filter(command -> command.getName().equalsIgnoreCase(getCommandName(userInput)))
                .findFirst()
                .orElse(null);
    }

    public Command getCommandByState(State state) {
        return commands.stream()
                .filter(command -> command.getAcceptableStates().contains(state))
                .findFirst()
                .orElse(null);
    }

    public List<Command> getAllCommands() {
        return Collections.unmodifiableList(commands);
    }

    private String getCommandName(String userInput) {
        return userInput.trim().split(SPACE_DELIMITER)[0];
    }
}
