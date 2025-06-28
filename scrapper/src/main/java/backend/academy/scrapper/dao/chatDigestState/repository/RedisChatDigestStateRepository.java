package backend.academy.scrapper.dao.chatDigestState.repository;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.LinkInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatDigestStateRepository {

    private static final String ONLY_DIGIT_WILDCARD_PATTERN = "[0-9]*";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void addNotificationToState(Long chatId, LinkInfo linkInfo) {
        Object value = redisTemplate.opsForValue().get(String.valueOf(chatId));
        DigestLinkUpdate digestLinkUpdate = objectMapper.convertValue(value, DigestLinkUpdate.class);

        if (digestLinkUpdate == null) {
            digestLinkUpdate = new DigestLinkUpdate(chatId, new ArrayList<>());
        }

        digestLinkUpdate.linkInfos().add(linkInfo);
        redisTemplate.opsForValue().set(chatId.toString(), digestLinkUpdate);
    }

    public DigestLinkUpdate getDigestState(Long chatId) {
        Object value = redisTemplate.opsForValue().get(String.valueOf(chatId));
        return objectMapper.convertValue(value, DigestLinkUpdate.class);
    }

    public void deleteState(Long chatId) {
        redisTemplate.delete(chatId.toString());
    }

    public List<DigestLinkUpdate> getAllDigestStates(Integer limit) {
        List<DigestLinkUpdate> result = new ArrayList<>(limit);
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(ONLY_DIGIT_WILDCARD_PATTERN)
                .count(limit)
                .build();
        int count = 0;

        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            while (cursor.hasNext() && count < limit) {
                String key = cursor.next();
                Object value = redisTemplate.opsForValue().get(key);

                if (value != null) {
                    result.add(objectMapper.convertValue(value, DigestLinkUpdate.class));
                    count++;
                }
            }
        }

        return result;
    }
}
