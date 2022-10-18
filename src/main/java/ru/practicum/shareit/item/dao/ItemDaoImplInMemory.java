package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImplInMemory implements ItemDao {
    private final Map<Integer, Item> items = new HashMap<>();
    private static int id = 0;

    private int getId() {
        return ++id;
    }

    @Override
    public Item createItem(Item item) {
        int id = getId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(Integer id, Item patchItem) {
        Item item = items.get(id);
        if (patchItem.getName() != null) {
            item.setName(patchItem.getName());
        }
        if (patchItem.getDescription() != null) {
            item.setDescription(patchItem.getDescription());
        }
        if (patchItem.getAvailable() != null) {
            item.setAvailable(patchItem.getAvailable());
        }
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getItemsByOwner(Integer userId) {
        return items.values().stream().filter(item -> item.getOwner().getId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String searchText) {
        return items.values().stream()
            .filter(Item::getAvailable)
            .filter(item -> item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(searchText.toLowerCase()))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> deleteItem(Integer id) {
        return Optional.ofNullable(items.remove(id));
    }
}
