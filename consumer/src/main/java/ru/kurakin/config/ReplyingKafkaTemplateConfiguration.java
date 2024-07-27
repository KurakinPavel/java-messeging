package ru.kurakin.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.kurakin.dto.UserDtoIn;
import ru.kurakin.dto.UserDtoOut;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReplyingKafkaTemplateConfiguration {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.group}")
    private String groupId;

    @Bean
    public Map< String, Object > consumerConfigs() {
        Map < String, Object > props = new HashMap< >();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return props;
    }

    @Bean
    public Map < String, Object > producerConfigs() {
        Map < String, Object > props = new HashMap < > ();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory< String, UserDtoIn> requestConsumerFactory() {
        return new DefaultKafkaConsumerFactory< >(consumerConfigs(), new StringDeserializer(),
                new JsonDeserializer<UserDtoIn>(UserDtoIn.class, false));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer< String, UserDtoIn>> requestListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory< String, UserDtoIn> factory =
                new ConcurrentKafkaListenerContainerFactory < > ();
        factory.setConsumerFactory(requestConsumerFactory());
        factory.setReplyTemplate(replyTemplate());
        return factory;
    }

    @Bean
    public ProducerFactory< String, UserDtoOut > replyProducerFactory() {
        return new DefaultKafkaProducerFactory< >(producerConfigs());
    }

    @Bean
    public KafkaTemplate < String, UserDtoOut> replyTemplate() {
        return new KafkaTemplate< >(replyProducerFactory());
    }
}
