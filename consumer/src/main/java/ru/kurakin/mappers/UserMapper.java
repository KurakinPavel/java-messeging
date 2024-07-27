package ru.kurakin.mappers;

import ru.kurakin.dto.UserDtoIn;
import ru.kurakin.dto.UserDtoOut;
import ru.kurakin.model.User;

public class UserMapper {

    public static User toUser(UserDtoIn userDtoIn) {
        return new User(
                0,
                userDtoIn.getName() != null ? userDtoIn.getName() : "",
                userDtoIn.getEmail() != null ? userDtoIn.getEmail() : ""
        );
    }

    public static UserDtoOut toUserDtoOut(User user) {
        return new UserDtoOut(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

}
