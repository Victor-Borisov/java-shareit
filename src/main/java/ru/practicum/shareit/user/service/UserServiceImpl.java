package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        throwIfExists(userDto.getEmail());
        User user = userRepository.create(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        UserDto userDtoExisted = getUserById(id);
        if (userDto.getEmail() != null && !userDto.getEmail().equals(userDtoExisted.getEmail())) {
            throwIfExists(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.update(id, UserMapper.fromUserDto(userDto)));
    }

    @Override
    public UserDto deleteUser(Long id) {
        UserDto userDto = getUserById(id);
        userRepository.delete(id);
        return userDto;
    }

    private void throwIfExists(String email) {
        userRepository.getAll().stream()
            .filter(user -> user.getEmail().equalsIgnoreCase(email))
            .findAny()
            .ifPresent(s -> {
                throw new ConflictException("User with email = " + email + " already exists");
            });
    }
}
