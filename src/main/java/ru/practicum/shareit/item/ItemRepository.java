package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId, Pageable page);

    @Query("SELECT i FROM Item AS i " +
            "WHERE (LOWER(i.name) LIKE LOWER(concat('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(concat('%', :text, '%'))) AND i.available=true")
    List<Item> findByNameOrDescriptionAndAvailable(@Param("text") String text, Pageable page);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findByRequest_IdIn(List<Long> requestsId);
}
