package com.mamazinha.baby.web.rest;

import com.mamazinha.baby.repository.BabyProfileRepository;
import com.mamazinha.baby.security.AuthoritiesConstants;
import com.mamazinha.baby.service.BabyProfileService;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mamazinha.baby.domain.BabyProfile}.
 */
@RestController
@RequestMapping("/api")
public class BabyProfileResource {

    private final Logger log = LoggerFactory.getLogger(BabyProfileResource.class);

    private static final String ENTITY_NAME = "babyProfile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BabyProfileService babyProfileService;

    private final BabyProfileRepository babyProfileRepository;

    public BabyProfileResource(BabyProfileService babyProfileService, BabyProfileRepository babyProfileRepository) {
        this.babyProfileService = babyProfileService;
        this.babyProfileRepository = babyProfileRepository;
    }

    /**
     * {@code POST  /baby-profiles} : Create a new babyProfile.
     *
     * @param babyProfileDTO the babyProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new babyProfileDTO, or with status {@code 400 (Bad Request)}
     *         if the babyProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/baby-profiles")
    public ResponseEntity<BabyProfileDTO> createBabyProfile(@Valid @RequestBody BabyProfileDTO babyProfileDTO) throws URISyntaxException {
        log.debug("REST request to save BabyProfile : {}", babyProfileDTO);
        if (babyProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new babyProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BabyProfileDTO result = babyProfileService.save(babyProfileDTO);
        return ResponseEntity
            .created(new URI("/api/baby-profiles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /baby-profiles/:id} : Updates an existing babyProfile.
     *
     * @param id             the id of the babyProfileDTO to save.
     * @param babyProfileDTO the babyProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated babyProfileDTO,
     *         or with status {@code 400 (Bad Request)} if the babyProfileDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         babyProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/baby-profiles/{id}")
    public ResponseEntity<BabyProfileDTO> updateBabyProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BabyProfileDTO babyProfileDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BabyProfile : {}, {}", id, babyProfileDTO);
        if (babyProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, babyProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!babyProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BabyProfileDTO result = babyProfileService.update(babyProfileDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, babyProfileDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /baby-profiles/:id} : Partial updates given fields of an
     * existing babyProfile, field will ignore if it is null
     *
     * @param id             the id of the babyProfileDTO to save.
     * @param babyProfileDTO the babyProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated babyProfileDTO,
     *         or with status {@code 400 (Bad Request)} if the babyProfileDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the babyProfileDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         babyProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/baby-profiles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BabyProfileDTO> partialUpdateBabyProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BabyProfileDTO babyProfileDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BabyProfile partially : {}, {}", id, babyProfileDTO);
        if (babyProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, babyProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!babyProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BabyProfileDTO> result = babyProfileService.partialUpdate(babyProfileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, babyProfileDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /baby-profiles} : get all the babyProfiles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of babyProfiles in body.
     */
    @GetMapping("/baby-profiles")
    public ResponseEntity<List<BabyProfileDTO>> getAllBabyProfiles(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of BabyProfiles");
        Page<BabyProfileDTO> page = babyProfileService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /baby-profiles/:id} : get the "id" babyProfile.
     *
     * @param id the id of the babyProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the babyProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/baby-profiles/{id}")
    public ResponseEntity<BabyProfileDTO> getBabyProfile(@PathVariable Long id) {
        log.debug("REST request to get BabyProfile : {}", id);
        Optional<BabyProfileDTO> babyProfileDTO = babyProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(babyProfileDTO);
    }

    /**
     * {@code DELETE  /baby-profiles/:id} : delete the "id" babyProfile.
     *
     * @param id the id of the babyProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/baby-profiles/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteBabyProfile(@PathVariable Long id) {
        log.debug("REST request to delete BabyProfile : {}", id);
        babyProfileService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
