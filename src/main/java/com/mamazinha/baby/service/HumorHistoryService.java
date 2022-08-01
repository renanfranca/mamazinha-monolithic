package com.mamazinha.baby.service;

import com.mamazinha.baby.domain.HumorHistory;
import com.mamazinha.baby.repository.HumorHistoryRepository;
import com.mamazinha.baby.security.AuthoritiesConstants;
import com.mamazinha.baby.security.SecurityUtils;
import com.mamazinha.baby.service.dto.HumorAverageDTO;
import com.mamazinha.baby.service.dto.HumorAverageLastCurrentWeekDTO;
import com.mamazinha.baby.service.dto.HumorHistoryDTO;
import com.mamazinha.baby.service.mapper.HumorHistoryMapper;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private final Clock clock;

    private final BabyProfileService babyProfileService;

    public HumorHistoryService(
        HumorHistoryRepository humorHistoryRepository,
        HumorHistoryMapper humorHistoryMapper,
        Clock clock,
        BabyProfileService babyProfileService
    ) {
        this.humorHistoryRepository = humorHistoryRepository;
        this.humorHistoryMapper = humorHistoryMapper;
        this.clock = clock;
        this.babyProfileService = babyProfileService;
    }

    /**
     * Save a humorHistory.
     *
     * @param humorHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public HumorHistoryDTO save(HumorHistoryDTO humorHistoryDTO) {
        log.debug("Request to save HumorHistory : {}", humorHistoryDTO);
        babyProfileService.verifyBabyProfileOwner(humorHistoryDTO.getBabyProfile());
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
                babyProfileService.verifyBabyProfileOwner(existingHumorHistory.getBabyProfile());
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
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return humorHistoryRepository.findAll(pageable).map(humorHistoryMapper::toDto);
        }
        Optional<String> userId = SecurityUtils.getCurrentUserId();
        if (userId.isPresent()) {
            return humorHistoryRepository.findByBabyProfileUserId(pageable, userId.get()).map(humorHistoryMapper::toDto);
        }
        return Page.empty();
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
        Optional<HumorHistoryDTO> humorHistoryDTOOptional = humorHistoryRepository.findById(id).map(humorHistoryMapper::toDto);
        if (humorHistoryDTOOptional.isPresent()) {
            babyProfileService.verifyBabyProfileOwner(humorHistoryDTOOptional.get().getBabyProfile());
        }
        return humorHistoryDTOOptional;
    }

    @Transactional(readOnly = true)
    public HumorAverageDTO getTodayAverageHumorHistoryByBabyProfile(Long id, String timeZone) {
        babyProfileService.verifyBabyProfileOwner(id);

        LocalDate nowLocalDate = LocalDate.now(clock);
        if (timeZone != null) {
            nowLocalDate = LocalDate.now(clock.withZone(ZoneId.of(timeZone)));
        }

        return averageHumorHistoryByDate(id, timeZone, nowLocalDate);
    }

    @Transactional(readOnly = true)
    public HumorAverageLastCurrentWeekDTO getLastCurrentWeekAverageHumorHistoryByBabyProfile(Long id, String timeZone) {
        babyProfileService.verifyBabyProfileOwner(id);

        LocalDate nowLocalDate = LocalDate.now(clock);
        if (timeZone != null) {
            nowLocalDate = LocalDate.now(clock.withZone(ZoneId.of(timeZone)));
        }
        // Get first day of week
        LocalDate startOfWeek = nowLocalDate.with(DayOfWeek.MONDAY);
        // Get last day of week
        LocalDate endOfWeek = nowLocalDate.with(DayOfWeek.SUNDAY);
        // Get first day of last week
        LocalDate startOfLastWeek = startOfWeek.minusDays(1).with(DayOfWeek.MONDAY);
        // Get last day of last week
        LocalDate endOfLastWeek = startOfLastWeek.with(DayOfWeek.SUNDAY);

        HumorAverageLastCurrentWeekDTO humorAverageLastCurrentWeekDTO = new HumorAverageLastCurrentWeekDTO();
        humorAverageLastCurrentWeekDTO.currentWeekHumorAverage(averageNapsHumorEachDay(startOfWeek, endOfWeek, id, timeZone));
        humorAverageLastCurrentWeekDTO.lastWeekHumorAverage(averageNapsHumorEachDay(startOfLastWeek, endOfLastWeek, id, timeZone));
        return humorAverageLastCurrentWeekDTO;
    }

    private List<HumorAverageDTO> averageNapsHumorEachDay(LocalDate startDate, LocalDate endDate, Long babyProfileId, String timeZone) {
        List<HumorAverageDTO> humorAverageDTOList = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            humorAverageDTOList.add(averageHumorHistoryByDate(babyProfileId, timeZone, currentDate));
            currentDate = currentDate.plusDays(1);
        }
        return humorAverageDTOList;
    }

    private HumorAverageDTO averageHumorHistoryByDate(Long id, String timeZone, LocalDate nowLocalDate) {
        ZonedDateTime todayMidnight = ZonedDateTime.of(nowLocalDate.atStartOfDay(), ZoneId.systemDefault());
        ZonedDateTime tomorrowMidnight = ZonedDateTime.of(nowLocalDate.plusDays(1l).atStartOfDay(), ZoneId.systemDefault());
        if (timeZone != null) {
            todayMidnight = ZonedDateTime.of(nowLocalDate.atStartOfDay(), ZoneId.of(timeZone));
            tomorrowMidnight = ZonedDateTime.of(nowLocalDate.plusDays(1l).atStartOfDay(), ZoneId.of(timeZone));
        }

        List<HumorHistory> humorHistoryList = humorHistoryRepository.findByBabyProfileIdAndDateGreaterThanEqualAndDateLessThanAndHumorNotNull(
            id,
            todayMidnight,
            tomorrowMidnight
        );

        if (humorHistoryList.isEmpty()) {
            return new HumorAverageDTO().dayOfWeek(nowLocalDate.getDayOfWeek().getValue()).humorAverage(0);
        }
        return new HumorAverageDTO()
            .dayOfWeek(nowLocalDate.getDayOfWeek().getValue())
            .humorAverage(
                humorHistoryList.stream().mapToInt(humorHistory -> humorHistory.getHumor().getValue()).sum() / humorHistoryList.size()
            );
    }

    /**
     * Delete the humorHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete HumorHistory : {}", id);
        Optional<HumorHistoryDTO> humorHistoryDTOOptional = humorHistoryRepository.findById(id).map(humorHistoryMapper::toDto);
        if (humorHistoryDTOOptional.isPresent()) {
            babyProfileService.verifyBabyProfileOwner(humorHistoryDTOOptional.get().getBabyProfile());
        }
        humorHistoryRepository.deleteById(id);
    }
}
