package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserDaoImplInMemory implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private static long id = 0;

    private long getId() {
        return ++id;
    }

    @Override
    public User create(User user) {
        long id = getId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(Long id, User patchUser) {
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
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean isExistedByEmail(String email, Long excludedId) {
        return users.values().stream()
                    .anyMatch(user -> user.getEmail().equalsIgnoreCase(email) && !user.getId().equals(excludedId));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}
