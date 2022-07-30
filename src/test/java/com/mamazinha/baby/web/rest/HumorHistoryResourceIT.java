package com.mamazinha.baby.web.rest;

import static com.mamazinha.baby.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mamazinha.baby.IntegrationTest;
import com.mamazinha.baby.domain.HumorHistory;
import com.mamazinha.baby.repository.HumorHistoryRepository;
import com.mamazinha.baby.service.HumorHistoryService;
import com.mamazinha.baby.service.dto.HumorHistoryDTO;
import com.mamazinha.baby.service.mapper.HumorHistoryMapper;
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
 * Integration tests for the {@link HumorHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HumorHistoryResourceIT {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/humor-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HumorHistoryRepository humorHistoryRepository;

    @Mock
    private HumorHistoryRepository humorHistoryRepositoryMock;

    @Autowired
    private HumorHistoryMapper humorHistoryMapper;

    @Mock
    private HumorHistoryService humorHistoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHumorHistoryMockMvc;

    private HumorHistory humorHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HumorHistory createEntity(EntityManager em) {
        HumorHistory humorHistory = new HumorHistory().date(DEFAULT_DATE);
        return humorHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HumorHistory createUpdatedEntity(EntityManager em) {
        HumorHistory humorHistory = new HumorHistory().date(UPDATED_DATE);
        return humorHistory;
    }

    @BeforeEach
    public void initTest() {
        humorHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createHumorHistory() throws Exception {
        int databaseSizeBeforeCreate = humorHistoryRepository.findAll().size();
        // Create the HumorHistory
        HumorHistoryDTO humorHistoryDTO = humorHistoryMapper.toDto(humorHistory);
        restHumorHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humorHistoryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        HumorHistory testHumorHistory = humorHistoryList.get(humorHistoryList.size() - 1);
        assertThat(testHumorHistory.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createHumorHistoryWithExistingId() throws Exception {
        // Create the HumorHistory with an existing ID
        humorHistory.setId(1L);
        HumorHistoryDTO humorHistoryDTO = humorHistoryMapper.toDto(humorHistory);

        int databaseSizeBeforeCreate = humorHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHumorHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humorHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHumorHistories() throws Exception {
        // Initialize the database
        humorHistoryRepository.saveAndFlush(humorHistory);

        // Get all the humorHistoryList
        restHumorHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(humorHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHumorHistoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(humorHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHumorHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(humorHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHumorHistoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(humorHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHumorHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(humorHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getHumorHistory() throws Exception {
        // Initialize the database
        humorHistoryRepository.saveAndFlush(humorHistory);

        // Get the humorHistory
        restHumorHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, humorHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(humorHistory.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingHumorHistory() throws Exception {
        // Get the humorHistory
        restHumorHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHumorHistory() throws Exception {
        // Initialize the database
        humorHistoryRepository.saveAndFlush(humorHistory);

        int databaseSizeBeforeUpdate = humorHistoryRepository.findAll().size();

        // Update the humorHistory
        HumorHistory updatedHumorHistory = humorHistoryRepository.findById(humorHistory.getId()).get();
        // Disconnect from session so that the updates on updatedHumorHistory are not directly saved in db
        em.detach(updatedHumorHistory);
        updatedHumorHistory.date(UPDATED_DATE);
        HumorHistoryDTO humorHistoryDTO = humorHistoryMapper.toDto(updatedHumorHistory);

        restHumorHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, humorHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humorHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeUpdate);
        HumorHistory testHumorHistory = humorHistoryList.get(humorHistoryList.size() - 1);
        assertThat(testHumorHistory.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingHumorHistory() throws Exception {
        int databaseSizeBeforeUpdate = humorHistoryRepository.findAll().size();
        humorHistory.setId(count.incrementAndGet());

        // Create the HumorHistory
        HumorHistoryDTO humorHistoryDTO = humorHistoryMapper.toDto(humorHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHumorHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, humorHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humorHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHumorHistory() throws Exception {
        int databaseSizeBeforeUpdate = humorHistoryRepository.findAll().size();
        humorHistory.setId(count.incrementAndGet());

        // Create the HumorHistory
        HumorHistoryDTO humorHistoryDTO = humorHistoryMapper.toDto(humorHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumorHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humorHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHumorHistory() throws Exception {
        int databaseSizeBeforeUpdate = humorHistoryRepository.findAll().size();
        humorHistory.setId(count.incrementAndGet());

        // Create the HumorHistory
        HumorHistoryDTO humorHistoryDTO = humorHistoryMapper.toDto(humorHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumorHistoryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humorHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHumorHistoryWithPatch() throws Exception {
        // Initialize the database
        humorHistoryRepository.saveAndFlush(humorHistory);

        int databaseSizeBeforeUpdate = humorHistoryRepository.findAll().size();

        // Update the humorHistory using partial update
        HumorHistory partialUpdatedHumorHistory = new HumorHistory();
        partialUpdatedHumorHistory.setId(humorHistory.getId());

        restHumorHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHumorHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHumorHistory))
            )
            .andExpect(status().isOk());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeUpdate);
        HumorHistory testHumorHistory = humorHistoryList.get(humorHistoryList.size() - 1);
        assertThat(testHumorHistory.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void fullUpdateHumorHistoryWithPatch() throws Exception {
        // Initialize the database
        humorHistoryRepository.saveAndFlush(humorHistory);

        int databaseSizeBeforeUpdate = humorHistoryRepository.findAll().size();

        // Update the humorHistory using partial update
        HumorHistory partialUpdatedHumorHistory = new HumorHistory();
        partialUpdatedHumorHistory.setId(humorHistory.getId());

        partialUpdatedHumorHistory.date(UPDATED_DATE);

        restHumorHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHumorHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHumorHistory))
            )
            .andExpect(status().isOk());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeUpdate);
        HumorHistory testHumorHistory = humorHistoryList.get(humorHistoryList.size() - 1);
        assertThat(testHumorHistory.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingHumorHistory() throws Exception {
        int databaseSizeBeforeUpdate = humorHistoryRepository.findAll().size();
        humorHistory.setId(count.incrementAndGet());

        // Create the HumorHistory
        HumorHistoryDTO humorHistoryDTO = humorHistoryMapper.toDto(humorHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHumorHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, humorHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humorHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHumorHistory() throws Exception {
        int databaseSizeBeforeUpdate = humorHistoryRepository.findAll().size();
        humorHistory.setId(count.incrementAndGet());

        // Create the HumorHistory
        HumorHistoryDTO humorHistoryDTO = humorHistoryMapper.toDto(humorHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumorHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humorHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHumorHistory() throws Exception {
        int databaseSizeBeforeUpdate = humorHistoryRepository.findAll().size();
        humorHistory.setId(count.incrementAndGet());

        // Create the HumorHistory
        HumorHistoryDTO humorHistoryDTO = humorHistoryMapper.toDto(humorHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumorHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humorHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HumorHistory in the database
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHumorHistory() throws Exception {
        // Initialize the database
        humorHistoryRepository.saveAndFlush(humorHistory);

        int databaseSizeBeforeDelete = humorHistoryRepository.findAll().size();

        // Delete the humorHistory
        restHumorHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, humorHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HumorHistory> humorHistoryList = humorHistoryRepository.findAll();
        assertThat(humorHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
