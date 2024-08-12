package ru.kurakin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.kurakin.dto.UserDtoIn;
import ru.kurakin.dto.UserDtoOut;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReplyingKafkaTemplateConfiguration {

    @Bean
    public ReplyingKafkaTemplate<String, UserDtoIn, UserDtoOut> replyKafkaTemplate(ProducerFactory<String, UserDtoIn> pf,
                                                                                           KafkaMessageListenerContainer<String, UserDtoOut> container) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.group}")
    private String groupId;

    @Value("${kafka.topic.name.reply}")
    private String replyTopic;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public Map < String, Object > consumerConfigs() {
        Map < String, Object > props = new HashMap < > ();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return props;
    }

    @Bean
    public ConsumerFactory<String, UserDtoOut> replyConsumerFactory() {
        return new DefaultKafkaConsumerFactory< >(consumerConfigs(), new StringDeserializer(),
                new JsonDeserializer<UserDtoOut>(UserDtoOut.class, false));
    }

    @Bean
    public ProducerFactory<String, UserDtoIn> requestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaMessageListenerContainer<String, UserDtoOut> replyListenerContainer(ConsumerFactory<String, UserDtoOut> cf) {
        ContainerProperties containerProperties = new ContainerProperties(replyTopic);
        containerProperties.setGroupId(groupId);
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }
}
