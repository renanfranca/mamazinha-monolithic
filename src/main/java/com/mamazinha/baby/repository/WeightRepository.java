package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.Weight;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Weight entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WeightRepository extends JpaRepository<Weight, Long> {
    Page<Weight> findByBabyProfileUserId(Pageable pageable, String string);

    boolean existsByBabyProfileId(Long id);

    boolean existsByBabyProfileIdAndBabyProfileUserId(Long id, String userId);

    Optional<Weight> findFirstByBabyProfileIdOrderByDateDesc(Long id);

    List<Weight> findAllByBabyProfileIdAndDateBetweenOrderByDateAsc(Long id, ZonedDateTime tomorrowMidnight, ZonedDateTime daysAgo);
}
