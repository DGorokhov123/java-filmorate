package ru.yandex.practicum.filmorate.model;

public class UserMapper {

    public static UserApiDto toDto(User user) {
        UserApiDto dto = new UserApiDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());
        dto.setFollowing(user.getFollowing());
        dto.setFollowers(user.getFollowers());
        return dto;
    }

    public static User toUser(UserApiDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setLogin(dto.getLogin());
        user.setName(dto.getName());
        user.setBirthday(dto.getBirthday());
        user.setFollowing(dto.getFollowing());
        user.setFollowers(dto.getFollowers());
        return user;
    }

}
