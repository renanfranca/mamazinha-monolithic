package com.mamazinha.baby.web.rest;

import com.mamazinha.baby.repository.NapRepository;
import com.mamazinha.baby.service.NapService;
import com.mamazinha.baby.service.dto.FavoriteNapPlaceDTO;
import com.mamazinha.baby.service.dto.HumorAverageDTO;
import com.mamazinha.baby.service.dto.HumorAverageLastCurrentWeekDTO;
import com.mamazinha.baby.service.dto.NapDTO;
import com.mamazinha.baby.service.dto.NapLastCurrentWeekDTO;
import com.mamazinha.baby.service.dto.NapTodayDTO;
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
 * REST controller for managing {@link com.mamazinha.baby.domain.Nap}.
 */
@RestController
@RequestMapping("/api")
public class NapResource {

    private final Logger log = LoggerFactory.getLogger(NapResource.class);

    private static final String ENTITY_NAME = "babyNap";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NapService napService;

    private final NapRepository napRepository;

    public NapResource(NapService napService, NapRepository napRepository) {
        this.napService = napService;
        this.napRepository = napRepository;
    }

    /**
     * {@code POST  /naps} : Create a new nap.
     *
     * @param napDTO the napDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new napDTO, or with status {@code 400 (Bad Request)} if the nap has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/naps")
    public ResponseEntity<NapDTO> createNap(@RequestBody NapDTO napDTO) throws URISyntaxException {
        log.debug("REST request to save Nap : {}", napDTO);
        if (napDTO.getId() != null) {
            throw new BadRequestAlertException("A new nap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NapDTO result = napService.save(napDTO);
        return ResponseEntity
            .created(new URI("/api/naps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /naps/:id} : Updates an existing nap.
     *
     * @param id the id of the napDTO to save.
     * @param napDTO the napDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated napDTO,
     * or with status {@code 400 (Bad Request)} if the napDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the napDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/naps/{id}")
    public ResponseEntity<NapDTO> updateNap(@PathVariable(value = "id", required = false) final Long id, @RequestBody NapDTO napDTO)
        throws URISyntaxException {
        log.debug("REST request to update Nap : {}, {}", id, napDTO);
        if (napDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, napDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!napRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NapDTO result = napService.save(napDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, napDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /naps/:id} : Partial updates given fields of an existing nap, field will ignore if it is null
     *
     * @param id the id of the napDTO to save.
     * @param napDTO the napDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated napDTO,
     * or with status {@code 400 (Bad Request)} if the napDTO is not valid,
     * or with status {@code 404 (Not Found)} if the napDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the napDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/naps/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NapDTO> partialUpdateNap(@PathVariable(value = "id", required = false) final Long id, @RequestBody NapDTO napDTO)
        throws URISyntaxException {
        log.debug("REST request to partial update Nap partially : {}, {}", id, napDTO);
        if (napDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, napDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!napRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NapDTO> result = napService.partialUpdate(napDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, napDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /naps} : get all the naps.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of naps in body.
     */
    @GetMapping("/naps")
    public ResponseEntity<List<NapDTO>> getAllNaps(Pageable pageable) {
        log.debug("REST request to get a page of Naps");
        Page<NapDTO> page = napService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /naps/:id} : get the "id" nap.
     *
     * @param id the id of the napDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the napDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/naps/{id}")
    public ResponseEntity<NapDTO> getNap(@PathVariable Long id) {
        log.debug("REST request to get Nap : {}", id);
        Optional<NapDTO> napDTO = napService.findOne(id);
        return ResponseUtil.wrapOrNotFound(napDTO);
    }

    @GetMapping("/naps/today-sum-naps-in-hours-by-baby-profile/{id}")
    public ResponseEntity<NapTodayDTO> getTodaySumNapsHoursByBabyProfile(
        @PathVariable Long id,
        @RequestParam(value = "tz", required = false) String timeZone
    ) {
        NapTodayDTO napTodayDTO = napService.getTodaySumNapsHoursByBabyProfile(id, timeZone);
        return ResponseEntity.ok(napTodayDTO);
    }

    @GetMapping("/naps/lastweek-currentweek-sum-naps-in-hours-eachday-by-baby-profile/{id}")
    public ResponseEntity<NapLastCurrentWeekDTO> getLastWeekCurrentWeekSumNapsHoursEachDayByBabyProfile(
        @PathVariable Long id,
        @RequestParam(value = "tz", required = false) String timeZone
    ) {
        NapLastCurrentWeekDTO napLastCurrentWeekDTO = napService.getLastWeekCurrentWeekSumNapsHoursEachDayByBabyProfile(id, timeZone);
        return ResponseEntity.ok(napLastCurrentWeekDTO);
    }

    @GetMapping("/naps/lastweek-currentweek-average-naps-humor-eachday-by-baby-profile/{id}")
    public ResponseEntity<HumorAverageLastCurrentWeekDTO> getLastCurrentWeekAverageNapsHumorByBabyProfile(
        @PathVariable Long id,
        @RequestParam(value = "tz", required = false) String timeZone
    ) {
        HumorAverageLastCurrentWeekDTO humorAverageLastCurrentWeekDTO = napService.getLastCurrentWeekAverageNapsHumorByBabyProfile(
            id,
            timeZone
        );
        return ResponseEntity.ok(humorAverageLastCurrentWeekDTO);
    }

    @GetMapping("/naps/today-average-nap-humor-by-baby-profile/{id}")
    public ResponseEntity<HumorAverageDTO> getTodayAverageNapHumorByBabyProfile(
        @PathVariable Long id,
        @RequestParam(value = "tz", required = false) String timeZone
    ) {
        HumorAverageDTO humorAverageDTO = napService.getTodayAverageNapHumorByBabyProfile(id, timeZone);
        return ResponseEntity.ok(humorAverageDTO);
    }

    @GetMapping("/naps/favorite-nap-place-from-last-days-by-baby-profile/{id}")
    public ResponseEntity<FavoriteNapPlaceDTO> getFavoriteNapPlaceFromLastDaysByBabyProfile(
        @PathVariable Long id,
        @RequestParam(value = "lastDays", required = true) Integer lastDays,
        @RequestParam(value = "tz", required = false) String timeZone
    ) {
        FavoriteNapPlaceDTO favoriteNapPlaceDTO = napService.getFavoriteNapPlaceFromLastDaysByBabyProfile(id, lastDays, timeZone);
        return ResponseEntity.ok(favoriteNapPlaceDTO);
    }

    @GetMapping("/naps/incomplete-naps-by-baby-profile/{id}")
    public ResponseEntity<List<NapDTO>> getAllIncompleteNapsByBabyProfile(@PathVariable Long id) {
        List<NapDTO> napDTOList = napService.getAllIncompleteNapsByBabyProfile(id);
        return ResponseEntity.ok(napDTOList);
    }

    /**
     * {@code DELETE  /naps/:id} : delete the "id" nap.
     *
     * @param id the id of the napDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/naps/{id}")
    public ResponseEntity<Void> deleteNap(@PathVariable Long id) {
        log.debug("REST request to delete Nap : {}", id);
        napService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
