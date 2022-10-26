package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.Dao;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao extends Dao<User> {

    List<User> getAll();

    boolean isExistedByEmail(String email, Long excludedId);
}
