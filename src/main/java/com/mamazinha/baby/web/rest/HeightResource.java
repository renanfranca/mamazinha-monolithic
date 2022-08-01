package com.mamazinha.baby.web.rest;

import com.mamazinha.baby.repository.HeightRepository;
import com.mamazinha.baby.service.HeightService;
import com.mamazinha.baby.service.dto.HeightDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mamazinha.baby.domain.Height}.
 */
@RestController
@RequestMapping("/api")
public class HeightResource {

    private final Logger log = LoggerFactory.getLogger(HeightResource.class);

    private static final String ENTITY_NAME = "babyHeight";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HeightService heightService;

    private final HeightRepository heightRepository;

    public HeightResource(HeightService heightService, HeightRepository heightRepository) {
        this.heightService = heightService;
        this.heightRepository = heightRepository;
    }

    /**
     * {@code POST  /heights} : Create a new height.
     *
     * @param heightDTO the heightDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new heightDTO, or with status {@code 400 (Bad Request)} if the height has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/heights")
    public ResponseEntity<HeightDTO> createHeight(@Valid @RequestBody HeightDTO heightDTO) throws URISyntaxException {
        log.debug("REST request to save Height : {}", heightDTO);
        if (heightDTO.getId() != null) {
            throw new BadRequestAlertException("A new height cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HeightDTO result = heightService.save(heightDTO);
        return ResponseEntity
            .created(new URI("/api/heights/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /heights/:id} : Updates an existing height.
     *
     * @param id the id of the heightDTO to save.
     * @param heightDTO the heightDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated heightDTO,
     * or with status {@code 400 (Bad Request)} if the heightDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the heightDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/heights/{id}")
    public ResponseEntity<HeightDTO> updateHeight(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HeightDTO heightDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Height : {}, {}", id, heightDTO);
        if (heightDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, heightDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!heightRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HeightDTO result = heightService.save(heightDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, heightDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /heights/:id} : Partial updates given fields of an existing height, field will ignore if it is null
     *
     * @param id the id of the heightDTO to save.
     * @param heightDTO the heightDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated heightDTO,
     * or with status {@code 400 (Bad Request)} if the heightDTO is not valid,
     * or with status {@code 404 (Not Found)} if the heightDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the heightDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/heights/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HeightDTO> partialUpdateHeight(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HeightDTO heightDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Height partially : {}, {}", id, heightDTO);
        if (heightDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, heightDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!heightRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HeightDTO> result = heightService.partialUpdate(heightDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, heightDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /heights} : get all the heights.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of heights in body.
     */
    @GetMapping("/heights")
    public ResponseEntity<List<HeightDTO>> getAllHeights(Pageable pageable) {
        log.debug("REST request to get a page of Heights");
        Page<HeightDTO> page = heightService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/heights/latest-height-by-baby-profile/{id}")
    public ResponseEntity<HeightDTO> getLatestWeight(@PathVariable Long id) {
        Optional<HeightDTO> heightDTO = heightService.findLatestByBabyProfile(id);
        return ResponseEntity.ok(heightDTO.orElse(null));
    }

    @GetMapping("/heights/last-heights-by-days-by-baby-profile/{id}")
    public ResponseEntity<List<HeightDTO>> getAllLastHeightsByDaysByBabyProfile(
        @PathVariable Long id,
        @RequestParam(value = "days", required = true) Integer days,
        @RequestParam(value = "tz", required = false) String timeZone
    ) {
        List<HeightDTO> heightDTOList = heightService.findAllLastHeightsByDaysByBabyProfile(id, days, timeZone);
        return ResponseEntity.ok(heightDTOList);
    }

    /**
     * {@code GET  /heights/:id} : get the "id" height.
     *
     * @param id the id of the heightDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the heightDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/heights/{id}")
    public ResponseEntity<HeightDTO> getHeight(@PathVariable Long id) {
        log.debug("REST request to get Height : {}", id);
        Optional<HeightDTO> heightDTO = heightService.findOne(id);
        return ResponseUtil.wrapOrNotFound(heightDTO);
    }

    /**
     * {@code DELETE  /heights/:id} : delete the "id" height.
     *
     * @param id the id of the heightDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/heights/{id}")
    public ResponseEntity<Void> deleteHeight(@PathVariable Long id) {
        log.debug("REST request to delete Height : {}", id);
        heightService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
