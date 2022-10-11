package ru.practicum.shareit.ItemTest.ItemDtoTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemDtoRequest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoRequestTest {

    @Autowired
    private JacksonTester<ItemDtoRequest> json;

    @Test
    public void testItemDtoRequest() throws Exception {
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest();
        itemDtoRequest.setId(1L);
        itemDtoRequest.setName("item");
        itemDtoRequest.setIsAvailable(true);
        itemDtoRequest.setDescription("just an item");

        JsonContent<ItemDtoRequest> result = json.write(itemDtoRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("just an item");

    }
}