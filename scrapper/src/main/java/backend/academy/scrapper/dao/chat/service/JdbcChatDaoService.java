package backend.academy.scrapper.dao.chat.service;

import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chat.repository.JdbcChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcChatDaoService implements ChatDaoService {

    private final JdbcChatRepository jdbcChatRepository;

    @Override
    @Transactional
    public Chat save(Chat chat) {
        return jdbcChatRepository.save(chat);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Chat findChatById(Long id) {
        return jdbcChatRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {
        jdbcChatRepository.deleteById(id);
    }
}
