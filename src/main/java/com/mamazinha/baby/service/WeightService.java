package com.mamazinha.baby.service;

import com.mamazinha.baby.domain.Weight;
import com.mamazinha.baby.repository.WeightRepository;
import com.mamazinha.baby.security.AuthoritiesConstants;
import com.mamazinha.baby.security.SecurityUtils;
import com.mamazinha.baby.service.dto.WeightDTO;
import com.mamazinha.baby.service.mapper.WeightMapper;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    private final BabyProfileService babyProfileService;

    private final Clock clock;

    public WeightService(WeightRepository weightRepository, WeightMapper weightMapper, BabyProfileService babyProfileService, Clock clock) {
        this.weightRepository = weightRepository;
        this.weightMapper = weightMapper;
        this.babyProfileService = babyProfileService;
        this.clock = clock;
    }

    /**
     * Save a weight.
     *
     * @param weightDTO the entity to save.
     * @return the persisted entity.
     */
    public WeightDTO save(WeightDTO weightDTO) {
        log.debug("Request to save Weight : {}", weightDTO);
        babyProfileService.verifyBabyProfileOwner(weightDTO.getBabyProfile());
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
                babyProfileService.verifyBabyProfileOwner(existingWeight.getBabyProfile());
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
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return weightRepository.findAll(pageable).map(weightMapper::toDto);
        }
        Optional<String> userId = SecurityUtils.getCurrentUserId();
        if (userId.isPresent()) {
            return weightRepository.findByBabyProfileUserId(pageable, userId.get()).map(weightMapper::toDto);
        }
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public Optional<WeightDTO> findLatestByBabyProfile(Long id) {
        babyProfileService.verifyBabyProfileOwner(id);
        Optional<String> userId = SecurityUtils.getCurrentUserId();
        if (userId.isPresent()) {
            return weightRepository.findFirstByBabyProfileIdOrderByDateDesc(id).map(weightMapper::toDto);
        }
        return Optional.ofNullable(new WeightDTO());
    }

    @Transactional(readOnly = true)
    public List<WeightDTO> findAllLastWeightsByDaysByBabyProfile(Long id, Integer days, String timeZone) {
        babyProfileService.verifyBabyProfileOwner(id);

        LocalDate nowLocalDate = LocalDate.now(clock);
        if (timeZone != null) {
            nowLocalDate = LocalDate.now(clock.withZone(ZoneId.of(timeZone)));
        }

        ZonedDateTime tomorrowMidnight = ZonedDateTime.of(nowLocalDate.plusDays(1l).atStartOfDay(), ZoneId.systemDefault());
        ZonedDateTime daysAgo = ZonedDateTime.of(nowLocalDate.minusDays(days + 1l).atStartOfDay(), ZoneId.systemDefault());
        if (timeZone != null) {
            tomorrowMidnight = ZonedDateTime.of(nowLocalDate.plusDays(1l).atStartOfDay(), ZoneId.of(timeZone));
            daysAgo = ZonedDateTime.of(nowLocalDate.minusDays(days + 1l).atStartOfDay(), ZoneId.of(timeZone));
        }

        return weightRepository
            .findAllByBabyProfileIdAndDateBetweenOrderByDateAsc(id, daysAgo, tomorrowMidnight)
            .stream()
            .map(weightMapper::toDto)
            .collect(Collectors.toList());
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
        Optional<WeightDTO> weightDTOOptional = weightRepository.findById(id).map(weightMapper::toDto);
        if (weightDTOOptional.isPresent()) {
            babyProfileService.verifyBabyProfileOwner(weightDTOOptional.get().getBabyProfile());
        }
        return weightDTOOptional;
    }

    /**
     * Delete the weight by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Weight : {}", id);
        Optional<WeightDTO> weightDTOOptional = weightRepository.findById(id).map(weightMapper::toDto);
        if (weightDTOOptional.isPresent()) {
            babyProfileService.verifyBabyProfileOwner(weightDTOOptional.get().getBabyProfile());
        }
        weightRepository.deleteById(id);
    }
}
