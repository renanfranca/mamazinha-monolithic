package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.BreastFeed;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
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
}
