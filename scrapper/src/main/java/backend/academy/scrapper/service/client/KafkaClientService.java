package backend.academy.scrapper.service.client;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import backend.academy.scrapper.configuration.ScrapperConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaClientService {

    private final ScrapperConfig scrapperConfig;
    private final KafkaTemplate<String, ImmediateLinkUpdate> immediateLinkUpdateKafkaTemplate;
    private final KafkaTemplate<String, DigestLinkUpdate> digestLinkUpdateKafkaTemplate;

    public void sendImmediateLinkUpdate(ImmediateLinkUpdate immediateLinkUpdate) {
        immediateLinkUpdateKafkaTemplate.send(scrapperConfig.kafkaTopics().immediateLinkUpdate(), immediateLinkUpdate);
    }

    public void sendDigestLinkUpdate(DigestLinkUpdate digestLinkUpdate) {
        digestLinkUpdateKafkaTemplate.send(scrapperConfig.kafkaTopics().digestLinkUpdate(), digestLinkUpdate);
    }
}
