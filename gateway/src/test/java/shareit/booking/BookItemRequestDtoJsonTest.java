package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void testSerializeAndDeserialize() throws IOException {
        BookItemRequestDto dto = new BookItemRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 11, 10, 12, 0, 0));
        dto.setEnd(LocalDateTime.of(2026, 11, 11, 12, 0, 0));

        JsonContent<BookItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-11-10T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-11-11T12:00:00");

        String content = result.getJson();
        BookItemRequestDto parsedDto = json.parseObject(content);

        assertThat(parsedDto.getItemId()).isEqualTo(1L);
        assertThat(parsedDto.getStart()).isEqualTo(LocalDateTime.of(2026, 11, 10, 12, 0, 0));
        assertThat(parsedDto.getEnd()).isEqualTo(LocalDateTime.of(2026, 11, 11, 12, 0, 0));
    }
}