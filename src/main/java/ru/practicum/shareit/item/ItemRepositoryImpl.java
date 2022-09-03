package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class ItemRepositoryImpl  implements ItemRepositoryCustom{

    private final ItemRepository itemRepository;

    public ItemRepositoryImpl(ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> findAllItemsByOwner(Long id) {
        return itemRepository.findAll().stream().filter((s) -> s.getOwnerId().equals(id))
                .collect(Collectors.toList());

    }
}
