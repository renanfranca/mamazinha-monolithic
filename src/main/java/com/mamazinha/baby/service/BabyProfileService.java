package com.mamazinha.baby.service;

import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.repository.BabyProfileRepository;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.service.mapper.BabyProfileMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BabyProfile}.
 */
@Service
@Transactional
public class BabyProfileService {

    private final Logger log = LoggerFactory.getLogger(BabyProfileService.class);

    private final BabyProfileRepository babyProfileRepository;

    private final BabyProfileMapper babyProfileMapper;

    public BabyProfileService(BabyProfileRepository babyProfileRepository, BabyProfileMapper babyProfileMapper) {
        this.babyProfileRepository = babyProfileRepository;
        this.babyProfileMapper = babyProfileMapper;
    }

    /**
     * Save a babyProfile.
     *
     * @param babyProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public BabyProfileDTO save(BabyProfileDTO babyProfileDTO) {
        log.debug("Request to save BabyProfile : {}", babyProfileDTO);
        BabyProfile babyProfile = babyProfileMapper.toEntity(babyProfileDTO);
        babyProfile = babyProfileRepository.save(babyProfile);
        return babyProfileMapper.toDto(babyProfile);
    }

    /**
     * Update a babyProfile.
     *
     * @param babyProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public BabyProfileDTO update(BabyProfileDTO babyProfileDTO) {
        log.debug("Request to save BabyProfile : {}", babyProfileDTO);
        BabyProfile babyProfile = babyProfileMapper.toEntity(babyProfileDTO);
        babyProfile = babyProfileRepository.save(babyProfile);
        return babyProfileMapper.toDto(babyProfile);
    }

    /**
     * Partially update a babyProfile.
     *
     * @param babyProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BabyProfileDTO> partialUpdate(BabyProfileDTO babyProfileDTO) {
        log.debug("Request to partially update BabyProfile : {}", babyProfileDTO);

        return babyProfileRepository
            .findById(babyProfileDTO.getId())
            .map(existingBabyProfile -> {
                babyProfileMapper.partialUpdate(existingBabyProfile, babyProfileDTO);

                return existingBabyProfile;
            })
            .map(babyProfileRepository::save)
            .map(babyProfileMapper::toDto);
    }

    /**
     * Get all the babyProfiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BabyProfileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BabyProfiles");
        return babyProfileRepository.findAll(pageable).map(babyProfileMapper::toDto);
    }

    /**
     * Get one babyProfile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BabyProfileDTO> findOne(Long id) {
        log.debug("Request to get BabyProfile : {}", id);
        return babyProfileRepository.findById(id).map(babyProfileMapper::toDto);
    }

    /**
     * Delete the babyProfile by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BabyProfile : {}", id);
        babyProfileRepository.deleteById(id);
    }
}
