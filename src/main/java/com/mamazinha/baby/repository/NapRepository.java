package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.Nap;
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
 * Spring Data SQL repository for the Nap entity.
 */
@Repository
public interface NapRepository extends JpaRepository<Nap, Long> {
    default Optional<Nap> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Nap> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Nap> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct nap from Nap nap left join fetch nap.babyProfile left join fetch nap.humor",
        countQuery = "select count(distinct nap) from Nap nap"
    )
    Page<Nap> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct nap from Nap nap left join fetch nap.babyProfile left join fetch nap.humor")
    List<Nap> findAllWithToOneRelationships();

    @Query("select nap from Nap nap left join fetch nap.babyProfile left join fetch nap.humor where nap.id =:id")
    Optional<Nap> findOneWithToOneRelationships(@Param("id") Long id);

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
