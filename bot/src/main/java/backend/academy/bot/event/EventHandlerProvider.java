package backend.academy.bot.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventHandlerProvider {

    private final List<EventHandler> eventHandlers;

    public EventHandler getEventHandler(String name) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
