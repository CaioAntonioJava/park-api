package com.caiohenrique.demo_park_api.web.dto.mapper;


import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.web.dto.UserCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class UserMapper {

    public static User toUser (UserCreateDTO createDto) {
        return new ModelMapper().map(createDto, User.class);
    }

    public static UserResponseDTO toResponseDto (User user) {
        String role = user.getRole().name().substring("ROLE_".length()); // remove na conversão a String "ROLE_" do enum, p/ inserir apenas ADMIN | CLIENT

        PropertyMap<User, UserResponseDTO> props = new PropertyMap<User, UserResponseDTO>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };

        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props); // Quando o ModelMapper fizer a conversão ele terá de usar o valor da variável 'role'
        return mapper.map(user, UserResponseDTO.class);
    }
}
