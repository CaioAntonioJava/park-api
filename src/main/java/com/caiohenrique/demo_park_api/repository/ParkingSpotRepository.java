package com.caiohenrique.demo_park_api.repository;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    Optional<ParkingSpot> findBySpotCode(String spotCode);

    Optional<ParkingSpot> findFirstBySpotStatusOrderBySpotCodeAsc(SpotStatus spotStatus);
}
