package com.caiohenrique.demo_park_api.repository;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.repository.projection.ClientProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Page<ClientProjection> findAllBy(Pageable pageable);

    Client findByUserId(Long id);

}
