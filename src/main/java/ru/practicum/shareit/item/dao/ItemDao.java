package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long>, ItemCustomDao {
    List<Item> findAllByOwnerId(Long ownerId, PageRequest pageRequest);
    List<Item> findAllByRequestId(Long requestId);
}
