Архитектура проекта LinkTracker
1. Bot Service
    * Регистрация аккаунта в системе
    * Получение списка отслеживаемых ссылок
    * Получение списка доступных команд
    * Управление подписками на ссылки
    * Настройка режима отправки уведомлений
    * Отправка уведомлений по отслеживаемым ссылкам
2. Scraper Service
    * Создание учетных записей пользователей, отслеживаемых ссылок, фильтров и тэгов к ним
    * Периодическая проверка ссылок на наличие изменений путем обращения к API Github и Stackoverflow
    * Отправка изменений по отслеживаемым ссылкам в Bot Service с помощью HTTP или Kafka
    * Получение информации об учетной записи пользователя с его настройками
    * Получение доступных режимов отправки уведомлений
3. PostgreSQL Database
    * Хранение информации об учетных записях пользователей и их настроек
    * Хранение отслеживаемых ссылок, тэгов и фильтров к ним
4. Apache Kafka
    * Отправка уведомлений по отслеживаемым ссылкам из Scrapper Service в Bot Service
5. Redis
    * Кеширование запросов на получение списка отслеживаемых ссылок
    * Накопление состояния для отправки пользователю уведомления в виде дайджеста
6. Мониторинг
    * Prometheus собирает метрики у Bot Service и Scrapper Service
    * Grafana отображает метрики в виде графиков и дашбордов
```mermaid
flowchart LR
 subgraph ExternalServices["Внешние API"]
        GithubAPI["GithubAPI"]
        StackoverflowAPI["StackoverflowAPI"]
  end
 subgraph UserSpace["Пространство пользователя"]
        User["Пользователь"]
        TelegramBot["TelegramBot"]
  end
 subgraph BotApi["API"]
        BotActuatorAPI["BotActuatorAPI"]
        BotAPI["BotAPI"]
  end
 subgraph BotService["Bot Service"]
        BotApi
        CommandHandler["CommandHandler"]
        KafkaListener["KafkaListener"]
  end
 subgraph ScrapperApi["API"]
        ScrapperAPI["ScrapperAPI"]
        ScrapperActuatorAPI["ScrapperActuatorAPI"]
  end
 subgraph ScrapperSchedulers["Schedulers"]
        OrphanRemoveScheduler["OrphanRemoveScheduler"]
        LinkTrackerScheduler["LinkTrackerScheduler"]
        DigestSenderScheduler["DigestSenderScheduler"]
  end
 subgraph ScrapperService["Scrapper Service"]
        ScrapperApi
        ScrapperSchedulers
  end
 subgraph Databases["Databases"]
        PostgreSQL[("PostgreSQL")]
        Redis[("Redis")]
  end
 subgraph MessageQueue["MessageQueue"]
        Kafka["Kafka"]
  end
 subgraph Monitoring["Monitoring"]
        Prometheus["Prometheus"]
        Grafana["Grafana"]
  end
    OrphanRemoveScheduler -- Удаление неотслеживаемых ссылок --> PostgreSQL
    LinkTrackerScheduler <-- Получение ссылок для отслеживания --> PostgreSQL
    ScrapperAPI <--> PostgreSQL
    DigestSenderScheduler <-- Получение состояния для дайджеста --> Redis
    LinkTrackerScheduler -- Накопление состояния для дайджеста --> Redis

    LinkTrackerScheduler <-- получение информации о репозиториях --> GithubAPI
    LinkTrackerScheduler <-- получение информации о вопросах --> StackoverflowAPI

    LinkTrackerScheduler -- Отправка уведомлений --> Kafka & BotAPI

    Prometheus <-- Получение метрик --> BotActuatorAPI & ScrapperActuatorAPI
    Prometheus <-- Визуальное отображение --> Grafana
    User -- Команды --> TelegramBot
    TelegramBot -- Результат обработки команд --> User
    TelegramBot -- Уведомления --> User
    BotAPI -- Отправка уведомлений --> TelegramBot
    TelegramBot -- Команда пользователя --> CommandHandler
    CommandHandler -- Результат обработки команды --> TelegramBot
    KafkaListener -- Отправка уведомлений --> TelegramBot
    DigestSenderScheduler -- Отправка уведомлений --> BotAPI & Kafka
    Kafka <-- Получение уведомлений --> KafkaListener
    CommandHandler <--> ScrapperAPI
    CommandHandler <-- Получает данные из кэша --> Redis
```
