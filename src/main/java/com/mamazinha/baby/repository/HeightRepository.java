package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.Height;
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
 * Spring Data SQL repository for the Height entity.
 */
@Repository
public interface HeightRepository extends JpaRepository<Height, Long> {
    default Optional<Height> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Height> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Height> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct height from Height height left join fetch height.babyProfile",
        countQuery = "select count(distinct height) from Height height"
    )
    Page<Height> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct height from Height height left join fetch height.babyProfile")
    List<Height> findAllWithToOneRelationships();

    @Query("select height from Height height left join fetch height.babyProfile where height.id =:id")
    Optional<Height> findOneWithToOneRelationships(@Param("id") Long id);

    Page<Height> findByBabyProfileUserId(Pageable pageable, String string);

    Optional<Height> findFirstByBabyProfileIdOrderByDateDesc(Long id);

    List<Height> findAllByBabyProfileIdAndDateBetweenOrderByDateAsc(Long id, ZonedDateTime daysAgo, ZonedDateTime tomorrowMidnight);
}
