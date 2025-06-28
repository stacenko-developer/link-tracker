package backend.academy.scrapper.dao.chatDigestState.service;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.LinkInfo;
import java.util.List;

public interface ChatDigestStateService {

    void addLinkInfoToDigestState(Long chatId, LinkInfo linkInfo);

    DigestLinkUpdate getDigestState(Long chatId);

    void deleteDigestState(Long chatId);

    List<DigestLinkUpdate> getAllDigestStates(Integer limit);
}
