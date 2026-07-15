package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.exception.CpfUniqueViolationException;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.repository.ClientRepository;
import com.caiohenrique.demo_park_api.repository.projection.ClientProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user@email.com");

        client = new Client();
        client.setId(1L);
        client.setName("João da Silva");
        client.setCpf("12345678909");
        client.setUser(user);
    }

    @Test
    void save_withValidClient_shouldSaveAndReturn() {
        when(clientRepository.save(any(Client.class))).thenAnswer(returnsFirstArg());

        Client saved = clientService.save(client);

        assertNotNull(saved);
        assertEquals("12345678909", saved.getCpf());
        verify(clientRepository).save(client);
    }

    @Test
    void save_withDuplicateCpf_shouldThrowCpfUniqueViolationException() {
        when(clientRepository.save(any(Client.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(CpfUniqueViolationException.class,
                () -> clientService.save(client));
    }

    @Test
    void findById_withExistingId_shouldReturnClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Client found = clientService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("João da Silva", found.getName());
    }

    @Test
    void findById_withNonExistingId_shouldThrowEntityNotFoundException() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> clientService.findById(99L));
    }

    @Test
    void findByUserId_withExistingUserId_shouldReturnClient() {
        when(clientRepository.findByUserId(1L)).thenReturn(Optional.of(client));

        Client found = clientService.findByUserId(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void findByUserId_withNonExistingUserId_shouldThrowEntityNotFoundException() {
        when(clientRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> clientService.findByUserId(99L));
    }

    @Test
    void findByCpf_withExistingCpf_shouldReturnClient() {
        when(clientRepository.findByCpf("12345678909")).thenReturn(Optional.of(client));

        Client found = clientService.findByCpf("12345678909");

        assertNotNull(found);
        assertEquals("12345678909", found.getCpf());
    }

    @Test
    void findByCpf_withNonExistingCpf_shouldThrowEntityNotFoundException() {
        when(clientRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> clientService.findByCpf("00000000000"));
    }

    @Test
    void findAll_shouldReturnPagedClients() {
        Pageable pageable = Pageable.ofSize(10);
        ClientProjection projection = mock(ClientProjection.class);
        Page<ClientProjection> page = new PageImpl<>(List.of(projection));
        when(clientRepository.findAllBy(pageable)).thenReturn(page);

        Page<ClientProjection> result = clientService.findAll(pageable);

        assertEquals(1, result.getContent().size());
    }
}
