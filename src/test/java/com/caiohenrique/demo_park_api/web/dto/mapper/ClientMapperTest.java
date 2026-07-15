package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.web.dto.ClientCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ClientResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {

    @Test
    void toClient_shouldMapCreateDtoToClient() {
        ClientCreateDTO dto = new ClientCreateDTO("João da Silva", "12345678909");

        Client client = ClientMapper.toClient(dto);

        assertNotNull(client);
        assertEquals("João da Silva", client.getName());
        assertEquals("12345678909", client.getCpf());
        assertNull(client.getId());
        assertNull(client.getUser());
    }

    @Test
    void clientResponseDTO_shouldMapClientToDto() {
        User user = new User();
        user.setId(1L);

        Client client = new Client();
        client.setId(1L);
        client.setName("Maria Souza");
        client.setCpf("98765432100");
        client.setUser(user);

        ClientResponseDTO dto = ClientMapper.clientResponseDTO(client);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Maria Souza", dto.getName());
        assertEquals("98765432100", dto.getCpf());
    }
}
