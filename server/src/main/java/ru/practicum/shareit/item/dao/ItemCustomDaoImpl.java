package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class ItemCustomDaoImpl implements ItemCustomDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Item> findAllByCriteria(String pattern, PageRequest pageRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> criteria = cb.createQuery(Item.class);
        Root<Item> item = criteria.from(Item.class);
        criteria.select(item).where(
                cb.and(
                        cb.or(
                                cb.like(cb.upper(item.get("name")), "%" + pattern.toUpperCase() + "%"),
                                cb.like(cb.upper(item.get("description")), "%" + pattern.toUpperCase() + "%")
                        ),
                        cb.equal(item.get("available"), true)
                )
        );
        return entityManager.createQuery(criteria).getResultList();
    }
}
