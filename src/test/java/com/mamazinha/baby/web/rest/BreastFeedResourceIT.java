package com.mamazinha.baby.web.rest;

import static com.mamazinha.baby.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mamazinha.baby.IntegrationTest;
import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.domain.BreastFeed;
import com.mamazinha.baby.domain.enumeration.Pain;
import com.mamazinha.baby.repository.BabyProfileRepository;
import com.mamazinha.baby.repository.BreastFeedRepository;
import com.mamazinha.baby.security.CustomUser;
import com.mamazinha.baby.service.BreastFeedService;
import com.mamazinha.baby.service.dto.BreastFeedDTO;
import com.mamazinha.baby.service.mapper.BreastFeedMapper;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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

    @Mock
    private BreastFeedService breastFeedServiceMock;

    @Autowired
    private BreastFeedMapper breastFeedMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBreastFeedMockMvc;

    private BreastFeed breastFeed;

    @MockBean
    private Clock clock;

    @Autowired
    private BabyProfileRepository babyProfileRepository;

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
        Mockito.when(clock.instant()).thenReturn(Clock.systemDefaultZone().instant());
        Mockito.when(clock.getZone()).thenReturn(Clock.systemDefaultZone().getZone());
        breastFeed = createEntity(em);
    }

    @Test
    @Transactional
    void createBreastFeed() throws Exception {
        int databaseSizeBeforeCreate = breastFeedRepository.findAll().size();
        // Create the BreastFeed
        BreastFeedDTO breastFeedDTO = breastFeedMapper.toDto(breastFeed);
        restBreastFeedMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
                    .with(user("admin").roles("ADMIN"))
            )
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
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
                    .with(user("admin").roles("ADMIN"))
            )
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
            .perform(get(ENTITY_API_URL + "?sort=id,desc").with(user("admin").roles("ADMIN")))
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
            .perform(get(ENTITY_API_URL_ID, breastFeed.getId()).with(user("admin").roles("ADMIN")))
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
        restBreastFeedMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE).with(user("admin").roles("ADMIN")))
            .andExpect(status().isNotFound());
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
                    .with(user("admin").roles("ADMIN"))
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
                    .with(user("admin").roles("ADMIN"))
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
                    .with(user("admin").roles("ADMIN"))
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
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
                    .with(user("admin").roles("ADMIN"))
            )
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
                    .with(user("admin").roles("ADMIN"))
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
                    .with(user("admin").roles("ADMIN"))
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
                    .with(user("admin").roles("ADMIN"))
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
                    .with(user("admin").roles("ADMIN"))
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
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(breastFeedDTO))
                    .with(user("admin").roles("ADMIN"))
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
            .perform(delete(ENTITY_API_URL_ID, breastFeed.getId()).accept(MediaType.APPLICATION_JSON).with(user("admin").roles("ADMIN")))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BreastFeed> breastFeedList = breastFeedRepository.findAll();
        assertThat(breastFeedList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void shouldReturnTodayBreastFeeds(boolean withTimeZone) throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        int year = 2021;
        int month = 12;
        int day = 2;

        //last week
        createValidAndInvalidBreastFeedsByDate(year, month, day, babyProfile);

        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(year, month, day, 16, 30, 00, timeZone);
            uri = new URI(ENTITY_API_URL + "/today-breast-feeds-by-baby-profile/" + babyProfile.getId() + "?tz=" + timeZone);
        } else {
            mockClockFixed(year, month, day, 16, 30, 00, null);
            uri = new URI(ENTITY_API_URL + "/today-breast-feeds-by-baby-profile/" + babyProfile.getId());
        }
        restBreastFeedMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.size()").value(5))
            .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void shouldReturnAverageBreastFeedsInHourByDayFromLastWeekAndCurrentWeek(boolean withTimeZone) throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        int year = 2021;
        int month = 9;
        int day = 20;

        //last week
        createValidAndInvalidBreastFeedsByDate(year, month, 13, babyProfile);
        createValidAndInvalidBreastFeedsByDate(year, month, 19, babyProfile);

        //current week
        createValidAndInvalidBreastFeedsByDate(year, month, 20, babyProfile);
        createValidAndInvalidBreastFeedsByDate(year, month, 26, babyProfile);

        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(year, month, day, 16, 30, 00, timeZone);
            uri =
                new URI(
                    ENTITY_API_URL +
                    "/lastweek-currentweek-average-breast-feeds-in-hours-eachday-by-baby-profile/" +
                    babyProfile.getId() +
                    "?tz=" +
                    timeZone
                );
        } else {
            mockClockFixed(year, month, day, 16, 30, 00, null);
            uri =
                new URI(
                    ENTITY_API_URL + "/lastweek-currentweek-average-breast-feeds-in-hours-eachday-by-baby-profile/" + babyProfile.getId()
                );
        }
        restBreastFeedMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.lastWeekBreastFeeds.size()").value(7))
            .andExpect(jsonPath("$.currentWeekBreastFeeds.size()").value(7))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void shouldReturnIncompleteNapsByBabyProfile() throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        int year = 2021;
        int month = 10;
        int day = 25;

        mockClockFixed(year, month, day, 16, 30, 00, null);

        //valid
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault()))
                .end(null)
                .babyProfile(babyProfile)
        );
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 10, 0, 0, 0, ZoneId.systemDefault()))
                .end(null)
                .babyProfile(babyProfile)
        );

        //invalid
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 14, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 13, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );

        // when
        restBreastFeedMockMvc
            .perform(
                get(ENTITY_API_URL + "/incomplete-breast-feeds-by-baby-profile/{id}", babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.size()").value(2))
            .andDo(MockMvcResultHandlers.print());
    }

    private void createValidAndInvalidBreastFeedsByDate(Integer year, Integer month, Integer day, BabyProfile babyProfile) {
        // valid
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day - 1, 23, 30, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 0, 30, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 1, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 7, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 8, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 10, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 11, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 23, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 0, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 21, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 0, 30, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day + 3, 10, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 3, 11, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );

        // invalid
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day - 1, 8, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day - 1, 9, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day + 1, 8, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 9, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );
        breastFeedRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 8, 0, 0, 0, ZoneId.systemDefault()))
                .end(null)
                .babyProfile(babyProfile)
        );
    }

    private void mockClockFixed(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second, String timeZone) {
        LocalDateTime dataTimeFixedAtTest;
        if (hour != null && minute != null && second != null) {
            dataTimeFixedAtTest = LocalDateTime.of(year, month, day, hour, minute, second);
        } else {
            dataTimeFixedAtTest = LocalDate.of(year, month, day).atStartOfDay();
        }
        Clock fixedClock = Clock.fixed(dataTimeFixedAtTest.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        Mockito.when(clock.instant()).thenReturn(fixedClock.instant());
        Mockito.when(clock.getZone()).thenReturn(fixedClock.getZone());
        if (timeZone != null) {
            Mockito.when(clock.withZone(ZoneId.of(timeZone))).thenReturn(fixedClock);
        }
    }
}
