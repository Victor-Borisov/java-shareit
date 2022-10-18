package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Integer id) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto.getEmail());
        User user = userRepository.createUser(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id = " + id + " not found"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && validateEmail(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.updateUser(id, user));
    }

    @Override
    public UserDto deleteUser(Integer id) {
        UserDto userDto = getUserById(id);
        userRepository.deleteUser(id);
        return userDto;
    }

    private boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new BadRequestException("Email can not be empty");
        }
        if (userRepository.getAllUsers().stream()
            .anyMatch(user -> user.getEmail().toLowerCase(Locale.ROOT).equals(email.toLowerCase()))) {
            throw new ValidationException("User with email = " + email + " already exists");
        }
        return true;
    }
}
