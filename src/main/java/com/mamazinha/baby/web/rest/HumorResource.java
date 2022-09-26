package com.mamazinha.baby.web.rest;

import com.mamazinha.baby.repository.HumorRepository;
import com.mamazinha.baby.security.AuthoritiesConstants;
import com.mamazinha.baby.service.HumorService;
import com.mamazinha.baby.service.dto.HumorDTO;
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
 * REST controller for managing {@link com.mamazinha.baby.domain.Humor}.
 */
@RestController
@RequestMapping("/api")
public class HumorResource {

    private final Logger log = LoggerFactory.getLogger(HumorResource.class);

    private static final String ENTITY_NAME = "humor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HumorService humorService;

    private final HumorRepository humorRepository;

    public HumorResource(HumorService humorService, HumorRepository humorRepository) {
        this.humorService = humorService;
        this.humorRepository = humorRepository;
    }

    /**
     * {@code POST  /humors} : Create a new humor.
     *
     * @param humorDTO the humorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new humorDTO, or with status {@code 400 (Bad Request)} if
     *         the humor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/humors")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<HumorDTO> createHumor(@Valid @RequestBody HumorDTO humorDTO) throws URISyntaxException {
        log.debug("REST request to save Humor : {}", humorDTO);
        if (humorDTO.getId() != null) {
            throw new BadRequestAlertException("A new humor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HumorDTO result = humorService.save(humorDTO);
        return ResponseEntity
            .created(new URI("/api/humors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /humors/:id} : Updates an existing humor.
     *
     * @param id       the id of the humorDTO to save.
     * @param humorDTO the humorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated humorDTO,
     *         or with status {@code 400 (Bad Request)} if the humorDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the humorDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/humors/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<HumorDTO> updateHumor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HumorDTO humorDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Humor : {}, {}", id, humorDTO);
        if (humorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, humorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!humorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HumorDTO result = humorService.update(humorDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, humorDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /humors/:id} : Partial updates given fields of an existing
     * humor, field will ignore if it is null
     *
     * @param id       the id of the humorDTO to save.
     * @param humorDTO the humorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated humorDTO,
     *         or with status {@code 400 (Bad Request)} if the humorDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the humorDTO is not found,
     *         or with status {@code 500 (Internal Server Error)} if the humorDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/humors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<HumorDTO> partialUpdateHumor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HumorDTO humorDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Humor partially : {}, {}", id, humorDTO);
        if (humorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, humorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!humorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HumorDTO> result = humorService.partialUpdate(humorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, humorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /humors} : get all the humors.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of humors in body.
     */
    @GetMapping("/humors")
    public ResponseEntity<List<HumorDTO>> getAllHumors(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Humors");
        Page<HumorDTO> page = humorService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /humors/:id} : get the "id" humor.
     *
     * @param id the id of the humorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the humorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/humors/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<HumorDTO> getHumor(@PathVariable Long id) {
        log.debug("REST request to get Humor : {}", id);
        Optional<HumorDTO> humorDTO = humorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(humorDTO);
    }

    /**
     * {@code DELETE  /humors/:id} : delete the "id" humor.
     *
     * @param id the id of the humorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/humors/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteHumor(@PathVariable Long id) {
        log.debug("REST request to delete Humor : {}", id);
        humorService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
