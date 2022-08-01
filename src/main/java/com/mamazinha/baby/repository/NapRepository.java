package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.Nap;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Nap entity.
 */
// @SuppressWarnings("unused")
@Repository
public interface NapRepository extends JpaRepository<Nap, Long> {
    Page<Nap> findByBabyProfileUserId(Pageable pageable, String string);

    List<Nap> findByBabyProfileIdAndStartBetweenOrEndBetween(
        Long id,
        ZonedDateTime todayMidnight,
        ZonedDateTime tomorrowMidnight,
        ZonedDateTime todayMidnight2,
        ZonedDateTime tomorrowMidnight2
    );

    List<Nap> findByBabyProfileIdAndStartBetweenOrBabyProfileIdAndEndBetween(
        Long id,
        ZonedDateTime todayMidnight,
        ZonedDateTime tomorrowMidnight,
        Long id2,
        ZonedDateTime todayMidnight2,
        ZonedDateTime tomorrowMidnight2
    );

    List<Nap> findByBabyProfileIdAndStartGreaterThanEqualAndEndLessThan(
        Long id,
        ZonedDateTime todayMidnight,
        ZonedDateTime tomorrowMidnight
    );

    List<Nap> findByBabyProfileIdAndStartGreaterThanEqualAndEndLessThanAndHumorNotNull(
        Long id,
        ZonedDateTime todayMidnight,
        ZonedDateTime tomorrowMidnight
    );

    List<Nap> findByBabyProfileIdAndStartGreaterThanEqualAndEndLessThanAndPlaceNotNull(
        Long id,
        ZonedDateTime todayMidnight,
        ZonedDateTime tomorrowMidnight
    );

    List<Nap> findByBabyProfileIdAndEndIsNullOrderByStartDesc(Long id);
}
