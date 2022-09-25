package ru.practicum.shareit.ItemTest.ItemDtoTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemDtoBooking;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoBookingTest {

    @Autowired
    private JacksonTester<ItemDtoBooking> json;

    @Test
    void testItemDtoBooking() throws Exception {
        ItemDtoBooking itemDtoBooking = new ItemDtoBooking(
                1L,
                "Wrench",
                "the best wrench",
                true,
                null,
                null);

        JsonContent<ItemDtoBooking> result = json.write(itemDtoBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Wrench");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("the best wrench");
        assertThat(result).extractingJsonPathStringValue("$.isAvailable").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(null);
    }
}