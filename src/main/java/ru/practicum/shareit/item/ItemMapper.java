package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {

        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setIsAvailable(item.getIsAvailable());
        itemDto.setOwner(item.getOwner());
        itemDto.setRequest(item.getRequest());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getIsAvailable());
        item.setOwner(itemDto.getOwner());
        item.setRequest(itemDto.getRequest());
        return item;
    }
}
