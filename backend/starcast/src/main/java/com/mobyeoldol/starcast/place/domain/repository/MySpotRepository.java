package com.mobyeoldol.starcast.place.domain.repository;

import com.mobyeoldol.starcast.place.domain.MySpot;
import com.mobyeoldol.starcast.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MySpotRepository extends JpaRepository<MySpot, String> {

    @Query("SELECT place FROM MySpot WHERE profile.profileUid = :profileUid")
    Optional<Place> findByProfileIdAndSpotType(@Param("profileUid") String profileUid);
}
