package com.mamazinha.baby.web.rest;

import com.mamazinha.baby.repository.BreastFeedRepository;
import com.mamazinha.baby.service.BreastFeedService;
import com.mamazinha.baby.service.dto.BreastFeedDTO;
import com.mamazinha.baby.service.dto.BreastFeedLastCurrentWeekDTO;
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
 * REST controller for managing {@link com.mamazinha.baby.domain.BreastFeed}.
 */
@RestController
@RequestMapping("/api")
public class BreastFeedResource {

    private final Logger log = LoggerFactory.getLogger(BreastFeedResource.class);

    private static final String ENTITY_NAME = "babyBreastFeed";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BreastFeedService breastFeedService;

    private final BreastFeedRepository breastFeedRepository;

    public BreastFeedResource(BreastFeedService breastFeedService, BreastFeedRepository breastFeedRepository) {
        this.breastFeedService = breastFeedService;
        this.breastFeedRepository = breastFeedRepository;
    }

    /**
     * {@code POST  /breast-feeds} : Create a new breastFeed.
     *
     * @param breastFeedDTO the breastFeedDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new breastFeedDTO, or with status {@code 400 (Bad Request)} if the breastFeed has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/breast-feeds")
    public ResponseEntity<BreastFeedDTO> createBreastFeed(@RequestBody BreastFeedDTO breastFeedDTO) throws URISyntaxException {
        log.debug("REST request to save BreastFeed : {}", breastFeedDTO);
        if (breastFeedDTO.getId() != null) {
            throw new BadRequestAlertException("A new breastFeed cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BreastFeedDTO result = breastFeedService.save(breastFeedDTO);
        return ResponseEntity
            .created(new URI("/api/breast-feeds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /breast-feeds/:id} : Updates an existing breastFeed.
     *
     * @param id the id of the breastFeedDTO to save.
     * @param breastFeedDTO the breastFeedDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated breastFeedDTO,
     * or with status {@code 400 (Bad Request)} if the breastFeedDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the breastFeedDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/breast-feeds/{id}")
    public ResponseEntity<BreastFeedDTO> updateBreastFeed(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BreastFeedDTO breastFeedDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BreastFeed : {}, {}", id, breastFeedDTO);
        if (breastFeedDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, breastFeedDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!breastFeedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BreastFeedDTO result = breastFeedService.save(breastFeedDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, breastFeedDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /breast-feeds/:id} : Partial updates given fields of an existing breastFeed, field will ignore if it is null
     *
     * @param id the id of the breastFeedDTO to save.
     * @param breastFeedDTO the breastFeedDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated breastFeedDTO,
     * or with status {@code 400 (Bad Request)} if the breastFeedDTO is not valid,
     * or with status {@code 404 (Not Found)} if the breastFeedDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the breastFeedDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/breast-feeds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BreastFeedDTO> partialUpdateBreastFeed(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BreastFeedDTO breastFeedDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BreastFeed partially : {}, {}", id, breastFeedDTO);
        if (breastFeedDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, breastFeedDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!breastFeedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BreastFeedDTO> result = breastFeedService.partialUpdate(breastFeedDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, breastFeedDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /breast-feeds} : get all the breastFeeds.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of breastFeeds in body.
     */
    @GetMapping("/breast-feeds")
    public ResponseEntity<List<BreastFeedDTO>> getAllBreastFeeds(Pageable pageable) {
        log.debug("REST request to get a page of BreastFeeds");
        Page<BreastFeedDTO> page = breastFeedService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /breast-feeds/:id} : get the "id" breastFeed.
     *
     * @param id the id of the breastFeedDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the breastFeedDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/breast-feeds/{id}")
    public ResponseEntity<BreastFeedDTO> getBreastFeed(@PathVariable Long id) {
        log.debug("REST request to get BreastFeed : {}", id);
        Optional<BreastFeedDTO> breastFeedDTO = breastFeedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(breastFeedDTO);
    }

    @GetMapping("/breast-feeds/today-breast-feeds-by-baby-profile/{id}")
    public ResponseEntity<List<BreastFeedDTO>> getAllTodayBrastFeedsByBabyProfile(
        @PathVariable Long id,
        @RequestParam(value = "tz", required = false) String timeZone
    ) {
        List<BreastFeedDTO> breastFeedDTOList = breastFeedService.getAllTodayBrastFeedsByBabyProfile(id, timeZone);
        return ResponseEntity.ok(breastFeedDTOList);
    }

    @GetMapping("/breast-feeds/lastweek-currentweek-average-breast-feeds-in-hours-eachday-by-baby-profile/{id}")
    public ResponseEntity<BreastFeedLastCurrentWeekDTO> getLastWeekCurrentWeekSumNapsHoursEachDayByBabyProfile(
        @PathVariable Long id,
        @RequestParam(value = "tz", required = false) String timeZone
    ) {
        BreastFeedLastCurrentWeekDTO breastFeedLastCurrentWeekDTO = breastFeedService.getLastWeekCurrentWeekAverageBreastFeedsHoursEachDayByBabyProfile(
            id,
            timeZone
        );
        return ResponseEntity.ok(breastFeedLastCurrentWeekDTO);
    }

    @GetMapping("/breast-feeds/incomplete-breast-feeds-by-baby-profile/{id}")
    public ResponseEntity<List<BreastFeedDTO>> getAllIncompleteBreastFeedsByBabyProfile(@PathVariable Long id) {
        List<BreastFeedDTO> breastFeedDTOList = breastFeedService.getAllIncompleteBreastFeedsByBabyProfile(id);
        return ResponseEntity.ok(breastFeedDTOList);
    }

    /**
     * {@code DELETE  /breast-feeds/:id} : delete the "id" breastFeed.
     *
     * @param id the id of the breastFeedDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/breast-feeds/{id}")
    public ResponseEntity<Void> deleteBreastFeed(@PathVariable Long id) {
        log.debug("REST request to delete BreastFeed : {}", id);
        breastFeedService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
