package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.jwt.JwtUserDetails;
import com.caiohenrique.demo_park_api.service.ClientService;
import com.caiohenrique.demo_park_api.service.UserService;
import com.caiohenrique.demo_park_api.web.dto.ClientCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ClientResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.ClientMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid ClientCreateDTO clientCreateDTO,
                                                    @AuthenticationPrincipal JwtUserDetails userDetails) {

        Client client = ClientMapper.toClient(clientCreateDTO);

        /*
         Recupera o usuário autenticado da requisição.
         O Spring Security extrai o usuário do JWT enviado no header Authorization,
         armazena no SecurityContext e injeta automaticamente aqui através de
         @AuthenticationPrincipal. Com o id do usuário autenticado, buscamos a
         entidade User no banco e associamos ao Client que está sendo criado.
         */
        client.setUser(userService.findById(userDetails.getId()));

        clientService.save(client);

        return ResponseEntity.status(201).body(ClientMapper.clientResponseDTO(client));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok().body(ClientMapper.clientResponseDTO(client));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Client>> getAll(Pageable pageable) {
        Page<Client> clientList = clientService.findAll(pageable);
        return ResponseEntity.ok().body(clientList);
    }
}
