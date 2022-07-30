package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.HumorHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the HumorHistory entity.
 */
@Repository
public interface HumorHistoryRepository extends JpaRepository<HumorHistory, Long> {
    default Optional<HumorHistory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<HumorHistory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<HumorHistory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct humorHistory from HumorHistory humorHistory left join fetch humorHistory.babyProfile left join fetch humorHistory.humor",
        countQuery = "select count(distinct humorHistory) from HumorHistory humorHistory"
    )
    Page<HumorHistory> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct humorHistory from HumorHistory humorHistory left join fetch humorHistory.babyProfile left join fetch humorHistory.humor"
    )
    List<HumorHistory> findAllWithToOneRelationships();

    @Query(
        "select humorHistory from HumorHistory humorHistory left join fetch humorHistory.babyProfile left join fetch humorHistory.humor where humorHistory.id =:id"
    )
    Optional<HumorHistory> findOneWithToOneRelationships(@Param("id") Long id);
}
