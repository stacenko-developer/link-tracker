package backend.academy.bot.cache;

import static backend.academy.bot.constants.ConstValues.COMMA_DELIMITER;

import java.util.List;

public class CacheKeyGenerator {
    private static final String KEY_FORMAT = "%s,%s";

    public static String generateKey(Long tgChatId, List<String> tagNames) {
        String tagsPart = tagNames != null && !tagNames.isEmpty()
                ? String.join(
                        COMMA_DELIMITER, tagNames.stream().sorted().distinct().toList())
                : null;

        return String.format(KEY_FORMAT, tgChatId, tagsPart);
    }
}
