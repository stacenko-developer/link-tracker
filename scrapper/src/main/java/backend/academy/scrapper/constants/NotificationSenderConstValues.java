package backend.academy.scrapper.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationSenderConstValues {

    public static final String MESSAGE_TRANSPORT_PROPERTY = "app.message-transport";
    public static final String HTTP_MESSAGE_TRANSPORT = "HTTP";
    public static final String KAFKA_MESSAGE_TRANSPORT = "KAFKA";
}
