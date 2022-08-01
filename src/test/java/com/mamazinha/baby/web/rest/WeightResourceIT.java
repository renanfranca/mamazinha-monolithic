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
import com.mamazinha.baby.domain.Weight;
import com.mamazinha.baby.repository.BabyProfileRepository;
import com.mamazinha.baby.repository.WeightRepository;
import com.mamazinha.baby.security.CustomUser;
import com.mamazinha.baby.service.dto.WeightDTO;
import com.mamazinha.baby.service.mapper.WeightMapper;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link WeightResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WeightResourceIT {

    private static final Double DEFAULT_VALUE = 1D;
    private static final Double UPDATED_VALUE = 2D;

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/weights";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private WeightMapper weightMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWeightMockMvc;

    private Weight weight;

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
    public static Weight createEntity(EntityManager em) {
        Weight weight = new Weight().value(DEFAULT_VALUE).date(DEFAULT_DATE);
        return weight;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Weight createUpdatedEntity(EntityManager em) {
        Weight weight = new Weight().value(UPDATED_VALUE).date(UPDATED_DATE);
        return weight;
    }

    @BeforeEach
    public void initTest() {
        Mockito.when(clock.instant()).thenReturn(Clock.systemDefaultZone().instant());
        Mockito.when(clock.getZone()).thenReturn(Clock.systemDefaultZone().getZone());
        weight = createEntity(em);
    }

    @Test
    @Transactional
    void createWeight() throws Exception {
        int databaseSizeBeforeCreate = weightRepository.findAll().size();
        // Create the Weight
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        WeightDTO weightDTO = weightMapper.toDto(weight.babyProfile(babyProfile));
        restWeightMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(weightDTO))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isCreated());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeCreate + 1);
        Weight testWeight = weightList.get(weightList.size() - 1);
        assertThat(testWeight.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testWeight.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createWeightWithExistingId() throws Exception {
        // Create the Weight with an existing ID
        weight.setId(1L);
        WeightDTO weightDTO = weightMapper.toDto(weight);

        int databaseSizeBeforeCreate = weightRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWeightMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(weightDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = weightRepository.findAll().size();
        // set the field null
        weight.setValue(null);

        // Create the Weight, which fails.
        WeightDTO weightDTO = weightMapper.toDto(weight);

        restWeightMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(weightDTO)))
            .andExpect(status().isBadRequest());

        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = weightRepository.findAll().size();
        // set the field null
        weight.setDate(null);

        // Create the Weight, which fails.
        WeightDTO weightDTO = weightMapper.toDto(weight);

        restWeightMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(weightDTO)))
            .andExpect(status().isBadRequest());

        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWeights() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList
        restWeightMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(weight.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));
    }

    @Test
    @Transactional
    void getAllWeightsWhithUserRole() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));
        weight.babyProfile(babyProfile);
        weightRepository.saveAndFlush(weight);

        // Get all the weightList
        restWeightMockMvc
            .perform(
                get(ENTITY_API_URL + "?sort=id,desc")
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(weight.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));
    }

    @Test
    @Transactional
    void shouldThrowAccessDeniedExceptionWhenGetLatestWeightByBabyProfile() throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        weightRepository.saveAndFlush(weight.babyProfile(babyProfile));
        // when
        restWeightMockMvc
            .perform(
                get(ENTITY_API_URL + "/latest-weight-by-baby-profile/{id}", babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", "another userId value", "ROLE_USER")))
            )
            // then
            .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void getLastWeightsByDaysByBabyProfile(boolean withTimeZone) throws Exception {
        // given
        Integer year = 2021;
        Integer month = 10;
        Integer day = 28;
        Integer hour = 8;
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));
        weightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 3, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        weightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 2, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        weightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 1, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        weightRepository
            .saveAndFlush(weight.date((ZonedDateTime.of(year, month, day, hour, 30, 0, 0, ZoneId.systemDefault()))))
            .babyProfile(babyProfile);

        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(year, month, day, hour, 35, 00, timeZone);
            uri =
                new URI(
                    ENTITY_API_URL + "/last-weights-by-days-by-baby-profile/" + babyProfile.getId() + "?tz=" + timeZone + "&days=" + 30
                );
        } else {
            mockClockFixed(year, month, day, hour, 35, 00, null);
            uri = new URI(ENTITY_API_URL + "/last-weights-by-days-by-baby-profile/" + babyProfile.getId() + "?days=" + 30);
        }
        restWeightMockMvc
            .perform(
                get(uri)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.size()").value(4))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void getLatestWeightByBabyProfile() throws Exception {
        // given
        Integer year = 2021;
        Integer month = 10;
        Integer day = 28;
        Integer hour = 8;
        // Initialize the database
        mockClockFixed(year, month, day, hour, 35, 00, null);
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));
        weightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 3, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        weightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 2, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        weightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 1, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        weightRepository
            .saveAndFlush(weight.date((ZonedDateTime.of(year, month, day, hour, 30, 0, 0, ZoneId.systemDefault()))))
            .babyProfile(babyProfile);

        // when
        restWeightMockMvc
            .perform(
                get(ENTITY_API_URL + "/latest-weight-by-baby-profile/{id}", babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(weight.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.doubleValue()))
            .andExpect(jsonPath("$.date").value(sameInstant((ZonedDateTime.of(year, month, day, hour, 30, 0, 0, ZoneId.systemDefault())))));
    }

    @Test
    @Transactional
    void getWeight() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        weightRepository.saveAndFlush(weight.babyProfile(babyProfile));

        // Get the weight
        restWeightMockMvc
            .perform(
                get(ENTITY_API_URL_ID, weight.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(weight.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.doubleValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingWeight() throws Exception {
        // Get the weight
        restWeightMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWeight() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        weightRepository.saveAndFlush(weight.babyProfile(babyProfile));

        int databaseSizeBeforeUpdate = weightRepository.findAll().size();

        // Update the weight
        Weight updatedWeight = weightRepository.findById(weight.getId()).get();
        // Disconnect from session so that the updates on updatedWeight are not directly saved in db
        em.detach(updatedWeight);
        updatedWeight.value(UPDATED_VALUE).date(UPDATED_DATE);
        WeightDTO weightDTO = weightMapper.toDto(updatedWeight);

        restWeightMockMvc
            .perform(
                put(ENTITY_API_URL_ID, weightDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(weightDTO))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
        Weight testWeight = weightList.get(weightList.size() - 1);
        assertThat(testWeight.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testWeight.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingWeight() throws Exception {
        int databaseSizeBeforeUpdate = weightRepository.findAll().size();
        weight.setId(count.incrementAndGet());

        // Create the Weight
        WeightDTO weightDTO = weightMapper.toDto(weight);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWeightMockMvc
            .perform(
                put(ENTITY_API_URL_ID, weightDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(weightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWeight() throws Exception {
        int databaseSizeBeforeUpdate = weightRepository.findAll().size();
        weight.setId(count.incrementAndGet());

        // Create the Weight
        WeightDTO weightDTO = weightMapper.toDto(weight);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWeightMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(weightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWeight() throws Exception {
        int databaseSizeBeforeUpdate = weightRepository.findAll().size();
        weight.setId(count.incrementAndGet());

        // Create the Weight
        WeightDTO weightDTO = weightMapper.toDto(weight);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWeightMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(weightDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWeightWithPatch() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        weightRepository.saveAndFlush(weight.babyProfile(babyProfile));

        int databaseSizeBeforeUpdate = weightRepository.findAll().size();

        // Update the weight using partial update
        Weight partialUpdatedWeight = new Weight();
        partialUpdatedWeight.setId(weight.getId());

        partialUpdatedWeight.date(UPDATED_DATE);

        restWeightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWeight.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWeight))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
        Weight testWeight = weightList.get(weightList.size() - 1);
        assertThat(testWeight.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testWeight.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateWeightWithPatch() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        weightRepository.saveAndFlush(weight.babyProfile(babyProfile));

        int databaseSizeBeforeUpdate = weightRepository.findAll().size();

        // Update the weight using partial update
        Weight partialUpdatedWeight = new Weight();
        partialUpdatedWeight.setId(weight.getId());

        partialUpdatedWeight.value(UPDATED_VALUE).date(UPDATED_DATE);

        restWeightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWeight.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWeight))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
        Weight testWeight = weightList.get(weightList.size() - 1);
        assertThat(testWeight.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testWeight.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingWeight() throws Exception {
        int databaseSizeBeforeUpdate = weightRepository.findAll().size();
        weight.setId(count.incrementAndGet());

        // Create the Weight
        WeightDTO weightDTO = weightMapper.toDto(weight);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWeightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, weightDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(weightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWeight() throws Exception {
        int databaseSizeBeforeUpdate = weightRepository.findAll().size();
        weight.setId(count.incrementAndGet());

        // Create the Weight
        WeightDTO weightDTO = weightMapper.toDto(weight);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWeightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(weightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWeight() throws Exception {
        int databaseSizeBeforeUpdate = weightRepository.findAll().size();
        weight.setId(count.incrementAndGet());

        // Create the Weight
        WeightDTO weightDTO = weightMapper.toDto(weight);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWeightMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(weightDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWeight() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        weightRepository.saveAndFlush(weight.babyProfile(babyProfile));

        int databaseSizeBeforeDelete = weightRepository.findAll().size();

        // Delete the weight
        restWeightMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, weight.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeDelete - 1);
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
