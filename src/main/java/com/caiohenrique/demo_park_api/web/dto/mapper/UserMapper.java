package com.caiohenrique.demo_park_api.web.dto.mapper;


import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.web.dto.UserCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    // Instância única e compartilhada: a criação de ModelMapper é custosa
    // (faz scanning/reflection), então não deve ser refeita a cada requisição.
    private static final ModelMapper MAPPER = new ModelMapper();

    static {
        // Registra uma única vez o mapeamento User -> UserResponseDTO.
        // O postConverter remove o prefixo "ROLE_" do enum (ROLE_ADMIN -> ADMIN)
        // para o DTO expor apenas ADMIN | CLIENT.
        TypeMap<User, UserResponseDTO> typeMap = MAPPER.typeMap(User.class, UserResponseDTO.class);
        // Pula o mapeamento automático de 'role' (enum -> String) porque o postConverter
        // abaixo é quem define o valor correto, sem o prefixo "ROLE_".
        typeMap.addMappings(mapping -> mapping.skip(UserResponseDTO::setRole));
        typeMap.setPostConverter(context -> {
            User source = context.getSource();
            if (source.getRole() != null) {
                // remove na conversão a String "ROLE_" do enum, p/ inserir apenas ADMIN | CLIENT
                context.getDestination().setRole(source.getRole().name().substring("ROLE_".length()));
            }
            return context.getDestination();
        });
    }

    public static User toUser(UserCreateDTO createDto) {
        return MAPPER.map(createDto, User.class);
    }

    public static UserResponseDTO toResponseDto(User user) {
        return MAPPER.map(user, UserResponseDTO.class);
    }

    public static List<UserResponseDTO> toListDto(List<User> users) {
        return users.stream().map(user -> toResponseDto(user)).collect(Collectors.toList());
    }
}
