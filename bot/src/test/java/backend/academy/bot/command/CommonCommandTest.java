package backend.academy.bot.command;

import static backend.academy.bot.ConstValues.TEXT_KEY;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class CommonCommandTest {

    protected String getText(SendMessage sendMessage) {
        return (String) sendMessage.getParameters().get(TEXT_KEY);
    }
}
