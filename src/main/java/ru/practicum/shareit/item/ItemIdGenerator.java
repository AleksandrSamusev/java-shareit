package ru.practicum.shareit.item;

public class ItemIdGenerator {
    private static long id = 1;

    public static void setId(long id) {
        ItemIdGenerator.id = id;
    }

    public static long generateId() {
        return id++;
    }
}
