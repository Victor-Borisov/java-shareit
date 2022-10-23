package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImplInMemory implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private static long id = 0;

    private long getId() {
        return ++id;
    }

    @Override
    public Item create(Item item) {
        Long id = getId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Long id, Item patchItem) {
        Item item = items.get(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getByOwner(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String searchText) {
        return items.values().stream()
            .filter(Item::getAvailable)
            .filter(item -> item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(searchText.toLowerCase()))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }


}
