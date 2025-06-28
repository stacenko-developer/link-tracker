package backend.academy.bot.event.stackoverflow;

import backend.academy.bot.configuration.updateLink.stackoverflow.StackoverflowAnswerProperties;
import backend.academy.bot.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StackoverflowAnswerEventHandler extends EventHandler {

    private final StackoverflowAnswerProperties stackoverflowAnswerProperties;

    @Override
    public String getMessageFormat() {
        return stackoverflowAnswerProperties.message();
    }

    @Override
    public String getName() {
        return stackoverflowAnswerProperties.name();
    }
}
