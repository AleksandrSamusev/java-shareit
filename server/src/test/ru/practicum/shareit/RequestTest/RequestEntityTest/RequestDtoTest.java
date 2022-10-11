package ru.practicum.shareit.RequestTest.RequestEntityTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.RequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoTest {

    @Autowired
    private JacksonTester<RequestDto> json;

    @Test
    public void testRequestDto() throws Exception {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("need something");

        JsonContent<RequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("need something");
    }

}