package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it " +
            "from Item as it " +
            "join it.owner as ow " +
            "where ow.id = ?1 " +
            "order by it.id asc")
    List<Item> getAllUserItems(Long userId);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))" +
            " and i.available = true")
    List<Item> findAllByNameContainingOrDescriptionContainingIgnoreCase(String text);

    @Query("select i " +
            "from Item i " +
            "join i.request as r " +
            "where r.id = ?1 " +
            "order by r.created desc")
    List<Item> findItemsByRequest(Long requestId);

}
