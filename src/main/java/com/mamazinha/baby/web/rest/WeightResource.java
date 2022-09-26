package com.mamazinha.baby.web.rest;

import com.mamazinha.baby.repository.WeightRepository;
import com.mamazinha.baby.service.WeightService;
import com.mamazinha.baby.service.dto.WeightDTO;
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
 * REST controller for managing {@link com.mamazinha.baby.domain.Weight}.
 */
@RestController
@RequestMapping("/api")
public class WeightResource {

    private final Logger log = LoggerFactory.getLogger(WeightResource.class);

    private static final String ENTITY_NAME = "weight";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WeightService weightService;

    private final WeightRepository weightRepository;

    public WeightResource(WeightService weightService, WeightRepository weightRepository) {
        this.weightService = weightService;
        this.weightRepository = weightRepository;
    }

    /**
     * {@code POST  /weights} : Create a new weight.
     *
     * @param weightDTO the weightDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new weightDTO, or with status {@code 400 (Bad Request)} if
     *         the weight has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/weights")
    public ResponseEntity<WeightDTO> createWeight(@Valid @RequestBody WeightDTO weightDTO) throws URISyntaxException {
        log.debug("REST request to save Weight : {}", weightDTO);
        if (weightDTO.getId() != null) {
            throw new BadRequestAlertException("A new weight cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WeightDTO result = weightService.save(weightDTO);
        return ResponseEntity
            .created(new URI("/api/weights/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /weights/:id} : Updates an existing weight.
     *
     * @param id        the id of the weightDTO to save.
     * @param weightDTO the weightDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated weightDTO,
     *         or with status {@code 400 (Bad Request)} if the weightDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the weightDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/weights/{id}")
    public ResponseEntity<WeightDTO> updateWeight(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WeightDTO weightDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Weight : {}, {}", id, weightDTO);
        if (weightDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, weightDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!weightRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WeightDTO result = weightService.update(weightDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, weightDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /weights/:id} : Partial updates given fields of an existing
     * weight, field will ignore if it is null
     *
     * @param id        the id of the weightDTO to save.
     * @param weightDTO the weightDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated weightDTO,
     *         or with status {@code 400 (Bad Request)} if the weightDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the weightDTO is not found,
     *         or with status {@code 500 (Internal Server Error)} if the weightDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/weights/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WeightDTO> partialUpdateWeight(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WeightDTO weightDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Weight partially : {}, {}", id, weightDTO);
        if (weightDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, weightDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!weightRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WeightDTO> result = weightService.partialUpdate(weightDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, weightDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /weights} : get all the weights.
     *
     * @param pageable  the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is
     *                  applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of weights in body.
     */
    @GetMapping("/weights")
    public ResponseEntity<List<WeightDTO>> getAllWeights(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Weights");
        Page<WeightDTO> page;
        if (eagerload) {
            page = weightService.findAllWithEagerRelationships(pageable);
        } else {
            page = weightService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/weights/latest-weight-by-baby-profile/{id}")
    public ResponseEntity<WeightDTO> getLatestWeight(@PathVariable Long id) {
        Optional<WeightDTO> weightDTO = weightService.findLatestByBabyProfile(id);
        return ResponseEntity.ok(weightDTO.orElse(null));
    }

    @GetMapping("/weights/last-weights-by-days-by-baby-profile/{id}")
    public ResponseEntity<List<WeightDTO>> getAllLastWeightsByDaysByBabyProfile(
        @PathVariable Long id,
        @RequestParam(value = "days", required = true) Integer days,
        @RequestParam(value = "tz", required = false) String timeZone
    ) {
        List<WeightDTO> weightDTOList = weightService.findAllLastWeightsByDaysByBabyProfile(id, days, timeZone);
        return ResponseEntity.ok(weightDTOList);
    }

    /**
     * {@code GET  /weights/:id} : get the "id" weight.
     *
     * @param id the id of the weightDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the weightDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/weights/{id}")
    public ResponseEntity<WeightDTO> getWeight(@PathVariable Long id) {
        log.debug("REST request to get Weight : {}", id);
        Optional<WeightDTO> weightDTO = weightService.findOne(id);
        return ResponseUtil.wrapOrNotFound(weightDTO);
    }

    /**
     * {@code DELETE  /weights/:id} : delete the "id" weight.
     *
     * @param id the id of the weightDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/weights/{id}")
    public ResponseEntity<Void> deleteWeight(@PathVariable Long id) {
        log.debug("REST request to delete Weight : {}", id);
        weightService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
