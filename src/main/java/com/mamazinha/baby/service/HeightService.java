package com.mamazinha.baby.service;

import com.mamazinha.baby.domain.Height;
import com.mamazinha.baby.repository.HeightRepository;
import com.mamazinha.baby.service.dto.HeightDTO;
import com.mamazinha.baby.service.mapper.HeightMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Height}.
 */
@Service
@Transactional
public class HeightService {

    private final Logger log = LoggerFactory.getLogger(HeightService.class);

    private final HeightRepository heightRepository;

    private final HeightMapper heightMapper;

    public HeightService(HeightRepository heightRepository, HeightMapper heightMapper) {
        this.heightRepository = heightRepository;
        this.heightMapper = heightMapper;
    }

    /**
     * Save a height.
     *
     * @param heightDTO the entity to save.
     * @return the persisted entity.
     */
    public HeightDTO save(HeightDTO heightDTO) {
        log.debug("Request to save Height : {}", heightDTO);
        Height height = heightMapper.toEntity(heightDTO);
        height = heightRepository.save(height);
        return heightMapper.toDto(height);
    }

    /**
     * Update a height.
     *
     * @param heightDTO the entity to save.
     * @return the persisted entity.
     */
    public HeightDTO update(HeightDTO heightDTO) {
        log.debug("Request to save Height : {}", heightDTO);
        Height height = heightMapper.toEntity(heightDTO);
        height = heightRepository.save(height);
        return heightMapper.toDto(height);
    }

    /**
     * Partially update a height.
     *
     * @param heightDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HeightDTO> partialUpdate(HeightDTO heightDTO) {
        log.debug("Request to partially update Height : {}", heightDTO);

        return heightRepository
            .findById(heightDTO.getId())
            .map(existingHeight -> {
                heightMapper.partialUpdate(existingHeight, heightDTO);

                return existingHeight;
            })
            .map(heightRepository::save)
            .map(heightMapper::toDto);
    }

    /**
     * Get all the heights.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HeightDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Heights");
        return heightRepository.findAll(pageable).map(heightMapper::toDto);
    }

    /**
     * Get all the heights with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<HeightDTO> findAllWithEagerRelationships(Pageable pageable) {
        return heightRepository.findAllWithEagerRelationships(pageable).map(heightMapper::toDto);
    }

    /**
     * Get one height by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HeightDTO> findOne(Long id) {
        log.debug("Request to get Height : {}", id);
        return heightRepository.findOneWithEagerRelationships(id).map(heightMapper::toDto);
    }

    /**
     * Delete the height by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Height : {}", id);
        heightRepository.deleteById(id);
    }
}
