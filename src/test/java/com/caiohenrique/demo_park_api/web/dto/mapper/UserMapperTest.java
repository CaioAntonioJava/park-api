package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.web.dto.UserCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUser_shouldMapCreateDtoToUser() {
        UserCreateDTO dto = new UserCreateDTO("user@email.com.br", "password123");

        User user = UserMapper.toUser(dto);

        assertNotNull(user);
        assertEquals("user@email.com.br", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertNull(user.getId());
    }

    @Test
    void toResponseDto_withAdminRole_shouldStripRolePrefix() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin@email.com.br");
        user.setPassword("encoded");
        user.setRole(User.Role.ROLE_ADMIN);

        UserResponseDTO dto = UserMapper.toResponseDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("admin@email.com.br", dto.getUsername());
        assertEquals("ADMIN", dto.getRole());
    }

    @Test
    void toResponseDto_withClientRole_shouldStripRolePrefix() {
        User user = new User();
        user.setId(2L);
        user.setUsername("client@email.com.br");
        user.setPassword("encoded");
        user.setRole(User.Role.ROLE_CLIENT);

        UserResponseDTO dto = UserMapper.toResponseDto(user);

        assertEquals("CLIENT", dto.getRole());
    }

    @Test
    void toResponseDto_withNullRole_shouldNotThrow() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user@email.com.br");
        user.setPassword("encoded");
        user.setRole(null);

        UserResponseDTO dto = UserMapper.toResponseDto(user);

        assertNull(dto.getRole());
    }

    @Test
    void toListDto_shouldMapListOfUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1@email.com.br");
        user1.setRole(User.Role.ROLE_ADMIN);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2@email.com.br");
        user2.setRole(User.Role.ROLE_CLIENT);

        List<UserResponseDTO> dtos = UserMapper.toListDto(List.of(user1, user2));

        assertEquals(2, dtos.size());
        assertEquals("ADMIN", dtos.get(0).getRole());
        assertEquals("CLIENT", dtos.get(1).getRole());
    }
}
