package ru.kurakin.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import ru.kurakin.dto.UserDtoIn;
import ru.kurakin.dto.UserDtoOut;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class Producer {
    ReplyingKafkaTemplate<String, UserDtoIn, UserDtoOut> replyingKafkaTemplate;

    @Autowired
    public Producer(ReplyingKafkaTemplate<String, UserDtoIn, UserDtoOut> replyingKafkaTemplate) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    @Value("${kafka.topic.name.request}")
    private String requestTopic;

    @Value("${kafka.topic.name.reply}")
    private String replyTopic;

    public UserDtoOut sendMessageToTopicAndReceiveReplying(UserDtoIn userDtoIn) throws ExecutionException, InterruptedException {
        ProducerRecord<String, UserDtoIn> record = new ProducerRecord<>(requestTopic, userDtoIn);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes()));
        log.info("Готовлюсь отправить сообщение!");
        RequestReplyFuture<String, UserDtoIn, UserDtoOut> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, UserDtoOut> result = sendAndReceive.get();
        log.info("Результат получен. Пытаюсь преобразовать в объект");
        UserDtoOut userDtoOut = result.value();
        System.out.println(userDtoOut);
        return userDtoOut;
    }
}
