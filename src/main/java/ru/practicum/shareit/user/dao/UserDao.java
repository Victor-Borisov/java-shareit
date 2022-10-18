package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User createUser(User user);

    User updateUser(Integer id, User user);

    Optional<User> getUserById(Integer id);

    List<User> getAllUsers();

    Optional<User> deleteUser(Integer id);
}
