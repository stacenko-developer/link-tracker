package backend.academy.scrapper.dao.chatDigestState.service;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.LinkInfo;
import backend.academy.scrapper.dao.chatDigestState.repository.RedisChatDigestStateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisChatDigestStateService implements ChatDigestStateService {

    private final RedisChatDigestStateRepository redisChatDigestStateRepository;

    @Override
    public void addLinkInfoToDigestState(Long chatId, LinkInfo linkInfo) {
        redisChatDigestStateRepository.addNotificationToState(chatId, linkInfo);
    }

    @Override
    public DigestLinkUpdate getDigestState(Long chatId) {
        return redisChatDigestStateRepository.getDigestState(chatId);
    }

    @Override
    public void deleteDigestState(Long chatId) {
        redisChatDigestStateRepository.deleteState(chatId);
    }

    @Override
    public List<DigestLinkUpdate> getAllDigestStates(Integer limit) {
        return redisChatDigestStateRepository.getAllDigestStates(limit);
    }
}
