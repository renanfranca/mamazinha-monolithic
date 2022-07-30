package com.mamazinha.baby.service;

import com.mamazinha.baby.domain.BreastFeed;
import com.mamazinha.baby.repository.BreastFeedRepository;
import com.mamazinha.baby.service.dto.BreastFeedDTO;
import com.mamazinha.baby.service.mapper.BreastFeedMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BreastFeed}.
 */
@Service
@Transactional
public class BreastFeedService {

    private final Logger log = LoggerFactory.getLogger(BreastFeedService.class);

    private final BreastFeedRepository breastFeedRepository;

    private final BreastFeedMapper breastFeedMapper;

    public BreastFeedService(BreastFeedRepository breastFeedRepository, BreastFeedMapper breastFeedMapper) {
        this.breastFeedRepository = breastFeedRepository;
        this.breastFeedMapper = breastFeedMapper;
    }

    /**
     * Save a breastFeed.
     *
     * @param breastFeedDTO the entity to save.
     * @return the persisted entity.
     */
    public BreastFeedDTO save(BreastFeedDTO breastFeedDTO) {
        log.debug("Request to save BreastFeed : {}", breastFeedDTO);
        BreastFeed breastFeed = breastFeedMapper.toEntity(breastFeedDTO);
        breastFeed = breastFeedRepository.save(breastFeed);
        return breastFeedMapper.toDto(breastFeed);
    }

    /**
     * Update a breastFeed.
     *
     * @param breastFeedDTO the entity to save.
     * @return the persisted entity.
     */
    public BreastFeedDTO update(BreastFeedDTO breastFeedDTO) {
        log.debug("Request to save BreastFeed : {}", breastFeedDTO);
        BreastFeed breastFeed = breastFeedMapper.toEntity(breastFeedDTO);
        breastFeed = breastFeedRepository.save(breastFeed);
        return breastFeedMapper.toDto(breastFeed);
    }

    /**
     * Partially update a breastFeed.
     *
     * @param breastFeedDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BreastFeedDTO> partialUpdate(BreastFeedDTO breastFeedDTO) {
        log.debug("Request to partially update BreastFeed : {}", breastFeedDTO);

        return breastFeedRepository
            .findById(breastFeedDTO.getId())
            .map(existingBreastFeed -> {
                breastFeedMapper.partialUpdate(existingBreastFeed, breastFeedDTO);

                return existingBreastFeed;
            })
            .map(breastFeedRepository::save)
            .map(breastFeedMapper::toDto);
    }

    /**
     * Get all the breastFeeds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BreastFeedDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BreastFeeds");
        return breastFeedRepository.findAll(pageable).map(breastFeedMapper::toDto);
    }

    /**
     * Get all the breastFeeds with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BreastFeedDTO> findAllWithEagerRelationships(Pageable pageable) {
        return breastFeedRepository.findAllWithEagerRelationships(pageable).map(breastFeedMapper::toDto);
    }

    /**
     * Get one breastFeed by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BreastFeedDTO> findOne(Long id) {
        log.debug("Request to get BreastFeed : {}", id);
        return breastFeedRepository.findOneWithEagerRelationships(id).map(breastFeedMapper::toDto);
    }

    /**
     * Delete the breastFeed by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BreastFeed : {}", id);
        breastFeedRepository.deleteById(id);
    }
}
