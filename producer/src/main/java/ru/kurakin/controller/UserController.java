package ru.kurakin.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kurakin.dto.UserDtoIn;
import ru.kurakin.dto.UserDtoOut;
import ru.kurakin.service.Producer;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {
    private final Producer producer;

    @PostMapping("/sendToDB")
    public UserDtoOut saveUser(@RequestBody UserDtoIn userDtoIn) throws ExecutionException, InterruptedException {
        return producer.sendMessageToTopicAndReceiveReplying(userDtoIn);
    }
}
