package com.caiohenrique.demo_park_api.web.dto.mapper;


import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.web.dto.UserCreateDto;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class UserMapper {

    public static User toUser (UserCreateDto createDto) {
        return new ModelMapper().map(createDto, User.class);
    }

    public static UserResponseDto toResponseDto (User user) {
        String role = user.getRole().name().substring("ROLE_".length()); // remove na conversão a String "ROLE_" do enum, p/ inserir apenas ADMIN | CLIENT

        PropertyMap<User, UserResponseDto> props = new PropertyMap<User, UserResponseDto>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };

        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props); // Quando o ModelMapper fizer a conversão ele terá de usar o valor da variável 'role'
        return mapper.map(user, UserResponseDto.class);
    }
}
