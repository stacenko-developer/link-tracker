package backend.academy.scrapper.exception.chat;

import backend.academy.common.exception.BadRequestException;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import java.io.Serial;
import java.util.List;

public class NotificationModeNotSupportedException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 7325101832153989438L;

    public NotificationModeNotSupportedException(String notificationMode, List<String> availableNotificationModes) {
        super(
                ExceptionMessageValues.NOTIFICATION_MODE_NOT_SUPPORTED_EXCEPTION_MESSAGE,
                String.format(
                        ExceptionDescriptionValues.NOTIFICATION_MODE_NOT_SUPPORTED_EXCEPTION_DESCRIPTION
                                + "%n"
                                + ExceptionDescriptionValues.AVAILABLE_NOTIFICATION_MODES_DESCRIPTION,
                        notificationMode,
                        String.join("\n", availableNotificationModes)));
    }
}
