package com.mamazinha.baby.web.rest;

import static com.mamazinha.baby.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
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
import com.mamazinha.baby.domain.Humor;
import com.mamazinha.baby.domain.Nap;
import com.mamazinha.baby.domain.enumeration.Place;
import com.mamazinha.baby.repository.BabyProfileRepository;
import com.mamazinha.baby.repository.HumorRepository;
import com.mamazinha.baby.repository.NapRepository;
import com.mamazinha.baby.security.CustomUser;
import com.mamazinha.baby.service.dto.NapDTO;
import com.mamazinha.baby.service.mapper.NapMapper;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NapResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NapResourceIT {

    private static final ZonedDateTime DEFAULT_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_END = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Place DEFAULT_PLACE = Place.LAP;
    private static final Place UPDATED_PLACE = Place.BABY_CRIB;

    private static final String ENTITY_API_URL = "/api/naps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NapRepository napRepository;

    @Autowired
    private NapMapper napMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNapMockMvc;

    private Nap nap;

    @MockBean
    private Clock clock;

    @Autowired
    private BabyProfileRepository babyProfileRepository;

    @Autowired
    private HumorRepository humorRepository;

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
        Mockito.when(clock.instant()).thenReturn(Clock.systemDefaultZone().instant());
        Mockito.when(clock.getZone()).thenReturn(Clock.systemDefaultZone().getZone());
        nap = createEntity(em);
    }

    @Test
    @Transactional
    void createNap() throws Exception {
        int databaseSizeBeforeCreate = napRepository.findAll().size();
        // Create the Nap
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        NapDTO napDTO = napMapper.toDto(nap.babyProfile(babyProfile));
        restNapMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(napDTO))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
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
            .perform(get(ENTITY_API_URL + "?sort=id,desc").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nap.getId().intValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(DEFAULT_END))))
            .andExpect(jsonPath("$.[*].place").value(hasItem(DEFAULT_PLACE.toString())));
    }

    @Test
    @Transactional
    void getNap() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        napRepository.saveAndFlush(nap.babyProfile(babyProfile));

        // Get the nap
        restNapMockMvc
            .perform(
                get(ENTITY_API_URL_ID, nap.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
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
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        napRepository.saveAndFlush(nap.babyProfile(babyProfile));

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
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
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
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        napRepository.saveAndFlush(nap.babyProfile(babyProfile));

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
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
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
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        napRepository.saveAndFlush(nap.babyProfile(babyProfile));

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
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
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
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        napRepository.saveAndFlush(nap.babyProfile(babyProfile));

        int databaseSizeBeforeDelete = napRepository.findAll().size();

        // Delete the nap
        restNapMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, nap.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Nap> napList = napRepository.findAll();
        assertThat(napList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void shouldSumNapsInHoursOfToday(boolean withTimeZone) throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        createValidAndInvalidNapsByDate(2021, 9, 20, babyProfile);
        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(2021, 9, 20, 16, 30, 00, timeZone);
            uri = new URI(ENTITY_API_URL + "/today-sum-naps-in-hours-by-baby-profile/" + babyProfile.getId() + "?tz=" + timeZone);
        } else {
            mockClockFixed(2021, 9, 20, 16, 30, 00, null);
            uri = new URI(ENTITY_API_URL + "/today-sum-naps-in-hours-by-baby-profile/" + babyProfile.getId());
        }
        restNapMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.sleepHours").value(7.5))
            .andExpect(jsonPath("$.sleepHoursGoal").value(16));
    }

    @ParameterizedTest
    @CsvSource({ "0,0,1,22,1.4", "0,0,1,21,1.4", "0,0,1,18,1.3", "0,0,2,0,2" })
    @Transactional
    void shouldSumNapsInHoursOfTodayOnlyWithMaxOneDecimalPlaces(
        int startHour,
        int startMinute,
        int endHour,
        int endMinute,
        double expectedValue
    ) throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        int year = 2021;
        int month = 9;
        int day = 20;
        // valid
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, startHour, startMinute, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, endHour, endMinute, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.LAP)
        );
        // when
        URI uri;
        mockClockFixed(2021, 9, 20, 16, 30, 00, null);
        uri = new URI(ENTITY_API_URL + "/today-sum-naps-in-hours-by-baby-profile/" + babyProfile.getId());

        restNapMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.sleepHours").value(expectedValue))
            .andExpect(jsonPath("$.sleepHoursGoal").value(16));
    }

    @ParameterizedTest
    @CsvSource(
        {
            "today-sum-naps-in-hours-by-baby-profile, ",
            "lastweek-currentweek-sum-naps-in-hours-eachday-by-baby-profile, ",
            "today-average-nap-humor-by-baby-profile, ",
            "favorite-nap-place-from-last-days-by-baby-profile,?lastDays=30",
        }
    )
    @Transactional
    void shouldThrowAccessDeniedExceptionWhenUserIsNotTheTheBabyProfileIdOwner(String endPointUrl, String parameter) throws Exception {
        // given
        mockClockFixed(2021, 9, 20, 16, 30, 00, null);

        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(2021, 9, 19, 23, 30, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(2021, 9, 20, 0, 30, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );
        // when
        restNapMockMvc
            .perform(
                get(ENTITY_API_URL + "/" + endPointUrl + "/{id}" + (parameter == null ? "" : parameter), babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", "another userId value", "ROLE_USER")))
            )
            // then
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void shouldReturnSumNapsInHourByDayFromLastWeekAndCurrentWeek() throws Exception {
        // given
        mockClockFixed(2021, 9, 20, 16, 30, 00, null);

        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        //last week
        createValidAndInvalidNapsByDate(2021, 9, 13, babyProfile);
        createValidAndInvalidNapsByDate(2021, 9, 19, babyProfile);

        //current week
        createValidAndInvalidNapsByDate(2021, 9, 20, babyProfile);
        createValidAndInvalidNapsByDate(2021, 9, 26, babyProfile);
        // when
        restNapMockMvc
            .perform(
                get(ENTITY_API_URL + "/lastweek-currentweek-sum-naps-in-hours-eachday-by-baby-profile/{id}", babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.lastWeekNaps.size()").value(7))
            .andExpect(jsonPath("$.currentWeekNaps.size()").value(7))
            .andExpect(jsonPath("$.sleepHoursGoal").value(16));
    }

    @Test
    @Transactional
    void shouldReturnSumNapsInHourByDayFromLastWeekAndCurrentWeekEmptyNaps() throws Exception {
        // given
        mockClockFixed(2021, 9, 20, 16, 30, 00, null);

        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        // when
        restNapMockMvc
            .perform(
                get(ENTITY_API_URL + "/lastweek-currentweek-sum-naps-in-hours-eachday-by-baby-profile/{id}", babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.lastWeekNaps.size()").value(7))
            .andExpect(jsonPath("$.currentWeekNaps.size()").value(7))
            .andExpect(jsonPath("$.sleepHoursGoal").value(16))
            .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void shouldReturnAverageNapHumor(boolean withTimeZone) throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));
        Humor angryHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(1));
        Humor sadHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(2));
        Humor calmHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(3));
        Humor happyHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(4));
        Humor excitedHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(5));

        int year = 2021;
        int month = 10;
        int day = 23;
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 1, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .humor(angryHumor)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 15, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 16, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .humor(calmHumor)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 17, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 18, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .humor(happyHumor)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 22, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 23, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .humor(excitedHumor)
        );
        //invalid
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 23, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day - 1, 0, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .humor(sadHumor)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 23, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 0, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .humor(sadHumor)
        );

        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(year, month, day, 16, 30, 00, timeZone);
            uri = new URI(ENTITY_API_URL + "/today-average-nap-humor-by-baby-profile/" + babyProfile.getId() + "?tz=" + timeZone);
        } else {
            mockClockFixed(year, month, day, 16, 30, 00, null);
            uri = new URI(ENTITY_API_URL + "/today-average-nap-humor-by-baby-profile/" + babyProfile.getId());
        }
        restNapMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.dayOfWeek").value(6))
            .andExpect(jsonPath("$.humorAverage").value(3))
            .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void shouldNotReturnAverageNapHumorWhenNapHumorIsNull(boolean withTimeZone) throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        int year = 2021;
        int month = 10;
        int day = 23;
        createValidAndInvalidNapsByDate(year, month, day, babyProfile);
        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(year, month, day, 16, 30, 00, timeZone);
            uri = new URI(ENTITY_API_URL + "/today-average-nap-humor-by-baby-profile/" + babyProfile.getId() + "?tz=" + timeZone);
        } else {
            mockClockFixed(year, month, day, 16, 30, 00, null);
            uri = new URI(ENTITY_API_URL + "/today-average-nap-humor-by-baby-profile/" + babyProfile.getId());
        }
        restNapMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dayOfWeek").value(6))
            .andExpect(jsonPath("$.humorAverage").value(0))
            .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void shouldReturnAverageNapsHumorFromLastWeekAndCurrentWeek(boolean withTimeZone) throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        Humor angryHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(1));
        Humor sadHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(2));
        Humor calmHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(3));
        Humor happyHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(4));
        Humor excitedHumor = humorRepository.saveAndFlush(HumorResourceIT.createEntity(em).value(5));
        List<Humor> humorList = Arrays.asList(angryHumor, sadHumor, calmHumor, happyHumor, excitedHumor);

        int year = 2021;
        int month = 9;
        int day = 20;

        //last week
        createValidAndInvalidNapsByDateWithHumor(2021, 9, 13, babyProfile, humorList);
        createValidAndInvalidNapsByDateWithHumor(2021, 9, 19, babyProfile, humorList);

        //current week
        createValidAndInvalidNapsByDateWithHumor(2021, 9, 20, babyProfile, humorList);
        createValidAndInvalidNapsByDateWithHumor(2021, 9, 26, babyProfile, humorList);

        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(year, month, day, 16, 30, 00, timeZone);
            uri =
                new URI(
                    ENTITY_API_URL +
                    "/lastweek-currentweek-average-naps-humor-eachday-by-baby-profile/" +
                    babyProfile.getId() +
                    "?tz=" +
                    timeZone
                );
        } else {
            mockClockFixed(year, month, day, 16, 30, 00, null);
            uri = new URI(ENTITY_API_URL + "/lastweek-currentweek-average-naps-humor-eachday-by-baby-profile/" + babyProfile.getId());
        }
        restNapMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.lastWeekHumorAverage.size()").value(7))
            .andExpect(jsonPath("$.currentWeekHumorAverage.size()").value(7))
            .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void shouldReturnFavoriteNapPlaceFromTheLast30DaysByBabyProfile(boolean withTimeZone) throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        int year = 2021;
        int month = 10;
        int day = 25;

        createValidAndInvalidNapsByDate(year, month, day, babyProfile);
        Integer lastDays = 30;
        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(year, month, day, 16, 30, 00, timeZone);
            uri =
                new URI(
                    ENTITY_API_URL +
                    "/favorite-nap-place-from-last-days-by-baby-profile/" +
                    babyProfile.getId() +
                    "?lastDays=" +
                    lastDays +
                    "&tz=" +
                    timeZone
                );
        } else {
            mockClockFixed(year, month, day, 16, 30, 00, null);
            uri =
                new URI(
                    ENTITY_API_URL + "/favorite-nap-place-from-last-days-by-baby-profile/" + babyProfile.getId() + "?lastDays=" + lastDays
                );
        }
        restNapMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.periodInDays").value(30))
            .andExpect(jsonPath("$.favoritePlace").value(Place.LAP.name()))
            .andExpect(jsonPath("$.amountOfTimes").value(3));
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void shouldNotReturnFavoriteNapPlaceFromTheLast30DaysByBabyProfileWhenPlaceIsNull(boolean withTimeZone) throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));

        int year = 2021;
        int month = 10;
        int day = 25;

        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day - 1, 23, 30, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 0, 30, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(null)
        );
        Integer lastDays = 30;
        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(year, month, day, 16, 30, 00, timeZone);
            uri =
                new URI(
                    ENTITY_API_URL +
                    "/favorite-nap-place-from-last-days-by-baby-profile/" +
                    babyProfile.getId() +
                    "?lastDays=" +
                    lastDays +
                    "&tz=" +
                    timeZone
                );
        } else {
            mockClockFixed(year, month, day, 16, 30, 00, null);
            uri =
                new URI(
                    ENTITY_API_URL + "/favorite-nap-place-from-last-days-by-baby-profile/" + babyProfile.getId() + "?lastDays=" + lastDays
                );
        }
        restNapMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.periodInDays").value(30))
            .andExpect(jsonPath("$.favoritePlace").doesNotExist())
            .andExpect(jsonPath("$.amountOfTimes").doesNotExist());
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
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault()))
                .end(null)
                .babyProfile(babyProfile)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 10, 0, 0, 0, ZoneId.systemDefault()))
                .end(null)
                .babyProfile(babyProfile)
        );

        //invalid
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 14, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 13, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
        );

        // when
        restNapMockMvc
            .perform(
                get(ENTITY_API_URL + "/incomplete-naps-by-baby-profile/{id}", babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.size()").value(2))
            .andDo(MockMvcResultHandlers.print());
    }

    private void createValidAndInvalidNapsByDate(Integer year, Integer month, Integer day, BabyProfile babyProfile) {
        // valid
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day - 1, 23, 30, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 0, 30, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.LAP)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 1, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.LAP)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 7, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 8, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.LAP)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 10, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 11, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.CART)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 23, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 0, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.BED)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 21, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 0, 30, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.BABY_CRIB)
        );

        // invalid
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day - 1, 8, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day - 1, 9, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.BABY_CONFORT)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day + 1, 8, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 9, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.BABY_CONFORT)
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 8, 0, 0, 0, ZoneId.systemDefault()))
                .end(null)
                .babyProfile(babyProfile)
                .place(Place.BABY_CRIB)
        );
    }

    private void createValidAndInvalidNapsByDateWithHumor(
        Integer year,
        Integer month,
        Integer day,
        BabyProfile babyProfile,
        List<Humor> humorList
    ) {
        // valid
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day - 1, 23, 30, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 0, 30, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.LAP)
                .humor(humorList.get(0))
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 1, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.LAP)
                .humor(humorList.get(2))
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 7, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 8, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.LAP)
                .humor(humorList.get(3))
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 10, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day, 11, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.CART)
                .humor(humorList.get(4))
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 23, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 0, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.BED)
                .humor(humorList.get(4))
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 21, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 0, 30, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.BABY_CRIB)
                .humor(humorList.get(4))
        );

        // invalid
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day - 1, 8, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day - 1, 9, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.BABY_CONFORT)
                .humor(humorList.get(1))
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day + 1, 8, 0, 0, 0, ZoneId.systemDefault()))
                .end(ZonedDateTime.of(year, month, day + 1, 9, 0, 0, 0, ZoneId.systemDefault()))
                .babyProfile(babyProfile)
                .place(Place.BABY_CONFORT)
                .humor(humorList.get(1))
        );
        napRepository.saveAndFlush(
            createEntity(em)
                .start(ZonedDateTime.of(year, month, day, 8, 0, 0, 0, ZoneId.systemDefault()))
                .end(null)
                .babyProfile(babyProfile)
                .place(Place.BABY_CRIB)
                .humor(humorList.get(1))
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
