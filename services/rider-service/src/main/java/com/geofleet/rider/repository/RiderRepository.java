package com.geofleet.rider.repository;

import com.geofleet.rider.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Rider, Long> {

    boolean existsByPhoneNumber(String phoneNumber);
}
