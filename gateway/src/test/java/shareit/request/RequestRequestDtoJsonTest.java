package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestRequestDtoJsonTest {

    @Autowired
    private JacksonTester<RequestRequestDto> json;

    @Test
    void testSerializeAndDeserialize() throws IOException {
        RequestRequestDto dto = new RequestRequestDto();
        dto.setDescription("Нужна дрель на выходные");

        JsonContent<RequestRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель на выходные");

        RequestRequestDto parsedDto = json.parseObject(result.getJson());
        assertThat(parsedDto.getDescription()).isEqualTo("Нужна дрель на выходные");
    }
}