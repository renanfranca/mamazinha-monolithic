package com.mamazinha.baby.service;

import com.mamazinha.baby.domain.Humor;
import com.mamazinha.baby.repository.HumorRepository;
import com.mamazinha.baby.service.dto.HumorDTO;
import com.mamazinha.baby.service.mapper.HumorMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Humor}.
 */
@Service
@Transactional
public class HumorService {

    private final Logger log = LoggerFactory.getLogger(HumorService.class);

    private final HumorRepository humorRepository;

    private final HumorMapper humorMapper;

    public HumorService(HumorRepository humorRepository, HumorMapper humorMapper) {
        this.humorRepository = humorRepository;
        this.humorMapper = humorMapper;
    }

    /**
     * Save a humor.
     *
     * @param humorDTO the entity to save.
     * @return the persisted entity.
     */
    public HumorDTO save(HumorDTO humorDTO) {
        log.debug("Request to save Humor : {}", humorDTO);
        Humor humor = humorMapper.toEntity(humorDTO);
        humor = humorRepository.save(humor);
        return humorMapper.toDto(humor);
    }

    /**
     * Partially update a humor.
     *
     * @param humorDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HumorDTO> partialUpdate(HumorDTO humorDTO) {
        log.debug("Request to partially update Humor : {}", humorDTO);

        return humorRepository
            .findById(humorDTO.getId())
            .map(existingHumor -> {
                humorMapper.partialUpdate(existingHumor, humorDTO);

                return existingHumor;
            })
            .map(humorRepository::save)
            .map(humorMapper::toDto);
    }

    /**
     * Get all the humors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HumorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Humors");
        return humorRepository.findAll(pageable).map(humorMapper::toDto);
    }

    /**
     * Get one humor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HumorDTO> findOne(Long id) {
        log.debug("Request to get Humor : {}", id);
        return humorRepository.findById(id).map(humorMapper::toDto);
    }

    /**
     * Delete the humor by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Humor : {}", id);
        humorRepository.deleteById(id);
    }
}
