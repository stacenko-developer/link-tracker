package backend.academy.scrapper.cache.cleaner;

import java.util.List;

public interface CacheCleaner {

    void cleanLinkTrackingCache(Long chatId, List<String> tags);

    void cleanLinkTrackingCache(Long chatId);
}
