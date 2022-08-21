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
import com.mamazinha.baby.domain.Height;
import com.mamazinha.baby.repository.BabyProfileRepository;
import com.mamazinha.baby.repository.HeightRepository;
import com.mamazinha.baby.security.CustomUser;
import com.mamazinha.baby.service.HeightService;
import com.mamazinha.baby.service.dto.HeightDTO;
import com.mamazinha.baby.service.mapper.HeightMapper;
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
 * Integration tests for the {@link HeightResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HeightResourceIT {

    private static final Double DEFAULT_VALUE = 1D;
    private static final Double UPDATED_VALUE = 2D;

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/heights";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HeightRepository heightRepository;

    @Mock
    private HeightRepository heightRepositoryMock;

    @Autowired
    private HeightMapper heightMapper;

    @Mock
    private HeightService heightServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHeightMockMvc;

    private Height height;

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
    public static Height createEntity(EntityManager em) {
        Height height = new Height().value(DEFAULT_VALUE).date(DEFAULT_DATE);
        return height;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Height createUpdatedEntity(EntityManager em) {
        Height height = new Height().value(UPDATED_VALUE).date(UPDATED_DATE);
        return height;
    }

    @BeforeEach
    public void initTest() {
        Mockito.when(clock.instant()).thenReturn(Clock.systemDefaultZone().instant());
        Mockito.when(clock.getZone()).thenReturn(Clock.systemDefaultZone().getZone());
        height = createEntity(em);
    }

    @Test
    @Transactional
    void createHeight() throws Exception {
        int databaseSizeBeforeCreate = heightRepository.findAll().size();
        // Create the Height
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        HeightDTO heightDTO = heightMapper.toDto(height.babyProfile(babyProfile));
        restHeightMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(heightDTO))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isCreated());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeCreate + 1);
        Height testHeight = heightList.get(heightList.size() - 1);
        assertThat(testHeight.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testHeight.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createHeightWithExistingId() throws Exception {
        // Create the Height with an existing ID
        height.setId(1L);
        HeightDTO heightDTO = heightMapper.toDto(height);

        int databaseSizeBeforeCreate = heightRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHeightMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(heightDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = heightRepository.findAll().size();
        // set the field null
        height.setValue(null);

        // Create the Height, which fails.
        HeightDTO heightDTO = heightMapper.toDto(height);

        restHeightMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(heightDTO)))
            .andExpect(status().isBadRequest());

        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = heightRepository.findAll().size();
        // set the field null
        height.setDate(null);

        // Create the Height, which fails.
        HeightDTO heightDTO = heightMapper.toDto(height);

        restHeightMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(heightDTO)))
            .andExpect(status().isBadRequest());

        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHeights() throws Exception {
        // Initialize the database
        heightRepository.saveAndFlush(height);

        // Get all the heightList
        restHeightMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(height.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHeightsWithEagerRelationshipsIsEnabled() throws Exception {
        when(heightServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHeightMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(heightServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHeightsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(heightServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHeightMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(heightServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getAllHeightsWhithUserRole() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));
        height.babyProfile(babyProfile);
        heightRepository.saveAndFlush(height);

        // Get all the heightList
        restHeightMockMvc
            .perform(
                get(ENTITY_API_URL + "?sort=id,desc")
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(height.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));
    }

    @Test
    @Transactional
    void shouldThrowAccessDeniedExceptionWhenGetLatestHeightByBabyProfile() throws Exception {
        // given
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        heightRepository.saveAndFlush(height.babyProfile(babyProfile));
        // when
        restHeightMockMvc
            .perform(
                get(ENTITY_API_URL + "/latest-height-by-baby-profile/{id}", babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", "another userId value", "ROLE_USER")))
            )
            // then
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void getLatestHeightByBabyProfile() throws Exception {
        // given
        Integer year = 2021;
        Integer month = 10;
        Integer day = 28;
        Integer hour = 8;
        // Initialize the database
        mockClockFixed(year, month, day, hour, 35, 00, null);
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));
        heightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 3, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        heightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 2, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        heightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 1, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        heightRepository
            .saveAndFlush(height.date((ZonedDateTime.of(year, month, day, hour, 30, 0, 0, ZoneId.systemDefault()))))
            .babyProfile(babyProfile);

        // when
        restHeightMockMvc
            .perform(
                get(ENTITY_API_URL + "/latest-height-by-baby-profile/{id}", babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(height.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.doubleValue()))
            .andExpect(jsonPath("$.date").value(sameInstant((ZonedDateTime.of(year, month, day, hour, 30, 0, 0, ZoneId.systemDefault())))));
    }

    @ParameterizedTest
    @CsvSource({ "false", "true" })
    @Transactional
    void getLastHeightsByDaysByBabyProfile(boolean withTimeZone) throws Exception {
        // given
        Integer year = 2021;
        Integer month = 10;
        Integer day = 28;
        Integer hour = 8;
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em));
        heightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 3, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        heightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 2, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        heightRepository.saveAndFlush(
            createEntity(em).date((ZonedDateTime.of(year, month, day - 1, hour, 30, 0, 0, ZoneId.systemDefault()))).babyProfile(babyProfile)
        );
        heightRepository
            .saveAndFlush(height.date((ZonedDateTime.of(year, month, day, hour, 30, 0, 0, ZoneId.systemDefault()))))
            .babyProfile(babyProfile);

        // when
        URI uri;
        if (withTimeZone) {
            String timeZone = "America/Sao_Paulo";
            mockClockFixed(year, month, day, hour, 35, 00, timeZone);
            uri =
                new URI(
                    ENTITY_API_URL + "/last-heights-by-days-by-baby-profile/" + babyProfile.getId() + "?tz=" + timeZone + "&days=" + 30
                );
        } else {
            mockClockFixed(year, month, day, hour, 35, 00, null);
            uri = new URI(ENTITY_API_URL + "/last-heights-by-days-by-baby-profile/" + babyProfile.getId() + "?days=" + 30);
        }
        restHeightMockMvc
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
    void getHeight() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        heightRepository.saveAndFlush(height.babyProfile(babyProfile));

        // Get the height
        restHeightMockMvc
            .perform(
                get(ENTITY_API_URL_ID, height.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(height.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.doubleValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingHeight() throws Exception {
        // Get the height
        restHeightMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHeight() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        heightRepository.saveAndFlush(height.babyProfile(babyProfile));

        int databaseSizeBeforeUpdate = heightRepository.findAll().size();

        // Update the height
        Height updatedHeight = heightRepository.findById(height.getId()).get();
        // Disconnect from session so that the updates on updatedHeight are not directly
        // saved in db
        em.detach(updatedHeight);
        updatedHeight.value(UPDATED_VALUE).date(UPDATED_DATE);
        HeightDTO heightDTO = heightMapper.toDto(updatedHeight);

        restHeightMockMvc
            .perform(
                put(ENTITY_API_URL_ID, heightDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(heightDTO))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeUpdate);
        Height testHeight = heightList.get(heightList.size() - 1);
        assertThat(testHeight.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testHeight.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingHeight() throws Exception {
        int databaseSizeBeforeUpdate = heightRepository.findAll().size();
        height.setId(count.incrementAndGet());

        // Create the Height
        HeightDTO heightDTO = heightMapper.toDto(height);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHeightMockMvc
            .perform(
                put(ENTITY_API_URL_ID, heightDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(heightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHeight() throws Exception {
        int databaseSizeBeforeUpdate = heightRepository.findAll().size();
        height.setId(count.incrementAndGet());

        // Create the Height
        HeightDTO heightDTO = heightMapper.toDto(height);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeightMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(heightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHeight() throws Exception {
        int databaseSizeBeforeUpdate = heightRepository.findAll().size();
        height.setId(count.incrementAndGet());

        // Create the Height
        HeightDTO heightDTO = heightMapper.toDto(height);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeightMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(heightDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHeightWithPatch() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        heightRepository.saveAndFlush(height.babyProfile(babyProfile));

        int databaseSizeBeforeUpdate = heightRepository.findAll().size();

        // Update the height using partial update
        Height partialUpdatedHeight = new Height();
        partialUpdatedHeight.setId(height.getId());

        restHeightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHeight.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHeight))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeUpdate);
        Height testHeight = heightList.get(heightList.size() - 1);
        assertThat(testHeight.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testHeight.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void fullUpdateHeightWithPatch() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        heightRepository.saveAndFlush(height.babyProfile(babyProfile));

        int databaseSizeBeforeUpdate = heightRepository.findAll().size();

        // Update the height using partial update
        Height partialUpdatedHeight = new Height();
        partialUpdatedHeight.setId(height.getId());

        partialUpdatedHeight.value(UPDATED_VALUE).date(UPDATED_DATE);

        restHeightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHeight.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHeight))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeUpdate);
        Height testHeight = heightList.get(heightList.size() - 1);
        assertThat(testHeight.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testHeight.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingHeight() throws Exception {
        int databaseSizeBeforeUpdate = heightRepository.findAll().size();
        height.setId(count.incrementAndGet());

        // Create the Height
        HeightDTO heightDTO = heightMapper.toDto(height);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHeightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, heightDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(heightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHeight() throws Exception {
        int databaseSizeBeforeUpdate = heightRepository.findAll().size();
        height.setId(count.incrementAndGet());

        // Create the Height
        HeightDTO heightDTO = heightMapper.toDto(height);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(heightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHeight() throws Exception {
        int databaseSizeBeforeUpdate = heightRepository.findAll().size();
        height.setId(count.incrementAndGet());

        // Create the Height
        HeightDTO heightDTO = heightMapper.toDto(height);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeightMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(heightDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Height in the database
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHeight() throws Exception {
        // Initialize the database
        BabyProfile babyProfile = babyProfileRepository.saveAndFlush(BabyProfileResourceIT.createEntity(em).userId("11111111"));
        heightRepository.saveAndFlush(height.babyProfile(babyProfile));

        int databaseSizeBeforeDelete = heightRepository.findAll().size();

        // Delete the height
        restHeightMockMvc
            .perform(
                delete(ENTITY_API_URL_ID, height.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Height> heightList = heightRepository.findAll();
        assertThat(heightList).hasSize(databaseSizeBeforeDelete - 1);
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
