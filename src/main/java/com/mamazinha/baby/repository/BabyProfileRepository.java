package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.BabyProfile;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BabyProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BabyProfileRepository extends JpaRepository<BabyProfile, Long> {
    Page<BabyProfile> findByUserId(Pageable pageable, String userId);

    List<BabyProfile> findByUserIdAndIdNotIn(String userId, List<Long> id);
}
