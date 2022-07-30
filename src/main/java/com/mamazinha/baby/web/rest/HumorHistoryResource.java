package com.mamazinha.baby.web.rest;

import com.mamazinha.baby.repository.HumorHistoryRepository;
import com.mamazinha.baby.service.HumorHistoryService;
import com.mamazinha.baby.service.dto.HumorHistoryDTO;
import com.mamazinha.baby.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mamazinha.baby.domain.HumorHistory}.
 */
@RestController
@RequestMapping("/api")
public class HumorHistoryResource {

    private final Logger log = LoggerFactory.getLogger(HumorHistoryResource.class);

    private static final String ENTITY_NAME = "humorHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HumorHistoryService humorHistoryService;

    private final HumorHistoryRepository humorHistoryRepository;

    public HumorHistoryResource(HumorHistoryService humorHistoryService, HumorHistoryRepository humorHistoryRepository) {
        this.humorHistoryService = humorHistoryService;
        this.humorHistoryRepository = humorHistoryRepository;
    }

    /**
     * {@code POST  /humor-histories} : Create a new humorHistory.
     *
     * @param humorHistoryDTO the humorHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new humorHistoryDTO, or with status {@code 400 (Bad Request)} if the humorHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/humor-histories")
    public ResponseEntity<HumorHistoryDTO> createHumorHistory(@RequestBody HumorHistoryDTO humorHistoryDTO) throws URISyntaxException {
        log.debug("REST request to save HumorHistory : {}", humorHistoryDTO);
        if (humorHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new humorHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HumorHistoryDTO result = humorHistoryService.save(humorHistoryDTO);
        return ResponseEntity
            .created(new URI("/api/humor-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /humor-histories/:id} : Updates an existing humorHistory.
     *
     * @param id the id of the humorHistoryDTO to save.
     * @param humorHistoryDTO the humorHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated humorHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the humorHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the humorHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/humor-histories/{id}")
    public ResponseEntity<HumorHistoryDTO> updateHumorHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HumorHistoryDTO humorHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update HumorHistory : {}, {}", id, humorHistoryDTO);
        if (humorHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, humorHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!humorHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HumorHistoryDTO result = humorHistoryService.update(humorHistoryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, humorHistoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /humor-histories/:id} : Partial updates given fields of an existing humorHistory, field will ignore if it is null
     *
     * @param id the id of the humorHistoryDTO to save.
     * @param humorHistoryDTO the humorHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated humorHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the humorHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the humorHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the humorHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/humor-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HumorHistoryDTO> partialUpdateHumorHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HumorHistoryDTO humorHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update HumorHistory partially : {}, {}", id, humorHistoryDTO);
        if (humorHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, humorHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!humorHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HumorHistoryDTO> result = humorHistoryService.partialUpdate(humorHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, humorHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /humor-histories} : get all the humorHistories.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of humorHistories in body.
     */
    @GetMapping("/humor-histories")
    public ResponseEntity<List<HumorHistoryDTO>> getAllHumorHistories(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of HumorHistories");
        Page<HumorHistoryDTO> page;
        if (eagerload) {
            page = humorHistoryService.findAllWithEagerRelationships(pageable);
        } else {
            page = humorHistoryService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /humor-histories/:id} : get the "id" humorHistory.
     *
     * @param id the id of the humorHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the humorHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/humor-histories/{id}")
    public ResponseEntity<HumorHistoryDTO> getHumorHistory(@PathVariable Long id) {
        log.debug("REST request to get HumorHistory : {}", id);
        Optional<HumorHistoryDTO> humorHistoryDTO = humorHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(humorHistoryDTO);
    }

    /**
     * {@code DELETE  /humor-histories/:id} : delete the "id" humorHistory.
     *
     * @param id the id of the humorHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/humor-histories/{id}")
    public ResponseEntity<Void> deleteHumorHistory(@PathVariable Long id) {
        log.debug("REST request to delete HumorHistory : {}", id);
        humorHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
