package com.mamazinha.baby.service;

import com.mamazinha.baby.domain.HumorHistory;
import com.mamazinha.baby.repository.HumorHistoryRepository;
import com.mamazinha.baby.service.dto.HumorHistoryDTO;
import com.mamazinha.baby.service.mapper.HumorHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link HumorHistory}.
 */
@Service
@Transactional
public class HumorHistoryService {

    private final Logger log = LoggerFactory.getLogger(HumorHistoryService.class);

    private final HumorHistoryRepository humorHistoryRepository;

    private final HumorHistoryMapper humorHistoryMapper;

    public HumorHistoryService(HumorHistoryRepository humorHistoryRepository, HumorHistoryMapper humorHistoryMapper) {
        this.humorHistoryRepository = humorHistoryRepository;
        this.humorHistoryMapper = humorHistoryMapper;
    }

    /**
     * Save a humorHistory.
     *
     * @param humorHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public HumorHistoryDTO save(HumorHistoryDTO humorHistoryDTO) {
        log.debug("Request to save HumorHistory : {}", humorHistoryDTO);
        HumorHistory humorHistory = humorHistoryMapper.toEntity(humorHistoryDTO);
        humorHistory = humorHistoryRepository.save(humorHistory);
        return humorHistoryMapper.toDto(humorHistory);
    }

    /**
     * Update a humorHistory.
     *
     * @param humorHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public HumorHistoryDTO update(HumorHistoryDTO humorHistoryDTO) {
        log.debug("Request to save HumorHistory : {}", humorHistoryDTO);
        HumorHistory humorHistory = humorHistoryMapper.toEntity(humorHistoryDTO);
        humorHistory = humorHistoryRepository.save(humorHistory);
        return humorHistoryMapper.toDto(humorHistory);
    }

    /**
     * Partially update a humorHistory.
     *
     * @param humorHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HumorHistoryDTO> partialUpdate(HumorHistoryDTO humorHistoryDTO) {
        log.debug("Request to partially update HumorHistory : {}", humorHistoryDTO);

        return humorHistoryRepository
            .findById(humorHistoryDTO.getId())
            .map(existingHumorHistory -> {
                humorHistoryMapper.partialUpdate(existingHumorHistory, humorHistoryDTO);

                return existingHumorHistory;
            })
            .map(humorHistoryRepository::save)
            .map(humorHistoryMapper::toDto);
    }

    /**
     * Get all the humorHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HumorHistoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all HumorHistories");
        return humorHistoryRepository.findAll(pageable).map(humorHistoryMapper::toDto);
    }

    /**
     * Get all the humorHistories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<HumorHistoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return humorHistoryRepository.findAllWithEagerRelationships(pageable).map(humorHistoryMapper::toDto);
    }

    /**
     * Get one humorHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HumorHistoryDTO> findOne(Long id) {
        log.debug("Request to get HumorHistory : {}", id);
        return humorHistoryRepository.findOneWithEagerRelationships(id).map(humorHistoryMapper::toDto);
    }

    /**
     * Delete the humorHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete HumorHistory : {}", id);
        humorHistoryRepository.deleteById(id);
    }
}
