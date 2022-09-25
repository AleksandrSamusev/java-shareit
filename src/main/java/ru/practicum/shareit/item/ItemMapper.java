package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {

        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setIsAvailable(item.getIsAvailable());
        itemDto.setOwner(UserMapper.toUserDto(item.getOwner()));
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getIsAvailable());
        item.setOwner(UserMapper.toUser(itemDto.getOwner()));
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static List<ItemDto> toItemDtos(List<Item> items) {
        List<ItemDto> tempList = new ArrayList<>();
        for (Item item : items) {
            tempList.add(toItemDto(item));
        }
        return tempList;
    }

    public static ItemDtoBooking toItemDtoBooking(Item item) {
        ItemDtoBooking itemDtoBooking = new ItemDtoBooking();
        itemDtoBooking.setId(item.getId());
        itemDtoBooking.setName(item.getName());
        itemDtoBooking.setDescription(item.getDescription());
        itemDtoBooking.setIsAvailable(item.getIsAvailable());
        return itemDtoBooking;
    }

    public static List<ItemDtoBooking> toItemBookingDtos(List<Item> items) {
        List<ItemDtoBooking> tempList = new ArrayList<>();
        for (Item item : items) {
            tempList.add(toItemDtoBooking(item));
        }
        return tempList;
    }

    public static ItemDtoRequest toItemDtoRequest(Item item) {
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest();
        itemDtoRequest.setId(item.getId());
        itemDtoRequest.setName(item.getName());
        itemDtoRequest.setDescription(item.getDescription());
        itemDtoRequest.setIsAvailable(item.getIsAvailable());
        itemDtoRequest.setRequestId(item.getRequestId());


        return itemDtoRequest;
    }

    public static List<ItemDtoRequest> toItemRequestDtos(List<Item> items) {
        List<ItemDtoRequest> tempList = new ArrayList<>();
        for (Item item : items) {
            tempList.add(toItemDtoRequest(item));
        }
        return tempList;
    }

}
