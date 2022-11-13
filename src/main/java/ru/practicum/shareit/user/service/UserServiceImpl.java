package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
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
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                             .map(UserMapper::toUserDto)
                             .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NotFoundException("User not found"));

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.fromUserDto(userDto));

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NotFoundException("User not found"));
        if(!StringUtils.isBlank(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        if(!StringUtils.isBlank(userDto.getName())) {
            user.setName(userDto.getName());
        }

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto delete(Long id) {
        UserDto userDto = getById(id);
        userRepository.deleteById(id);

        return userDto;
    }
}
