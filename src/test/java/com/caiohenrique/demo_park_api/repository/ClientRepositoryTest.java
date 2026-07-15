package com.caiohenrique.demo_park_api.repository;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.repository.projection.ClientProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("client@email.com.br");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.ROLE_CLIENT);
        user = userRepository.save(user);
    }

    @Test
    void save_shouldPersistClient() {
        Client client = new Client();
        client.setName("João da Silva");
        client.setCpf("12345678909");
        client.setUser(user);

        Client saved = clientRepository.save(client);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("João da Silva", saved.getName());
        assertEquals("12345678909", saved.getCpf());
    }

    @Test
    void findByUserId_shouldReturnClient() {
        Client client = new Client();
        client.setName("Maria Souza");
        client.setCpf("98765432100");
        client.setUser(user);
        clientRepository.save(client);

        Optional<Client> found = clientRepository.findByUserId(user.getId());

        assertTrue(found.isPresent());
        assertEquals("Maria Souza", found.get().getName());
    }

    @Test
    void findByUserId_withNonExistingUser_shouldReturnEmpty() {
        Optional<Client> found = clientRepository.findByUserId(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findByCpf_shouldReturnClient() {
        Client client = new Client();
        client.setName("Carlos Pereira");
        client.setCpf("11122233344");
        client.setUser(user);
        clientRepository.save(client);

        Optional<Client> found = clientRepository.findByCpf("11122233344");

        assertTrue(found.isPresent());
        assertEquals("Carlos Pereira", found.get().getName());
    }

    @Test
    void findByCpf_withNonExistingCpf_shouldReturnEmpty() {
        Optional<Client> found = clientRepository.findByCpf("00000000000");

        assertFalse(found.isPresent());
    }

    @Test
    void findAllBy_shouldReturnProjection() {
        Client client = new Client();
        client.setName("Ana Costa");
        client.setCpf("55566677788");
        client.setUser(user);
        clientRepository.save(client);

        Page<ClientProjection> projections = clientRepository.findAllBy(PageRequest.of(0, 10));

        assertTrue(projections.getTotalElements() >= 1);
        ClientProjection projection = projections.getContent().get(0);
        assertNotNull(projection.getId());
        assertNotNull(projection.getName());
        assertNotNull(projection.getCpf());
    }

    @Test
    void save_withDuplicateCpf_shouldThrowException() {
        Client client1 = new Client();
        client1.setName("First Client");
        client1.setCpf("99988877766");
        client1.setUser(user);
        clientRepository.save(client1);

        Client client2 = new Client();
        client2.setName("Second Client");
        client2.setCpf("99988877766");
        client2.setUser(user);

        assertThrows(Exception.class, () -> clientRepository.save(client2));
    }
}
