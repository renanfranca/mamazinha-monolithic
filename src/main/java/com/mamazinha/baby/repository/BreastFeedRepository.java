package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.BreastFeed;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BreastFeed entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BreastFeedRepository extends JpaRepository<BreastFeed, Long> {
    Page<BreastFeed> findAllByBabyProfileUserId(Pageable pageable, String string);

    List<BreastFeed> findAllByBabyProfileIdAndEndIsNullOrderByStartDesc(Long id);

    List<BreastFeed> findAllByBabyProfileIdAndStartBetweenAndEndIsNotNull(
        Long babyProfileId,
        ZonedDateTime todayMidnight,
        ZonedDateTime tomorrowMidnight
    );

    List<BreastFeed> findAllByBabyProfileIdAndStartBetweenOrBabyProfileIdAndEndBetween(
        Long babyProfileId,
        ZonedDateTime todayMidnight,
        ZonedDateTime tomorrowMidnight,
        Long babyProfileId2,
        ZonedDateTime todayMidnight2,
        ZonedDateTime tomorrowMidnight2
    );
}
