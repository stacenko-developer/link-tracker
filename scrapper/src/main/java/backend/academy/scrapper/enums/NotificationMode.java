package backend.academy.scrapper.enums;

import backend.academy.scrapper.exception.chat.NotificationModeNotSupportedException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationMode {
    IMMEDIATE("Немедленно", "Уведомления приходят сразу при обнаружении изменений"),
    DAILY_DIGEST("Ежедневный дайджест", "Раз в день одним письмом");

    private final String value;
    private final String description;

    public static NotificationMode getNotificationModeByName(String name) {
        return Arrays.stream(values())
                .filter(notificationMode -> notificationMode.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new NotificationModeNotSupportedException(
                        name,
                        Arrays.stream(values()).map(NotificationMode::value).toList()));
    }
}
