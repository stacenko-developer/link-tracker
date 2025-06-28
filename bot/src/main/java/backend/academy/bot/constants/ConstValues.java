package backend.academy.bot.constants;

import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstValues {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final String RATE_LIMIT_EXCEEDED_MESSAGE = "Rate limit exceeded";
    public static final ReplyKeyboardRemove REPLY_KEYBOARD_REMOVE = new ReplyKeyboardRemove(true);

    public static final String SPACE_DELIMITER = " ";
    public static final String COMMA_DELIMITER = ",";
}
