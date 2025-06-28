package backend.academy.scrapper.service.client;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BotClientService extends CommonClientService {

    private final BotClient botClient;

    public BotClientService(ObjectMapper objectMapper, BotClient botClient) {
        super(objectMapper);
        this.botClient = botClient;
    }

    public ResponseDto<Void> immediateUpdate(ImmediateLinkUpdate immediateLinkUpdate) {
        return execute(() -> botClient.immediateUpdate(immediateLinkUpdate));
    }

    public ResponseDto<Void> digestUpdate(DigestLinkUpdate digestLinkUpdate) {
        return execute(() -> botClient.digestUpdate(digestLinkUpdate));
    }
}
