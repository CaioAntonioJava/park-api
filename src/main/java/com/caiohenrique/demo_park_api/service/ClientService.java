package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.exception.CpfUniqueViolationException;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.repository.ClientRepository;
import com.caiohenrique.demo_park_api.repository.projection.ClientProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    @Transactional
    public Client save(Client client) {

        try {
            return clientRepository.save(client);
        } catch (DataIntegrityViolationException exception) {
            throw new CpfUniqueViolationException(String.format("""
                    O CPF '%s' já está cadastrado no sistema.
                    """, client.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("""
                        Cliente ID { %d } não encontrado.
                        """, id))
        );
        return client;
    }

    @Transactional(readOnly = true)
    public Page<ClientProjection> findAll(Pageable pageable) {
        return clientRepository.findAllBy(pageable);
    }

    // ===== Retrieves the currently authenticated user by ID. =====
    @Transactional(readOnly = true)
    public Client findByUserId(Long id) {
        return clientRepository.findByUserId(id);
    }
}
