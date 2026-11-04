package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserRequestDtoJsonTest {

    @Autowired
    private JacksonTester<UserRequestDto> json;

    @Test
    void testSerializeAndDeserialize() throws IOException {
        UserRequestDto dto = new UserRequestDto();
        dto.setName("Ivan");
        dto.setEmail("ivan@yandex.ru");

        JsonContent<UserRequestDto> result = json.write(dto);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ivan@yandex.ru");

        UserRequestDto parsedDto = json.parseObject(result.getJson());
        assertThat(parsedDto.getName()).isEqualTo("Ivan");
        assertThat(parsedDto.getEmail()).isEqualTo("ivan@yandex.ru");
    }
}