package backend.academy.bot.configuration;

import static backend.academy.bot.constants.KafkaConstValues.DEAD_LETTER_QUEUE_FORMAT;

import backend.academy.bot.dto.linkUpdate.DigestLinkUpdate;
import backend.academy.bot.dto.linkUpdate.ImmediateLinkUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class KafkaConfiguration {

    private static final int NUM_PARTITIONS = 1;
    private static final short REPLICATION_FACTOR = 1;

    @Bean
    public NewTopic immediateLinkUpdateDlq(BotConfiguration botConfiguration) {
        return new NewTopic(
                String.format(
                        DEAD_LETTER_QUEUE_FORMAT, botConfiguration.kafkaTopics().immediateLinkUpdate()),
                NUM_PARTITIONS,
                REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic digestLinkUpdateDlq(BotConfiguration botConfiguration) {
        return new NewTopic(
                String.format(
                        DEAD_LETTER_QUEUE_FORMAT, botConfiguration.kafkaTopics().digestLinkUpdate()),
                NUM_PARTITIONS,
                REPLICATION_FACTOR);
    }

    @Bean
    public ConsumerFactory<String, ImmediateLinkUpdate> immediateConsumerFactory(
            KafkaProperties kafkaProperties, BotConfiguration botConfiguration) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(
                JsonDeserializer.VALUE_DEFAULT_TYPE,
                botConfiguration.kafkaTopics().immediateLinkUpdate().valueType());

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(ImmediateLinkUpdate.class)));
    }

    @Bean
    public ConsumerFactory<String, DigestLinkUpdate> digestConsumerFactory(
            KafkaProperties kafkaProperties, BotConfiguration botConfiguration) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        props.put(
                JsonDeserializer.VALUE_DEFAULT_TYPE,
                botConfiguration.kafkaTopics().digestLinkUpdate().valueType());

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(DigestLinkUpdate.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ImmediateLinkUpdate>
            validatedImmediateLinkUpdateContainerFactory(
                    ConsumerFactory<String, ImmediateLinkUpdate> consumerFactory, LocalValidatorFactoryBean validator) {
        return createValidatedContainerFactory(consumerFactory, record -> {
            ImmediateLinkUpdate value = record.value();
            if (value == null) {
                return Collections.emptySet();
            }

            Set<ConstraintViolation<?>> violations = new HashSet<>();
            violations.addAll(validator.validate(value));
            violations.addAll(validator.validate(value.eventDto()));
            return violations;
        });
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DigestLinkUpdate> validatedDigestLinkUpdateContainerFactory(
            ConsumerFactory<String, DigestLinkUpdate> consumerFactory, LocalValidatorFactoryBean validator) {
        return createValidatedContainerFactory(consumerFactory, record -> {
            DigestLinkUpdate value = record.value();
            if (value == null) {
                return Collections.emptySet();
            }

            Set<ConstraintViolation<?>> violations = new HashSet<>(validator.validate(value));
            value.linkInfos().stream().map(validator::validate).forEach(violations::addAll);
            return violations;
        });
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> createValidatedContainerFactory(
            ConsumerFactory<String, T> consumerFactory,
            Function<ConsumerRecord<String, T>, Set<ConstraintViolation<?>>> validationProcess) {

        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        factory.setRecordInterceptor((record, consumer) -> {
            Set<ConstraintViolation<?>> violations = validationProcess.apply(record);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            return record;
        });

        return factory;
    }
}
