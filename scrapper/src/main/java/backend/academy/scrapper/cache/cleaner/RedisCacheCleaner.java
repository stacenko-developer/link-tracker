package backend.academy.scrapper.cache.cleaner;

import static backend.academy.scrapper.constants.CacheConstValues.CACHE_KEY_PATTERN;
import static backend.academy.scrapper.constants.ConstValues.COMMA_DELIMITER;

import backend.academy.scrapper.configuration.ScrapperConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheCleaner implements CacheCleaner {
    private static final String MISSED_TAGS_VALUE = "null";

    private static final String DOUBLE_COLON_VALUE = "::";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ScrapperConfig scrapperConfig;

    @Override
    public void cleanLinkTrackingCache(Long chatId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            cleanLinkTrackingCache(chatId);
            return;
        }

        cleanWithTagsFilter(chatId, new HashSet<>(tags), scrapperConfig.cache().botTrackingLinks());
        cleanWithTagsFilter(chatId, new HashSet<>(tags), scrapperConfig.cache().scrapperTrackingLinks());
    }

    @Override
    public void cleanLinkTrackingCache(Long chatId) {
        cleanByPattern(chatId, scrapperConfig.cache().botTrackingLinks());
        cleanByPattern(chatId, scrapperConfig.cache().scrapperTrackingLinks());
    }

    private void cleanByPattern(Long chatId, String cacheName) {
        Set<String> keys = findKeys(chatId, cacheName);
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    private void cleanWithTagsFilter(Long chatId, Set<String> tags, String cacheName) {
        Set<String> keys = findKeys(chatId, cacheName);
        if (keys.isEmpty()) {
            return;
        }

        Set<String> keysToDelete =
                keys.stream().filter(key -> shouldDeleteKey(key, tags)).collect(Collectors.toSet());

        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
    }

    private Set<String> findKeys(Long chatId, String cacheName) {
        String pattern = String.format(CACHE_KEY_PATTERN, cacheName, chatId);
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null ? keys : Collections.emptySet();
    }

    private boolean shouldDeleteKey(String key, Set<String> tags) {
        int doubleColonIndex = key.indexOf(DOUBLE_COLON_VALUE);
        int chatIdEndIndex = key.indexOf(COMMA_DELIMITER, doubleColonIndex);
        int tagsBeginIndex = chatIdEndIndex + 1;

        String tagsPart = key.substring(tagsBeginIndex);

        if (MISSED_TAGS_VALUE.equals(tagsPart)) {
            return true;
        }

        return Arrays.stream(tagsPart.split(COMMA_DELIMITER)).anyMatch(tags::contains);
    }
}
