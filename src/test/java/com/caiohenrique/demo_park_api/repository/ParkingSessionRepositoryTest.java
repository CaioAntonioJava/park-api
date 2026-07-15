package com.caiohenrique.demo_park_api.repository;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import com.caiohenrique.demo_park_api.repository.projection.ParkingSessionProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ParkingSessionRepositoryTest {

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    private Client client;
    private ParkingSpot parkingSpot;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("session@email.com.br");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.ROLE_CLIENT);
        user = userRepository.save(user);

        client = new Client();
        client.setName("Cliente Sessão");
        client.setCpf("12345678909");
        client.setUser(user);
        client = clientRepository.save(client);

        parkingSpot = new ParkingSpot();
        parkingSpot.setSpotCode("A001");
        parkingSpot.setSpotStatus(SpotStatus.LIVRE);
        parkingSpot = parkingSpotRepository.save(parkingSpot);
    }

    @Test
    void save_shouldPersistParkingSession() {
        ParkingSession session = createSession("BRA1234");

        ParkingSession saved = parkingSessionRepository.save(session);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("BRA1234", saved.getLicensePlate());
        assertNotNull(saved.getReceiptNumber());
    }

    @Test
    void findByReceiptNumberAndCheckOutIsNull_withOpenSession_shouldReturnSession() {
        ParkingSession session = createSession("BRA1234");
        session.setCheckOut(null);
        parkingSessionRepository.save(session);

        Optional<ParkingSession> found = parkingSessionRepository
                .findByReceiptNumberAndCheckOutIsNull(session.getReceiptNumber());

        assertTrue(found.isPresent());
        assertNull(found.get().getCheckOut());
    }

    @Test
    void findByReceiptNumberAndCheckOutIsNull_withCompletedSession_shouldReturnEmpty() {
        ParkingSession session = createSession("BRA1234");
        session.setCheckOut(LocalDateTime.now());
        parkingSessionRepository.save(session);

        Optional<ParkingSession> found = parkingSessionRepository
                .findByReceiptNumberAndCheckOutIsNull(session.getReceiptNumber());

        assertFalse(found.isPresent());
    }

    @Test
    void countByClientCpfAndCheckOutIsNotNull_shouldReturnCount() {
        ParkingSession session1 = createSession("BRA1234");
        session1.setCheckOut(LocalDateTime.now());
        parkingSessionRepository.save(session1);

        ParkingSession session2 = createSession("BRA5678");
        session2.setCheckOut(LocalDateTime.now());
        parkingSessionRepository.save(session2);

        long count = parkingSessionRepository.countByClientCpfAndCheckOutIsNotNull("12345678909");

        assertEquals(2, count);
    }

    @Test
    void existsByLicensePlateAndCheckOutIsNull_withActiveSession_shouldReturnTrue() {
        ParkingSession session = createSession("BRA1234");
        session.setCheckOut(null);
        parkingSessionRepository.save(session);

        boolean exists = parkingSessionRepository.existsByLicensePlateAndCheckOutIsNull("BRA1234");

        assertTrue(exists);
    }

    @Test
    void existsByLicensePlateAndCheckOutIsNull_withNoActiveSession_shouldReturnFalse() {
        boolean exists = parkingSessionRepository.existsByLicensePlateAndCheckOutIsNull("NONEXIST");

        assertFalse(exists);
    }

    @Test
    void findAllByClientCpf_shouldReturnProjections() {
        ParkingSession session = createSession("BRA1234");
        session.setCheckOut(LocalDateTime.now());
        session.setParkingFee(BigDecimal.valueOf(18.00));
        session.setDiscount(BigDecimal.valueOf(5.40));
        parkingSessionRepository.save(session);

        Page<ParkingSessionProjection> projections = parkingSessionRepository
                .findAllByClientCpf("12345678909", PageRequest.of(0, 10));

        assertTrue(projections.getTotalElements() >= 1);
        ParkingSessionProjection projection = projections.getContent().get(0);
        assertEquals("BRA1234", projection.getLicensePlate());
        assertEquals("12345678909", projection.getClientCpf());
        assertEquals("A001", projection.getSpotCode());
    }

    @Test
    void findAllByClientUserId_shouldReturnProjections() {
        ParkingSession session = createSession("BRA1234");
        session.setCheckOut(LocalDateTime.now());
        parkingSessionRepository.save(session);

        Page<ParkingSessionProjection> projections = parkingSessionRepository
                .findAllByClientUserId(client.getUser().getId(), PageRequest.of(0, 10));

        assertTrue(projections.getTotalElements() >= 1);
    }

    private ParkingSession createSession(String licensePlate) {
        ParkingSession session = new ParkingSession();
        session.setReceiptNumber("REC-" + System.currentTimeMillis() + "-" + licensePlate);
        session.setLicensePlate(licensePlate);
        session.setBrand("Toyota");
        session.setModel("Corolla");
        session.setColor("Prata");
        session.setCheckIn(LocalDateTime.now());
        session.setClient(client);
        session.setParkingSpot(parkingSpot);
        return session;
    }
}
