package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;


import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long id, ItemRequestShortDto itemRequestShortDto);

    ItemRequestDto getRequestById(Long id, Long requestId);

    List<ItemRequestDto> getAllRequests(Long id, Pageable page);

    List<ItemRequestDto> getAllRequestsByRequester(Long userId, Pageable page);

}
