package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.Height;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Height entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HeightRepository extends JpaRepository<Height, Long> {
    Page<Height> findByBabyProfileUserId(Pageable pageable, String string);

    Optional<Height> findFirstByBabyProfileIdOrderByDateDesc(Long id);

    List<Height> findAllByBabyProfileIdAndDateBetweenOrderByDateAsc(Long id, ZonedDateTime daysAgo, ZonedDateTime tomorrowMidnight);
}
