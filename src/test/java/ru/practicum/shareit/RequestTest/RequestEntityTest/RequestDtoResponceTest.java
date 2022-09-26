package ru.practicum.shareit.RequestTest.RequestEntityTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.RequestDtoResponse;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoResponceTest {

    @Autowired
    private JacksonTester<RequestDtoResponse> json;

    @Test
    public void testRequestDtoResponce() throws Exception {
        RequestDtoResponse requestDtoResponse = new RequestDtoResponse();
        requestDtoResponse.setId(1L);
        requestDtoResponse.setDescription("description");

        JsonContent<RequestDtoResponse> result = json.write(requestDtoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
    }
}
