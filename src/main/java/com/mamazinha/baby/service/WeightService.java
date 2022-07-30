package com.mamazinha.baby.service;

import com.mamazinha.baby.domain.Weight;
import com.mamazinha.baby.repository.WeightRepository;
import com.mamazinha.baby.service.dto.WeightDTO;
import com.mamazinha.baby.service.mapper.WeightMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Weight}.
 */
@Service
@Transactional
public class WeightService {

    private final Logger log = LoggerFactory.getLogger(WeightService.class);

    private final WeightRepository weightRepository;

    private final WeightMapper weightMapper;

    public WeightService(WeightRepository weightRepository, WeightMapper weightMapper) {
        this.weightRepository = weightRepository;
        this.weightMapper = weightMapper;
    }

    /**
     * Save a weight.
     *
     * @param weightDTO the entity to save.
     * @return the persisted entity.
     */
    public WeightDTO save(WeightDTO weightDTO) {
        log.debug("Request to save Weight : {}", weightDTO);
        Weight weight = weightMapper.toEntity(weightDTO);
        weight = weightRepository.save(weight);
        return weightMapper.toDto(weight);
    }

    /**
     * Update a weight.
     *
     * @param weightDTO the entity to save.
     * @return the persisted entity.
     */
    public WeightDTO update(WeightDTO weightDTO) {
        log.debug("Request to save Weight : {}", weightDTO);
        Weight weight = weightMapper.toEntity(weightDTO);
        weight = weightRepository.save(weight);
        return weightMapper.toDto(weight);
    }

    /**
     * Partially update a weight.
     *
     * @param weightDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WeightDTO> partialUpdate(WeightDTO weightDTO) {
        log.debug("Request to partially update Weight : {}", weightDTO);

        return weightRepository
            .findById(weightDTO.getId())
            .map(existingWeight -> {
                weightMapper.partialUpdate(existingWeight, weightDTO);

                return existingWeight;
            })
            .map(weightRepository::save)
            .map(weightMapper::toDto);
    }

    /**
     * Get all the weights.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WeightDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Weights");
        return weightRepository.findAll(pageable).map(weightMapper::toDto);
    }

    /**
     * Get all the weights with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<WeightDTO> findAllWithEagerRelationships(Pageable pageable) {
        return weightRepository.findAllWithEagerRelationships(pageable).map(weightMapper::toDto);
    }

    /**
     * Get one weight by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WeightDTO> findOne(Long id) {
        log.debug("Request to get Weight : {}", id);
        return weightRepository.findOneWithEagerRelationships(id).map(weightMapper::toDto);
    }

    /**
     * Delete the weight by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Weight : {}", id);
        weightRepository.deleteById(id);
    }
}
