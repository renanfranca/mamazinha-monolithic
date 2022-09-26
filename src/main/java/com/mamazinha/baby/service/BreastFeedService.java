package com.mamazinha.baby.service;

import com.mamazinha.baby.domain.BreastFeed;
import com.mamazinha.baby.repository.BreastFeedRepository;
import com.mamazinha.baby.security.AuthoritiesConstants;
import com.mamazinha.baby.security.SecurityUtils;
import com.mamazinha.baby.service.dto.BreastFeedDTO;
import com.mamazinha.baby.service.dto.BreastFeedLastCurrentWeekDTO;
import com.mamazinha.baby.service.dto.BreastFeedWeekDTO;
import com.mamazinha.baby.service.mapper.BreastFeedMapper;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
 * Service Implementation for managing {@link BreastFeed}.
 */
@Service
@Transactional
public class BreastFeedService {

    private final Logger log = LoggerFactory.getLogger(BreastFeedService.class);

    private final BreastFeedRepository breastFeedRepository;

    private final BreastFeedMapper breastFeedMapper;

    private final BabyProfileService babyProfileService;

    private final Clock clock;

    public BreastFeedService(
        BreastFeedRepository breastFeedRepository,
        BreastFeedMapper breastFeedMapper,
        BabyProfileService babyProfileService,
        Clock clock
    ) {
        this.breastFeedRepository = breastFeedRepository;
        this.breastFeedMapper = breastFeedMapper;
        this.babyProfileService = babyProfileService;
        this.clock = clock;
    }

    /**
     * Save a breastFeed.
     *
     * @param breastFeedDTO the entity to save.
     * @return the persisted entity.
     */
    public BreastFeedDTO save(BreastFeedDTO breastFeedDTO) {
        log.debug("Request to save BreastFeed : {}", breastFeedDTO);
        babyProfileService.verifyBabyProfileOwner(breastFeedDTO.getBabyProfile());
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
        babyProfileService.verifyBabyProfileOwner(breastFeedDTO.getBabyProfile());
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
                babyProfileService.verifyBabyProfileOwner(existingBreastFeed.getBabyProfile());
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
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return breastFeedRepository.findAllWithEagerRelationships(pageable).map(breastFeedMapper::toDto);
        }
        Optional<String> userId = SecurityUtils.getCurrentUserId();
        if (userId.isPresent()) {
            return breastFeedRepository.findAllByBabyProfileUserId(pageable, userId.get()).map(breastFeedMapper::toDto);
        }
        return Page.empty();
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
        Optional<BreastFeedDTO> breastFeedDTOOptional = breastFeedRepository.findOneWithEagerRelationships(id).map(breastFeedMapper::toDto);
        if (breastFeedDTOOptional.isPresent()) {
            babyProfileService.verifyBabyProfileOwner(breastFeedDTOOptional.get().getBabyProfile());
        }
        return breastFeedDTOOptional;
    }

    @Transactional(readOnly = true)
    public List<BreastFeedDTO> getAllTodayBrastFeedsByBabyProfile(Long id, String timeZone) {
        babyProfileService.verifyBabyProfileOwner(id);

        LocalDate nowLocalDate = LocalDate.now(clock);
        ZonedDateTime todayMidnight = ZonedDateTime.of(nowLocalDate.atStartOfDay(), ZoneId.systemDefault());
        ZonedDateTime tomorrowMidnight = ZonedDateTime.of(nowLocalDate.plusDays(1l).atStartOfDay(), ZoneId.systemDefault());
        if (timeZone != null) {
            nowLocalDate = LocalDate.now(clock.withZone(ZoneId.of(timeZone)));
            todayMidnight = ZonedDateTime.of(nowLocalDate.atStartOfDay(), ZoneId.of(timeZone));
            tomorrowMidnight = ZonedDateTime.of(nowLocalDate.plusDays(1l).atStartOfDay(), ZoneId.of(timeZone));
        }

        return breastFeedRepository
            .findAllByBabyProfileIdAndStartBetweenAndEndIsNotNull(id, todayMidnight, tomorrowMidnight)
            .stream()
            .map(breastFeedMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BreastFeedLastCurrentWeekDTO getLastWeekCurrentWeekAverageBreastFeedsHoursEachDayByBabyProfile(Long id, String timeZone) {
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

        BreastFeedLastCurrentWeekDTO breastFeedLastCurrentWeekDTO = new BreastFeedLastCurrentWeekDTO();
        breastFeedLastCurrentWeekDTO.currentWeekBreastFeeds(averageEachDayTotalBreastFeedsInHours(startOfWeek, endOfWeek, id, timeZone));
        breastFeedLastCurrentWeekDTO.lastWeekBreastFeeds(
            averageEachDayTotalBreastFeedsInHours(startOfLastWeek, endOfLastWeek, id, timeZone)
        );

        return breastFeedLastCurrentWeekDTO;
    }

    @Transactional(readOnly = true)
    public List<BreastFeedDTO> getAllIncompleteBreastFeedsByBabyProfile(Long id) {
        babyProfileService.verifyBabyProfileOwner(id);

        return breastFeedRepository
            .findAllByBabyProfileIdAndEndIsNullOrderByStartDesc(id)
            .stream()
            .map(breastFeedMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Delete the breastFeed by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BreastFeed : {}", id);
        Optional<BreastFeedDTO> breastFeedDTOOptional = breastFeedRepository.findById(id).map(breastFeedMapper::toDto);
        if (breastFeedDTOOptional.isPresent()) {
            babyProfileService.verifyBabyProfileOwner(breastFeedDTOOptional.get().getBabyProfile());
        }
        breastFeedRepository.deleteById(id);
    }

    private List<BreastFeedWeekDTO> averageEachDayTotalBreastFeedsInHours(
        LocalDate startDate,
        LocalDate endDate,
        Long babyProfileId,
        String timeZone
    ) {
        List<BreastFeedWeekDTO> napWeekDTOList = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            napWeekDTOList.add(
                new BreastFeedWeekDTO()
                    .dayOfWeek(currentDate.getDayOfWeek().getValue())
                    .averageFeedHours(averageTotalBreastFeedsInHoursByDate(babyProfileId, timeZone, currentDate))
            );
            currentDate = currentDate.plusDays(1);
        }
        return napWeekDTOList;
    }

    private Double averageTotalBreastFeedsInHoursByDate(Long babyProfileId, String timeZone, LocalDate localDate) {
        ZonedDateTime todayMidnight = ZonedDateTime.of(localDate.atStartOfDay(), ZoneId.systemDefault());
        ZonedDateTime tomorrowMidnight = ZonedDateTime.of(localDate.plusDays(1l).atStartOfDay(), ZoneId.systemDefault());
        if (timeZone != null) {
            todayMidnight = ZonedDateTime.of(localDate.atStartOfDay(), ZoneId.of(timeZone));
            tomorrowMidnight = ZonedDateTime.of(localDate.plusDays(1l).atStartOfDay(), ZoneId.of(timeZone));
        }

        List<BreastFeed> breastFeedList = breastFeedRepository.findAllByBabyProfileIdAndStartBetweenOrBabyProfileIdAndEndBetween(
            babyProfileId,
            todayMidnight,
            tomorrowMidnight,
            babyProfileId,
            todayMidnight,
            tomorrowMidnight
        );

        return averageTotalBreastFeedsInHoursByBreastFeedList(breastFeedList, todayMidnight, tomorrowMidnight);
    }

    private Double averageTotalBreastFeedsInHoursByBreastFeedList(
        List<BreastFeed> breastFeedList,
        ZonedDateTime todayMidnight,
        ZonedDateTime tomorrowMidnight
    ) {
        Double sumTotal =
            (
                breastFeedList
                    .stream()
                    .mapToDouble(breastFeed -> {
                        if (breastFeed.getEnd() == null) {
                            return 0;
                        }
                        if (
                            (breastFeed.getStart().isEqual(todayMidnight) || breastFeed.getStart().isAfter(todayMidnight)) &&
                            breastFeed.getStart().isBefore(tomorrowMidnight) &&
                            breastFeed.getEnd().isAfter(todayMidnight) &&
                            (breastFeed.getEnd().isEqual(tomorrowMidnight) || breastFeed.getEnd().isBefore(tomorrowMidnight))
                        ) {
                            return ChronoUnit.MINUTES.between(breastFeed.getStart(), breastFeed.getEnd());
                        }
                        if (
                            breastFeed.getStart().isBefore(todayMidnight) &&
                            breastFeed.getEnd().isAfter(todayMidnight) &&
                            breastFeed.getEnd().isBefore(tomorrowMidnight)
                        ) {
                            return ChronoUnit.MINUTES.between(todayMidnight, breastFeed.getEnd());
                        }
                        if (
                            breastFeed.getStart().isAfter(todayMidnight) &&
                            breastFeed.getStart().isBefore(tomorrowMidnight) &&
                            breastFeed.getEnd().isAfter(tomorrowMidnight)
                        ) {
                            return ChronoUnit.MINUTES.between(breastFeed.getStart(), tomorrowMidnight);
                        }

                        return 0;
                    })
                    .sum() /
                60
            );
        if (sumTotal > 0d) {
            Double average = sumTotal / breastFeedList.size();
            return ServiceUtils.maxOneDecimalPlaces(average);
        }
        return sumTotal;
    }
}
