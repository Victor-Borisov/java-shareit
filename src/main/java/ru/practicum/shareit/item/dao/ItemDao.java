package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.Dao;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends Dao<Item> {

    List<Item> getByOwner(Long userId);

    List<Item> search(String query);

}
