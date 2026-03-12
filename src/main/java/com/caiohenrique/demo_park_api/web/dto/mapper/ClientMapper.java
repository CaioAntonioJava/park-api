package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.web.dto.ClientCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ClientResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {
    public static Client toClient(ClientCreateDTO clientCreateDTO) {
        return new ModelMapper().map(clientCreateDTO, Client.class);
    }

    public static ClientResponseDTO clientResponseDTO(Client client) {
        return new ModelMapper().map(client, ClientResponseDTO.class);

    }
}
