package backend.academy.bot.cache.cleaner;

import java.util.List;

public interface CacheCleaner {

    void cleanLinkTrackingCache(Long chatId, List<String> tags);

    void cleanLinkTrackingCache(Long chatId);
}
