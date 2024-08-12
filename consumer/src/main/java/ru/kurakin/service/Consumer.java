package ru.kurakin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import ru.kurakin.dto.UserDtoIn;
import ru.kurakin.dto.UserDtoOut;
import ru.kurakin.mappers.UserMapper;
import ru.kurakin.repository.UserRepository;

import java.io.IOException;

@Service
@Slf4j
public class Consumer {

    private final UserRepository repository;

    public Consumer(UserRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "request_Topic", groupId = "async", containerFactory = "requestListenerContainerFactory")
    @SendTo
    public UserDtoOut listenToTopic(UserDtoIn userDtoIn) throws IOException {
        log.info("Готовлюсь обработать сообщение!");
        UserDtoOut userDtoOut = UserMapper.toUserDtoOut(repository.save(UserMapper.toUser(userDtoIn)));
        log.info("Сущность сохранена");
        return userDtoOut;
    }
}
