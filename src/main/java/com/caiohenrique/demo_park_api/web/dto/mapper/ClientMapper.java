package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.web.dto.ClientCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ClientResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {

    // Instância única e compartilhada: a criação de ModelMapper é custosa
    // (faz scanning/reflection), então não deve ser refeita a cada requisição.
    private static final ModelMapper MAPPER = new ModelMapper();

    public static Client toClient(ClientCreateDTO clientCreateDTO) {
        return MAPPER.map(clientCreateDTO, Client.class);
    }

    public static ClientResponseDTO clientResponseDTO(Client client) {
        return MAPPER.map(client, ClientResponseDTO.class);
    }
}
