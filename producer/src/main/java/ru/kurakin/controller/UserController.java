package ru.kurakin.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kurakin.model.User;
import ru.kurakin.service.Producer;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {
    private final Producer producer;

    @PostMapping("/sendToDB")
    public String saveUser(@RequestBody User user) {
        producer.sendMessageToTopic(user);
        return "User was sent successfully";
    }
}
