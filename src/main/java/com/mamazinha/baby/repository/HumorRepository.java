package com.mamazinha.baby.repository;

import com.mamazinha.baby.domain.Humor;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Humor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HumorRepository extends JpaRepository<Humor, Long> {}
