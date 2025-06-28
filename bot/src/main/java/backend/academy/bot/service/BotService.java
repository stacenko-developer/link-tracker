package backend.academy.bot.service;

import backend.academy.bot.command.CommandHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import io.micrometer.core.instrument.Counter;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotService {

    private static final ParseMode DEFAULT_PARSE_MODE = ParseMode.Markdown;

    private final TelegramBot bot;
    private final CommandHandler commandHandler;

    private final Counter userMessagesCounter;

    @PostConstruct
    public void initBotCommands() {
        bot.setUpdatesListener(this::processUpdates);
    }

    public void sendMessage(long chatId, String message) {
        sendMessage(new SendMessage(chatId, message));
    }

    public void sendMessage(SendMessage sendMessage) {
        bot.execute(sendMessage.parseMode(DEFAULT_PARSE_MODE));
    }

    public int processUpdates(List<Update> updates) {
        updates.forEach(this::processSingleUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processSingleUpdate(Update update) {
        if (update.message() != null) {
            processUserInput(update.message().chat().id(), update.message().text());
        } else if (update.callbackQuery() != null) {
            processUserInput(
                    update.callbackQuery().from().id(), update.callbackQuery().data());
        }

        userMessagesCounter.increment();
    }

    private void processUserInput(long chatId, String input) {
        SendMessage response = commandHandler.handle(chatId, input);
        sendMessage(response);
    }
}
