package com.mobyeoldol.starcast.place.domain.repository;

import com.mobyeoldol.starcast.place.domain.Place;
import com.mobyeoldol.starcast.place.domain.enums.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {
    Optional<Place> findByPlaceUid(String placeUid);

    Optional<Place> findByAddress1AndAddress2AndAddress3AndAddress4(String address1, String address2, String address3, String address4);
    List<Place> findByType(PlaceType type);

    @Query("SELECT COUNT(c) FROM Community c WHERE c.place = :place")
    int countCommunitiesByPlace(@Param("place") Place place);

    @Query("SELECT p FROM Place p WHERE " +
            "p.address1 LIKE %:keyword% OR p.address2 LIKE %:keyword% OR p.address3 LIKE %:keyword% OR p.address4 LIKE %:keyword%")
    List<Place> findByAddressContaining(@Param("keyword") String keyword);

    Optional<Place> findByAddress1AndAddress2AndAddress3AndAddress4AndType(String address1, String address2, String address3, String address4, PlaceType type);
}
