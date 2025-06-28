package backend.academy.bot.configuration;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.CommandProvider;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BotConfiguration.class)
public class TelegramBotConfiguration {

    @Bean
    public TelegramBot telegramBot(BotConfiguration botConfiguration, CommandProvider commandProvider) {
        TelegramBot telegramBot = new TelegramBot(botConfiguration.telegramToken());

        List<BotCommand> botCommands = commandProvider.getAllCommands().stream()
                .map(Command::toDescriptionBotCommand)
                .toList();

        telegramBot.execute(new SetMyCommands(botCommands.toArray(new BotCommand[0])));

        return telegramBot;
    }
}
