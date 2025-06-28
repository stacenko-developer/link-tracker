package backend.academy.bot.command;

import backend.academy.bot.configuration.command.StartCommandProperties;
import backend.academy.bot.constants.exception.ClientExceptionMessageValues;
import backend.academy.bot.dto.command.CommandRequestDto;
import backend.academy.bot.dto.command.CommandResponseDto;
import backend.academy.bot.service.client.ScrapperClientService;
import backend.academy.common.dto.ResponseDto;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommand extends Command {

    private final ScrapperClientService scrapperServiceClient;
    private final StartCommandProperties startCommandProperties;

    @Override
    public String getName() {
        return startCommandProperties.name();
    }

    @Override
    public String getDescription() {
        return startCommandProperties.description();
    }

    @Override
    public String getUsageInformation() {
        return startCommandProperties.usageInformation();
    }

    @Override
    public CommandResponseDto process(CommandRequestDto commandRequestDto) {
        ResponseDto<Void> responseDto = scrapperServiceClient.registerChat(commandRequestDto.chatId());

        if (responseDto.apiErrorResponse() != null) {
            return processErrorResponse(commandRequestDto.chatId(), responseDto.apiErrorResponse());
        }

        return getResponse(new SendMessage(commandRequestDto.chatId(), startCommandProperties.success()));
    }

    @PostConstruct
    private void initErrorMessages() {
        errorsWithDefaultMessages.put(
                ClientExceptionMessageValues.REPEATED_REGISTRATION_EXCEPTION_MESSAGE,
                startCommandProperties.repeatedRegistration());
    }
}
