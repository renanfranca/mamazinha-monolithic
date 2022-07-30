package com.mamazinha.baby.web.rest;

import static com.mamazinha.baby.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mamazinha.baby.IntegrationTest;
import com.mamazinha.baby.domain.Nap;
import com.mamazinha.baby.domain.enumeration.Place;
import com.mamazinha.baby.repository.NapRepository;
import com.mamazinha.baby.service.NapService;
import com.mamazinha.baby.service.dto.NapDTO;
import com.mamazinha.baby.service.mapper.NapMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NapResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class NapResourceIT {

    private static final ZonedDateTime DEFAULT_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Place DEFAULT_PLACE = Place.LAP;
    private static final Place UPDATED_PLACE = Place.BABY_CRIB;

    private static final String ENTITY_API_URL = "/api/naps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NapRepository napRepository;

    @Mock
    private NapRepository napRepositoryMock;

    @Autowired
    private NapMapper napMapper;

    @Mock
    private NapService napServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNapMockMvc;

    private Nap nap;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Nap createEntity(EntityManager em) {
        Nap nap = new Nap().start(DEFAULT_START).end(DEFAULT_END).place(DEFAULT_PLACE);
        return nap;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Nap createUpdatedEntity(EntityManager em) {
        Nap nap = new Nap().start(UPDATED_START).end(UPDATED_END).place(UPDATED_PLACE);
        return nap;
    }

    @BeforeEach
    public void initTest() {
        nap = createEntity(em);
    }

    @Test
    @Transactional
    void createNap() throws Exception {
        int databaseSizeBeforeCreate = napRepository.findAll().size();
        // Create the Nap
        NapDTO napDTO = napMapper.toDto(nap);
        restNapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(napDTO)))
            .andExpect(status().isCreated());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeCreate + 1);
        Nap testNap = napList.get(napList.size() - 1);
        assertThat(testNap.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testNap.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testNap.getPlace()).isEqualTo(DEFAULT_PLACE);
    }

    @Test
    @Transactional
    void createNapWithExistingId() throws Exception {
        // Create the Nap with an existing ID
        nap.setId(1L);
        NapDTO napDTO = napMapper.toDto(nap);

        int databaseSizeBeforeCreate = napRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(napDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllNaps() throws Exception {
        // Initialize the database
        napRepository.saveAndFlush(nap);

        // Get all the napList
        restNapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nap.getId().intValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(DEFAULT_END))))
            .andExpect(jsonPath("$.[*].place").value(hasItem(DEFAULT_PLACE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNapsWithEagerRelationshipsIsEnabled() throws Exception {
        when(napServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNapMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(napServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNapsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(napServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNapMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(napServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getNap() throws Exception {
        // Initialize the database
        napRepository.saveAndFlush(nap);

        // Get the nap
        restNapMockMvc
            .perform(get(ENTITY_API_URL_ID, nap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(nap.getId().intValue()))
            .andExpect(jsonPath("$.start").value(sameInstant(DEFAULT_START)))
            .andExpect(jsonPath("$.end").value(sameInstant(DEFAULT_END)))
            .andExpect(jsonPath("$.place").value(DEFAULT_PLACE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNap() throws Exception {
        // Get the nap
        restNapMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNap() throws Exception {
        // Initialize the database
        napRepository.saveAndFlush(nap);

        int databaseSizeBeforeUpdate = napRepository.findAll().size();

        // Update the nap
        Nap updatedNap = napRepository.findById(nap.getId()).get();
        // Disconnect from session so that the updates on updatedNap are not directly saved in db
        em.detach(updatedNap);
        updatedNap.start(UPDATED_START).end(UPDATED_END).place(UPDATED_PLACE);
        NapDTO napDTO = napMapper.toDto(updatedNap);

        restNapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, napDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(napDTO))
            )
            .andExpect(status().isOk());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeUpdate);
        Nap testNap = napList.get(napList.size() - 1);
        assertThat(testNap.getStart()).isEqualTo(UPDATED_START);
        assertThat(testNap.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testNap.getPlace()).isEqualTo(UPDATED_PLACE);
    }

    @Test
    @Transactional
    void putNonExistingNap() throws Exception {
        int databaseSizeBeforeUpdate = napRepository.findAll().size();
        nap.setId(count.incrementAndGet());

        // Create the Nap
        NapDTO napDTO = napMapper.toDto(nap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, napDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(napDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNap() throws Exception {
        int databaseSizeBeforeUpdate = napRepository.findAll().size();
        nap.setId(count.incrementAndGet());

        // Create the Nap
        NapDTO napDTO = napMapper.toDto(nap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(napDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNap() throws Exception {
        int databaseSizeBeforeUpdate = napRepository.findAll().size();
        nap.setId(count.incrementAndGet());

        // Create the Nap
        NapDTO napDTO = napMapper.toDto(nap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNapMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(napDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNapWithPatch() throws Exception {
        // Initialize the database
        napRepository.saveAndFlush(nap);

        int databaseSizeBeforeUpdate = napRepository.findAll().size();

        // Update the nap using partial update
        Nap partialUpdatedNap = new Nap();
        partialUpdatedNap.setId(nap.getId());

        partialUpdatedNap.start(UPDATED_START).end(UPDATED_END).place(UPDATED_PLACE);

        restNapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNap.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNap))
            )
            .andExpect(status().isOk());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeUpdate);
        Nap testNap = napList.get(napList.size() - 1);
        assertThat(testNap.getStart()).isEqualTo(UPDATED_START);
        assertThat(testNap.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testNap.getPlace()).isEqualTo(UPDATED_PLACE);
    }

    @Test
    @Transactional
    void fullUpdateNapWithPatch() throws Exception {
        // Initialize the database
        napRepository.saveAndFlush(nap);

        int databaseSizeBeforeUpdate = napRepository.findAll().size();

        // Update the nap using partial update
        Nap partialUpdatedNap = new Nap();
        partialUpdatedNap.setId(nap.getId());

        partialUpdatedNap.start(UPDATED_START).end(UPDATED_END).place(UPDATED_PLACE);

        restNapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNap.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNap))
            )
            .andExpect(status().isOk());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeUpdate);
        Nap testNap = napList.get(napList.size() - 1);
        assertThat(testNap.getStart()).isEqualTo(UPDATED_START);
        assertThat(testNap.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testNap.getPlace()).isEqualTo(UPDATED_PLACE);
    }

    @Test
    @Transactional
    void patchNonExistingNap() throws Exception {
        int databaseSizeBeforeUpdate = napRepository.findAll().size();
        nap.setId(count.incrementAndGet());

        // Create the Nap
        NapDTO napDTO = napMapper.toDto(nap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, napDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(napDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNap() throws Exception {
        int databaseSizeBeforeUpdate = napRepository.findAll().size();
        nap.setId(count.incrementAndGet());

        // Create the Nap
        NapDTO napDTO = napMapper.toDto(nap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(napDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNap() throws Exception {
        int databaseSizeBeforeUpdate = napRepository.findAll().size();
        nap.setId(count.incrementAndGet());

        // Create the Nap
        NapDTO napDTO = napMapper.toDto(nap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNapMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(napDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Nap in the database
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNap() throws Exception {
        // Initialize the database
        napRepository.saveAndFlush(nap);

        int databaseSizeBeforeDelete = napRepository.findAll().size();

        // Delete the nap
        restNapMockMvc.perform(delete(ENTITY_API_URL_ID, nap.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
