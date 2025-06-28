package backend.academy.common.utils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtils {

    public static Long toEpochMillis(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toInstant().toEpochMilli();
    }

    public static long getNowUtc() {
        return Instant.now(Clock.systemUTC()).toEpochMilli();
    }

    public static LocalDateTime toUtc(Long epochMillis) {
        if (epochMillis == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
    }
}
