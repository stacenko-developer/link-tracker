package backend.academy.scrapper.dao.chat.repository;

import backend.academy.scrapper.dao.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {}
