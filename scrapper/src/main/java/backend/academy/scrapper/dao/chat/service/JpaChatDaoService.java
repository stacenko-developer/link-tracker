package backend.academy.scrapper.dao.chat.service;

import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chat.repository.JpaChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JpaChatDaoService implements ChatDaoService {

    private final JpaChatRepository jpaChatRepository;

    @Override
    @Transactional
    public Chat save(Chat chat) {
        return jpaChatRepository.save(chat);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Chat findChatById(Long id) {
        return jpaChatRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {
        jpaChatRepository.deleteById(id);
    }
}
