package backend.academy.scrapper.cache;

import static backend.academy.scrapper.constants.ConstValues.COMMA_DELIMITER;

import backend.academy.scrapper.dto.request.link.FindUserLinksRequest;

public class CacheKeyGenerator {
    private static final String KEY_FORMAT = "%s,%s";

    public static String generateKey(FindUserLinksRequest request) {
        if (request == null) {
            return null;
        }

        String chatIdPart = String.valueOf(request.chatId());
        String tagsPart = request.tagNames() != null ? String.join(COMMA_DELIMITER, request.tagNames()) : null;

        return String.format(KEY_FORMAT, chatIdPart, tagsPart);
    }
}
