package ru.practicum.shareit.item;

import jdk.jfr.Label;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.stream.Collectors;


public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final ItemRepository itemRepository;

    public ItemRepositoryImpl(@Lazy ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> findAllItemsByOwner(Long id) {
        return itemRepository.findAll().stream().filter((s) -> s.getOwner().getId().equals(id))
                .collect(Collectors.toList());

    }
}
