package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.NotificationModeDto;
import backend.academy.scrapper.enums.NotificationMode;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChatSettingsService {

    private static final List<NotificationModeDto> NOTIFICATION_MODES = Arrays.stream(NotificationMode.values())
            .map(notificationMode -> new NotificationModeDto(
                    notificationMode.name(), notificationMode.value(), notificationMode.description()))
            .toList();

    public List<NotificationModeDto> getNotificationModes() {
        return NOTIFICATION_MODES;
    }
}
