package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepositoryCustom {
    List<Item> findAllItemsByOwner(Long id);
}
