package backend.academy.scrapper.configuration;

import backend.academy.scrapper.dao.chat.repository.JdbcChatRepository;
import backend.academy.scrapper.dao.chat.repository.JpaChatRepository;
import backend.academy.scrapper.dao.chat.service.ChatDaoService;
import backend.academy.scrapper.dao.chat.service.JdbcChatDaoService;
import backend.academy.scrapper.dao.chat.service.JpaChatDaoService;
import backend.academy.scrapper.dao.filter.repository.JdbcFilterRepository;
import backend.academy.scrapper.dao.filter.repository.JpaFilterRepository;
import backend.academy.scrapper.dao.filter.service.FilterDaoService;
import backend.academy.scrapper.dao.filter.service.JdbcFilterDaoService;
import backend.academy.scrapper.dao.filter.service.JpaFilterDaoService;
import backend.academy.scrapper.dao.link.repository.jdbc.JdbcLinkRepository;
import backend.academy.scrapper.dao.link.repository.jpa.JpaLinkRepository;
import backend.academy.scrapper.dao.link.service.JdbcLinkDaoService;
import backend.academy.scrapper.dao.link.service.JpaLinkDaoService;
import backend.academy.scrapper.dao.link.service.LinkDaoService;
import backend.academy.scrapper.dao.tag.repository.JdbcTagRepository;
import backend.academy.scrapper.dao.tag.repository.JpaTagRepository;
import backend.academy.scrapper.dao.tag.service.JdbcTagDaoService;
import backend.academy.scrapper.dao.tag.service.JpaTagDaoService;
import backend.academy.scrapper.dao.tag.service.TagDaoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbServicesConfiguration {

    private static final String ACCESS_TYPE_PROPERTY = "app.access-type";
    private static final String SQL_ACCESS_TYPE = "SQL";
    private static final String ORM_ACCESS_TYPE = "ORM";

    @Configuration
    @ConditionalOnProperty(name = ACCESS_TYPE_PROPERTY, havingValue = SQL_ACCESS_TYPE)
    public static class SqlConfiguration {

        @Bean
        public ChatDaoService chatDaoService(JdbcChatRepository jdbcChatRepository) {
            return new JdbcChatDaoService(jdbcChatRepository);
        }

        @Bean
        public FilterDaoService filterDaoService(JdbcFilterRepository jdbcFilterRepository) {
            return new JdbcFilterDaoService(jdbcFilterRepository);
        }

        @Bean
        public LinkDaoService linkDaoService(JdbcLinkRepository jdbcLinkRepository) {
            return new JdbcLinkDaoService(jdbcLinkRepository);
        }

        @Bean
        public TagDaoService tagDaoService(JdbcTagRepository jdbcTagRepository) {
            return new JdbcTagDaoService(jdbcTagRepository);
        }
    }

    @Configuration
    @ConditionalOnProperty(name = ACCESS_TYPE_PROPERTY, havingValue = ORM_ACCESS_TYPE)
    public static class OrmConfiguration {

        @Bean
        public ChatDaoService chatDaoService(JpaChatRepository jpaChatRepository) {
            return new JpaChatDaoService(jpaChatRepository);
        }

        @Bean
        public FilterDaoService filterDaoService(JpaFilterRepository jpaFilterRepository) {
            return new JpaFilterDaoService(jpaFilterRepository);
        }

        @Bean
        public LinkDaoService linkDaoService(JpaLinkRepository jpaLinkRepository) {
            return new JpaLinkDaoService(jpaLinkRepository);
        }

        @Bean
        public TagDaoService tagDaoService(JpaTagRepository jpaTagRepository) {
            return new JpaTagDaoService(jpaTagRepository);
        }
    }
}
