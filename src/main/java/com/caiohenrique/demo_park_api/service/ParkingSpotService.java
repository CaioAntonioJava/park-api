package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.exception.CodeUniqueViolationException;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
