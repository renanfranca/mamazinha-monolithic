package com.mamazinha.baby.service;

import com.mamazinha.baby.config.Constants;
import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.repository.BabyProfileRepository;
import com.mamazinha.baby.security.AuthoritiesConstants;
import com.mamazinha.baby.security.SecurityUtils;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.service.mapper.BabyProfileMapper;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BabyProfile}.
 */
@Service
@Transactional
public class BabyProfileService {

    private static final int MAX_BABY_PROFILE_ALLOWED_FOR_USER_ROLE_AUTHORITY = 1;

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
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            Optional<String> userId = SecurityUtils.getCurrentUserId();
            if (userId.isPresent()) {
                if (
                    babyProfileRepository.findByUserId(Pageable.unpaged(), userId.get()).getTotalElements() >=
                    MAX_BABY_PROFILE_ALLOWED_FOR_USER_ROLE_AUTHORITY &&
                    babyProfileDTO.getId() == null
                ) {
                    throw new AccessDeniedException(Constants.REACH_MAX_BABY_PROFILE_ALLOWED_FOR_USER_ROLE_AUTHORITY);
                }
                babyProfile.userId(userId.get());
            }
        }
        babyProfile = babyProfileRepository.save(babyProfile);
        if (Boolean.TRUE.equals(babyProfile.getMain())) {
            updateOthersBabyProfileFromSameUserToMainFalse(babyProfile.getUserId(), babyProfile.getId());
        }
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
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            Optional<String> userId = SecurityUtils.getCurrentUserId();
            if (userId.isPresent()) {
                if (
                    babyProfileRepository.findByUserId(Pageable.unpaged(), userId.get()).getTotalElements() >=
                    MAX_BABY_PROFILE_ALLOWED_FOR_USER_ROLE_AUTHORITY &&
                    babyProfileDTO.getId() == null
                ) {
                    throw new AccessDeniedException(Constants.REACH_MAX_BABY_PROFILE_ALLOWED_FOR_USER_ROLE_AUTHORITY);
                }
                babyProfile.userId(userId.get());
            }
        }
        babyProfile = babyProfileRepository.save(babyProfile);
        if (Boolean.TRUE.equals(babyProfile.getMain())) {
            updateOthersBabyProfileFromSameUserToMainFalse(babyProfile.getUserId(), babyProfile.getId());
        }
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
                verifyAuthorizedOperation(existingBabyProfile.getUserId());
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
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return babyProfileRepository.findAll(pageable).map(babyProfileMapper::toDto);
        }
        Optional<String> userId = SecurityUtils.getCurrentUserId();
        if (userId.isPresent()) {
            return babyProfileRepository.findByUserId(pageable, userId.get()).map(babyProfileMapper::toDto);
        }
        return Page.empty();
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
        Optional<BabyProfileDTO> babyProfileOptional = babyProfileRepository.findById(id).map(babyProfileMapper::toDto);
        if (babyProfileOptional.isPresent()) {
            verifyAuthorizedOperation(babyProfileOptional.get().getUserId());
        }
        return babyProfileOptional;
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

    public void verifyBabyProfileOwner(BabyProfileDTO babyProfileDTO) {
        verifyBabyProfileOwner(babyProfileDTO == null ? null : babyProfileDTO.getId());
    }

    public void verifyBabyProfileOwner(BabyProfile babyProfile) {
        verifyBabyProfileOwner(babyProfile == null ? null : babyProfile.getId());
    }

    public void verifyBabyProfileOwner(Long babyProfileId) {
        if (babyProfileId == null) {
            verifyAuthorizedOperation(null);
        } else {
            Optional<BabyProfileDTO> babyProfileOptional = babyProfileRepository.findById(babyProfileId).map(babyProfileMapper::toDto);
            if (babyProfileOptional.isPresent()) {
                verifyAuthorizedOperation(babyProfileOptional.get().getUserId());
            }
        }
    }

    private void updateOthersBabyProfileFromSameUserToMainFalse(String userId, Long id) {
        babyProfileRepository.saveAll(
            babyProfileRepository
                .findByUserIdAndIdNotIn(userId, Arrays.asList(id))
                .stream()
                .map(babyProfile -> {
                    babyProfile.main(false);
                    return babyProfile;
                })
                .collect(Collectors.toList())
        );
    }

    private void verifyAuthorizedOperation(String id) {
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            String userId = SecurityUtils.getCurrentUserId().orElse("");
            if (!userId.equalsIgnoreCase(id)) {
                throw new AccessDeniedException(Constants.THAT_IS_NOT_YOUR_BABY_PROFILE);
            }
        }
    }
}
