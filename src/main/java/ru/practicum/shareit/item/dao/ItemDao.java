package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item createItem(Item item);

    Item updateItem(Integer id, Item item);

    Optional<Item> getItemById(Integer id);

    List<Item> getItemsByOwner(Integer userId);

    List<Item> searchItems(String query);

    Optional<Item> deleteItem(Integer id);
}
