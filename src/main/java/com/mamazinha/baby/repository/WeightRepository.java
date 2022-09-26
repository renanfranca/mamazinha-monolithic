package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.Weight;
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
 * Spring Data SQL repository for the Weight entity.
 */
@Repository
public interface WeightRepository extends JpaRepository<Weight, Long> {
    default Optional<Weight> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Weight> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Weight> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct weight from Weight weight left join fetch weight.babyProfile",
        countQuery = "select count(distinct weight) from Weight weight"
    )
    Page<Weight> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct weight from Weight weight left join fetch weight.babyProfile")
    List<Weight> findAllWithToOneRelationships();

    @Query("select weight from Weight weight left join fetch weight.babyProfile where weight.id =:id")
    Optional<Weight> findOneWithToOneRelationships(@Param("id") Long id);

    Page<Weight> findByBabyProfileUserId(Pageable pageable, String string);

    boolean existsByBabyProfileId(Long id);

    boolean existsByBabyProfileIdAndBabyProfileUserId(Long id, String userId);

    Optional<Weight> findFirstByBabyProfileIdOrderByDateDesc(Long id);

    List<Weight> findAllByBabyProfileIdAndDateBetweenOrderByDateAsc(Long id, ZonedDateTime tomorrowMidnight, ZonedDateTime daysAgo);
}
