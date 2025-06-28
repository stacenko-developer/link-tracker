package backend.academy.bot.event.stackoverflow;

import backend.academy.bot.configuration.updateLink.stackoverflow.StackoverflowCommentProperties;
import backend.academy.bot.event.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StackoverflowCommentEventHandler extends EventHandler {

    private final StackoverflowCommentProperties stackoverflowCommentProperties;

    @Override
    public String getMessageFormat() {
        return stackoverflowCommentProperties.message();
    }

    @Override
    public String getName() {
        return stackoverflowCommentProperties.name();
    }
}
