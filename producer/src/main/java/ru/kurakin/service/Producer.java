package ru.kurakin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kurakin.model.User;

@Service
public class Producer {
    KafkaTemplate<String, User> kafkaTemplate;

    @Value("knowledgeFactory_Topic")
    private String topic;

    public Producer(KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageToTopic(User user) {
        kafkaTemplate.send(topic, user);
    }
}
