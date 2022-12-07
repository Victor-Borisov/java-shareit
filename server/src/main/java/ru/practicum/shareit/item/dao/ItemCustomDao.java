package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemCustomDao {
    List<Item> findAllByCriteria(String criteria, PageRequest pageRequest);
}
