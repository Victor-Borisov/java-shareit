package ru.practicum.shareit;

import java.util.Optional;

public interface Dao<T> {
    Optional<T> getById(Long id);

    T create(T t);

    T update(Long id, T t);

    void delete(Long id);
}
