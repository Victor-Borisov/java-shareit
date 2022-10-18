package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Integer id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Integer id, UserDto userDto);

    UserDto deleteUser(Integer id);
}
