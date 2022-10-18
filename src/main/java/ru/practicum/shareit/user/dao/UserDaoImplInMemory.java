package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserDaoImplInMemory implements UserDao {
    private final Map<Integer, User> users = new HashMap<>();
    private static int id = 0;
    private int getId() {
        return ++id;
    }
    @Override
    public User createUser(User user) {
        int id = getId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(Integer id, User patchUser) {
        User user = users.get(id);
        if (patchUser.getName() != null) {
            user.setName(patchUser.getName());
        }
        if (patchUser.getEmail() != null) {
            user.setEmail(patchUser.getEmail());
        }
        users.put(id, user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> deleteUser(Integer id) {
        return Optional.ofNullable(users.remove(id));
    }
}
