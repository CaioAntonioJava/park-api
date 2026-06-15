package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.exception.CodeUniqueViolationException;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.caiohenrique.demo_park_api.enums.SpotStatus.LIVRE;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {
    private final ParkingSpotRepository parkingSpotRepository;

    @Transactional
    public ParkingSpot save(ParkingSpot parkingSpot) {
        try {
            return parkingSpotRepository.save(parkingSpot);
        } catch (DataIntegrityViolationException exception) {
            throw new CodeUniqueViolationException(String.format("""
                    Vaga com código { %s } já cadastrada.
                    """, parkingSpot.getSpotCode()));
        }
    }

    @Transactional(readOnly = true)
    public ParkingSpot findByCode(String spotCode) {
        return parkingSpotRepository.findBySpotCode(spotCode).orElseThrow(
                () -> new EntityNotFoundException(String.format("""
                        Vaga com código { %s } não foi encontrada.
                        """, spotCode))
        );
    }

    @Transactional(readOnly = true)
    public ParkingSpot findAvailableParkingSpot() {
        return parkingSpotRepository.findFirstBySpotStatus(LIVRE).orElseThrow(
                () -> new EntityNotFoundException("""
                        Nenhuma vaga livre foi encontrada
                        """)
        );
    }

    @Transactional(readOnly = true)
    public Page<ParkingSpot> findAll(Pageable pageable) {
        return parkingSpotRepository.findAll(pageable);
    }
}