package ru.kurakin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.kurakin.model.User;
import ru.kurakin.repository.UserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class Consumer {
    private final UserRepository repository;

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void listen(@Payload String us) throws JsonProcessingException {
        User user = new ObjectMapper().readValue(us, User.class);
        repository.save(user);
        log.info("Сохранённая сущность: {}", user);
    }
}
