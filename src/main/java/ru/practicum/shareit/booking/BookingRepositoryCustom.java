package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface BookingRepositoryCustom {

    List<Item> findAllBookingsByBookerId(Long id);
}
