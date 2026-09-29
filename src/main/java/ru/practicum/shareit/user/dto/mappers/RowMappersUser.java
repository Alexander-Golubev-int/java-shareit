package ru.practicum.shareit.user.dto.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RowMappersUser implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
    public static UserDto mapToUserDtoWithId(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
    public static UserDto mapToUserDtoWithoutId(User user) {
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

//    public User updateUserFields(User user, UpdateUserRequestDto request) {
//        if (request.hasEmail()) {
//            user.setEmail(request.getEmail());
//        }
//        if (request.hasLogin()) {
//            user.setLogin(request.getLogin());
//        }
//        if (request.hasName()) {
//            user.setName(request.getName());
//        }
//        if ((request.hasBirthday()))
//            user.setBirthday(user.getBirthday());
//        return user;
//    }
}
