package backend.academy.bot.dto.command;

import static backend.academy.bot.constants.ConstValues.SPACE_DELIMITER;

import backend.academy.bot.dto.UserStateDto;

public record CommandRequestDto(long chatId, String userInput, UserStateDto userStateDto) {

    public String[] getArguments() {
        return userInput.split(SPACE_DELIMITER);
    }
}
