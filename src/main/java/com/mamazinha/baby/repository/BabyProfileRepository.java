package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.BabyProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BabyProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BabyProfileRepository extends JpaRepository<BabyProfile, Long> {}
