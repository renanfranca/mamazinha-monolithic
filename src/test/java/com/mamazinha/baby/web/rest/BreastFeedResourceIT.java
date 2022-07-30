package com.mamazinha.baby.web.rest;

import static com.mamazinha.baby.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mamazinha.baby.IntegrationTest;
import com.mamazinha.baby.domain.BreastFeed;
import com.mamazinha.baby.domain.enumeration.Pain;
import com.mamazinha.baby.repository.BreastFeedRepository;
import com.mamazinha.baby.service.BreastFeedService;
import com.mamazinha.baby.service.dto.BreastFeedDTO;
import com.mamazinha.baby.service.mapper.BreastFeedMapper;
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
 * Integration tests for the {@link BreastFeedResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BreastFeedResourceIT {

    private static final ZonedDateTime DEFAULT_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Pain DEFAULT_PAIN = Pain.NO_PAIN;
    private static final Pain UPDATED_PAIN = Pain.DISCOMFORTING;

    private static final String ENTITY_API_URL = "/api/breast-feeds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BreastFeedRepository breastFeedRepository;

    @Mock
    private BreastFeedRepository breastFeedRepositoryMock;

    @Autowired
    private BreastFeedMapper breastFeedMapper;

    @Mock
    private BreastFeedService breastFeedServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBreastFeedMockMvc;

    private BreastFeed breastFeed;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BreastFeed createEntity(EntityManager em) {
        BreastFeed breastFeed = new BreastFeed().start(DEFAULT_START).end(DEFAULT_END).pain(DEFAULT_PAIN);
        return breastFeed;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BreastFeed createUpdatedEntity(EntityManager em) {
        BreastFeed breastFeed = new BreastFeed().start(UPDATED_START).end(UPDATED_END).pain(UPDATED_PAIN);
        return breastFeed;
    }

    @BeforeEach
    public void initTest() {
        breastFeed = createEntity(em);
    }

    @Test
    @Transactional
    void createBreastFeed() throws Exception {
        int databaseSizeBeforeCreate = breastFeedRepository.findAll().size();
        // Create the BreastFeed
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(breastFeed);
        restBreastFeedMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(breastFeedDTO)))
            .andExpect(status().isCreated());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeCreate + 1);
        BreastFeed testBreastFeed = breastFeedList.get(breastFeedList.size() - 1);
        assertThat(testBreastFeed.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testBreastFeed.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testBreastFeed.getPain()).isEqualTo(DEFAULT_PAIN);
    }

    @Test
    @Transactional
    void createBreastFeedWithExistingId() throws Exception {
        // Create the BreastFeed with an existing ID
        breastFeed.setId(1L);
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(breastFeed);

        int databaseSizeBeforeCreate = breastFeedRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBreastFeedMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(breastFeedDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBreastFeeds() throws Exception {
        // Initialize the database
        breastFeedRepository.saveAndFlush(breastFeed);

        // Get all the breastFeedList
        restBreastFeedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(breastFeed.getId().intValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(DEFAULT_END))))
            .andExpect(jsonPath("$.[*].pain").value(hasItem(DEFAULT_PAIN.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBreastFeedsWithEagerRelationshipsIsEnabled() throws Exception {
        when(breastFeedServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBreastFeedMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(breastFeedServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBreastFeedsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(breastFeedServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBreastFeedMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(breastFeedServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getBreastFeed() throws Exception {
        // Initialize the database
        breastFeedRepository.saveAndFlush(breastFeed);

        // Get the breastFeed
        restBreastFeedMockMvc
            .perform(get(ENTITY_API_URL_ID, breastFeed.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(breastFeed.getId().intValue()))
            .andExpect(jsonPath("$.start").value(sameInstant(DEFAULT_START)))
            .andExpect(jsonPath("$.end").value(sameInstant(DEFAULT_END)))
            .andExpect(jsonPath("$.pain").value(DEFAULT_PAIN.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBreastFeed() throws Exception {
        // Get the breastFeed
        restBreastFeedMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBreastFeed() throws Exception {
        // Initialize the database
        breastFeedRepository.saveAndFlush(breastFeed);

        int databaseSizeBeforeUpdate = breastFeedRepository.findAll().size();

        // Update the breastFeed
        BreastFeed updatedBreastFeed = breastFeedRepository.findById(breastFeed.getId()).get();
        // Disconnect from session so that the updates on updatedBreastFeed are not directly saved in db
        em.detach(updatedBreastFeed);
        updatedBreastFeed.start(UPDATED_START).end(UPDATED_END).pain(UPDATED_PAIN);
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(updatedBreastFeed);

        restBreastFeedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, breastFeedDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
            )
            .andExpect(status().isOk());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeUpdate);
        BreastFeed testBreastFeed = breastFeedList.get(breastFeedList.size() - 1);
        assertThat(testBreastFeed.getStart()).isEqualTo(UPDATED_START);
        assertThat(testBreastFeed.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testBreastFeed.getPain()).isEqualTo(UPDATED_PAIN);
    }

    @Test
    @Transactional
    void putNonExistingBreastFeed() throws Exception {
        int databaseSizeBeforeUpdate = breastFeedRepository.findAll().size();
        breastFeed.setId(count.incrementAndGet());

        // Create the BreastFeed
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(breastFeed);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBreastFeedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, breastFeedDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBreastFeed() throws Exception {
        int databaseSizeBeforeUpdate = breastFeedRepository.findAll().size();
        breastFeed.setId(count.incrementAndGet());

        // Create the BreastFeed
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(breastFeed);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBreastFeedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBreastFeed() throws Exception {
        int databaseSizeBeforeUpdate = breastFeedRepository.findAll().size();
        breastFeed.setId(count.incrementAndGet());

        // Create the BreastFeed
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(breastFeed);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBreastFeedMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(breastFeedDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBreastFeedWithPatch() throws Exception {
        // Initialize the database
        breastFeedRepository.saveAndFlush(breastFeed);

        int databaseSizeBeforeUpdate = breastFeedRepository.findAll().size();

        // Update the breastFeed using partial update
        BreastFeed partialUpdatedBreastFeed = new BreastFeed();
        partialUpdatedBreastFeed.setId(breastFeed.getId());

        partialUpdatedBreastFeed.start(UPDATED_START).end(UPDATED_END);

        restBreastFeedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBreastFeed.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBreastFeed))
            )
            .andExpect(status().isOk());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeUpdate);
        BreastFeed testBreastFeed = breastFeedList.get(breastFeedList.size() - 1);
        assertThat(testBreastFeed.getStart()).isEqualTo(UPDATED_START);
        assertThat(testBreastFeed.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testBreastFeed.getPain()).isEqualTo(DEFAULT_PAIN);
    }

    @Test
    @Transactional
    void fullUpdateBreastFeedWithPatch() throws Exception {
        // Initialize the database
        breastFeedRepository.saveAndFlush(breastFeed);

        int databaseSizeBeforeUpdate = breastFeedRepository.findAll().size();

        // Update the breastFeed using partial update
        BreastFeed partialUpdatedBreastFeed = new BreastFeed();
        partialUpdatedBreastFeed.setId(breastFeed.getId());

        partialUpdatedBreastFeed.start(UPDATED_START).end(UPDATED_END).pain(UPDATED_PAIN);

        restBreastFeedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBreastFeed.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBreastFeed))
            )
            .andExpect(status().isOk());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeUpdate);
        BreastFeed testBreastFeed = breastFeedList.get(breastFeedList.size() - 1);
        assertThat(testBreastFeed.getStart()).isEqualTo(UPDATED_START);
        assertThat(testBreastFeed.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testBreastFeed.getPain()).isEqualTo(UPDATED_PAIN);
    }

    @Test
    @Transactional
    void patchNonExistingBreastFeed() throws Exception {
        int databaseSizeBeforeUpdate = breastFeedRepository.findAll().size();
        breastFeed.setId(count.incrementAndGet());

        // Create the BreastFeed
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(breastFeed);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBreastFeedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, breastFeedDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBreastFeed() throws Exception {
        int databaseSizeBeforeUpdate = breastFeedRepository.findAll().size();
        breastFeed.setId(count.incrementAndGet());

        // Create the BreastFeed
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(breastFeed);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBreastFeedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBreastFeed() throws Exception {
        int databaseSizeBeforeUpdate = breastFeedRepository.findAll().size();
        breastFeed.setId(count.incrementAndGet());

        // Create the BreastFeed
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(breastFeed);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBreastFeedMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BreastFeed in the database
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBreastFeed() throws Exception {
        // Initialize the database
        breastFeedRepository.saveAndFlush(breastFeed);

        int databaseSizeBeforeDelete = breastFeedRepository.findAll().size();

        // Delete the breastFeed
        restBreastFeedMockMvc
            .perform(delete(ENTITY_API_URL_ID, breastFeed.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
