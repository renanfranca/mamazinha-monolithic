package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.BreastFeed;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BreastFeed entity.
 */
@Repository
public interface BreastFeedRepository extends JpaRepository<BreastFeed, Long> {
    default Optional<BreastFeed> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BreastFeed> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BreastFeed> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct breastFeed from BreastFeed breastFeed left join fetch breastFeed.babyProfile",
        countQuery = "select count(distinct breastFeed) from BreastFeed breastFeed"
    )
    Page<BreastFeed> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct breastFeed from BreastFeed breastFeed left join fetch breastFeed.babyProfile")
    List<BreastFeed> findAllWithToOneRelationships();

    @Query("select breastFeed from BreastFeed breastFeed left join fetch breastFeed.babyProfile where breastFeed.id =:id")
    Optional<BreastFeed> findOneWithToOneRelationships(@Param("id") Long id);

    Page<BreastFeed> findAllByBabyProfileUserId(Pageable pageable, String string);

    List<BreastFeed> findAllByBabyProfileIdAndEndIsNullOrderByStartDesc(Long id);

    List<BreastFeed> findAllByBabyProfileIdAndStartBetweenAndEndIsNotNullOrderByStart(
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
