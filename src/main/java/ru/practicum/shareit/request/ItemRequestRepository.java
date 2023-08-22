package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterId(Long userId, Pageable pageable);

    List<ItemRequest> findAllByRequesterIdNot(Long userId, Pageable pageable);

    List<ItemRequest> findAllByRequesterId(Long userId, Pageable pageable);

}
