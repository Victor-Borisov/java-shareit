package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemCustomDao {
    List<Item> findAllByCriteria(String criteria);
}
