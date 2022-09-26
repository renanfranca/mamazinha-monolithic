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
import com.mamazinha.baby.repository.BabyProfileRepository;
import com.mamazinha.baby.security.CustomUser;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.service.mapper.BabyProfileMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link BabyProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BabyProfileResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PICTURE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PICTURE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PICTURE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PICTURE_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_BIRTHDAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_BIRTHDAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_SIGN = "AAAAAAAAAA";
    private static final String UPDATED_SIGN = "BBBBBBBBBB";

    private static final Boolean DEFAULT_MAIN = false;
    private static final Boolean UPDATED_MAIN = true;

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/baby-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BabyProfileRepository babyProfileRepository;

    @Autowired
    private BabyProfileMapper babyProfileMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBabyProfileMockMvc;

    private BabyProfile babyProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BabyProfile createEntity(EntityManager em) {
        BabyProfile babyProfile = new BabyProfile()
            .name(DEFAULT_NAME)
            .picture(DEFAULT_PICTURE)
            .pictureContentType(DEFAULT_PICTURE_CONTENT_TYPE)
            .birthday(DEFAULT_BIRTHDAY)
            .sign(DEFAULT_SIGN)
            .main(DEFAULT_MAIN)
            .userId(DEFAULT_USER_ID);
        return babyProfile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BabyProfile createUpdatedEntity(EntityManager em) {
        BabyProfile babyProfile = new BabyProfile()
            .name(UPDATED_NAME)
            .picture(UPDATED_PICTURE)
            .pictureContentType(UPDATED_PICTURE_CONTENT_TYPE)
            .birthday(UPDATED_BIRTHDAY)
            .sign(UPDATED_SIGN)
            .main(UPDATED_MAIN)
            .userId(UPDATED_USER_ID);
        return babyProfile;
    }

    @BeforeEach
    public void initTest() {
        babyProfile = createEntity(em);
    }

    @Test
    @Transactional
    void createBabyProfile() throws Exception {
        int databaseSizeBeforeCreate = babyProfileRepository.findAll().size();
        // Create the BabyProfile
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);
        restBabyProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeCreate + 1);
        BabyProfile testBabyProfile = babyProfileList.get(babyProfileList.size() - 1);
        assertThat(testBabyProfile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBabyProfile.getPicture()).isEqualTo(DEFAULT_PICTURE);
        assertThat(testBabyProfile.getPictureContentType()).isEqualTo(DEFAULT_PICTURE_CONTENT_TYPE);
        assertThat(testBabyProfile.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testBabyProfile.getSign()).isEqualTo(DEFAULT_SIGN);
        assertThat(testBabyProfile.getMain()).isEqualTo(DEFAULT_MAIN);
        assertThat(testBabyProfile.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void shouldThrowAccessDeniedExceptionWhenReachMaxBabyProfileAllowedForUserRoleAuthority() throws Exception {
        // given
        babyProfileRepository.saveAndFlush(createEntity(em));
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);
        // when
        restBabyProfileMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            // then
            .andExpect(status().isForbidden())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void createBabyProfileWithExistingId() throws Exception {
        // Create the BabyProfile with an existing ID
        babyProfile.setId(1L);
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);

        int databaseSizeBeforeCreate = babyProfileRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBabyProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = babyProfileRepository.findAll().size();
        // set the field null
        babyProfile.setName(null);

        // Create the BabyProfile, which fails.
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);

        restBabyProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isBadRequest());

        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBirthdayIsRequired() throws Exception {
        int databaseSizeBeforeTest = babyProfileRepository.findAll().size();
        // set the field null
        babyProfile.setBirthday(null);

        // Create the BabyProfile, which fails.
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);

        restBabyProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isBadRequest());

        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBabyProfiles() throws Exception {
        // Initialize the database
        babyProfileRepository.saveAndFlush(babyProfile);

        // Get all the babyProfileList
        restBabyProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(babyProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].pictureContentType").value(hasItem(DEFAULT_PICTURE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].picture").value(hasItem(Base64Utils.encodeToString(DEFAULT_PICTURE))))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(sameInstant(DEFAULT_BIRTHDAY))))
            .andExpect(jsonPath("$.[*].sign").value(hasItem(DEFAULT_SIGN)))
            .andExpect(jsonPath("$.[*].main").value(hasItem(DEFAULT_MAIN.booleanValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)));
    }

    @Test
    @Transactional
    void getBabyProfile() throws Exception {
        // Initialize the database
        babyProfileRepository.saveAndFlush(babyProfile);

        // Get the babyProfile
        restBabyProfileMockMvc
            .perform(
                get(ENTITY_API_URL_ID, babyProfile.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(babyProfile.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.pictureContentType").value(DEFAULT_PICTURE_CONTENT_TYPE))
            .andExpect(jsonPath("$.picture").value(Base64Utils.encodeToString(DEFAULT_PICTURE)))
            .andExpect(jsonPath("$.birthday").value(sameInstant(DEFAULT_BIRTHDAY)))
            .andExpect(jsonPath("$.sign").value(DEFAULT_SIGN))
            .andExpect(jsonPath("$.main").value(DEFAULT_MAIN.booleanValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID));
    }

    @Test
    @Transactional
    void getNonExistingBabyProfile() throws Exception {
        // Get the babyProfile
        restBabyProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBabyProfile() throws Exception {
        // Initialize the database
        babyProfileRepository.saveAndFlush(babyProfile);

        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();

        // Update the babyProfile
        BabyProfile updatedBabyProfile = babyProfileRepository.findById(babyProfile.getId()).get();
        // Disconnect from session so that the updates on updatedBabyProfile are not
        // directly saved in db
        em.detach(updatedBabyProfile);
        updatedBabyProfile
            .name(UPDATED_NAME)
            .picture(UPDATED_PICTURE)
            .pictureContentType(UPDATED_PICTURE_CONTENT_TYPE)
            .birthday(UPDATED_BIRTHDAY)
            .sign(UPDATED_SIGN)
            .main(UPDATED_MAIN)
            .userId(UPDATED_USER_ID);
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(updatedBabyProfile);

        restBabyProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, babyProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isOk());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
        BabyProfile testBabyProfile = babyProfileList.get(babyProfileList.size() - 1);
        assertThat(testBabyProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBabyProfile.getPicture()).isEqualTo(UPDATED_PICTURE);
        assertThat(testBabyProfile.getPictureContentType()).isEqualTo(UPDATED_PICTURE_CONTENT_TYPE);
        assertThat(testBabyProfile.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testBabyProfile.getSign()).isEqualTo(UPDATED_SIGN);
        assertThat(testBabyProfile.getMain()).isEqualTo(UPDATED_MAIN);
        assertThat(testBabyProfile.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void putNewBabyProfileWithUserRole() throws Exception {
        // Initialize the database
        babyProfileRepository.saveAndFlush(babyProfile);

        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();

        // Update the babyProfile
        BabyProfile updatedBabyProfile = babyProfileRepository.findById(babyProfile.getId()).get();
        // Disconnect from session so that the updates on updatedBabyProfile are not
        // directly saved in db
        em.detach(updatedBabyProfile);
        updatedBabyProfile
            .name(UPDATED_NAME)
            .picture(UPDATED_PICTURE)
            .pictureContentType(UPDATED_PICTURE_CONTENT_TYPE)
            .birthday(UPDATED_BIRTHDAY)
            .sign(UPDATED_SIGN)
            .main(UPDATED_MAIN)
            .userId(UPDATED_USER_ID);
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(updatedBabyProfile);

        restBabyProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, babyProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", DEFAULT_USER_ID, "ROLE_USER")))
            )
            .andExpect(status().isOk());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
        BabyProfile testBabyProfile = babyProfileList.get(babyProfileList.size() - 1);
        assertThat(testBabyProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBabyProfile.getPicture()).isEqualTo(UPDATED_PICTURE);
        assertThat(testBabyProfile.getPictureContentType()).isEqualTo(UPDATED_PICTURE_CONTENT_TYPE);
        assertThat(testBabyProfile.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testBabyProfile.getSign()).isEqualTo(UPDATED_SIGN);
        assertThat(testBabyProfile.getMain()).isEqualTo(UPDATED_MAIN);
        assertThat(testBabyProfile.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void putNonExistingBabyProfile() throws Exception {
        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();
        babyProfile.setId(count.incrementAndGet());

        // Create the BabyProfile
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBabyProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, babyProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBabyProfile() throws Exception {
        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();
        babyProfile.setId(count.incrementAndGet());

        // Create the BabyProfile
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBabyProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBabyProfile() throws Exception {
        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();
        babyProfile.setId(count.incrementAndGet());

        // Create the BabyProfile
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBabyProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(babyProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBabyProfileWithPatch() throws Exception {
        // Initialize the database
        babyProfileRepository.saveAndFlush(babyProfile);

        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();

        // Update the babyProfile using partial update
        BabyProfile partialUpdatedBabyProfile = new BabyProfile();
        partialUpdatedBabyProfile.setId(babyProfile.getId());

        partialUpdatedBabyProfile.name(UPDATED_NAME).picture(UPDATED_PICTURE).pictureContentType(UPDATED_PICTURE_CONTENT_TYPE);

        restBabyProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBabyProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBabyProfile))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
        BabyProfile testBabyProfile = babyProfileList.get(babyProfileList.size() - 1);
        assertThat(testBabyProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBabyProfile.getPicture()).isEqualTo(UPDATED_PICTURE);
        assertThat(testBabyProfile.getPictureContentType()).isEqualTo(UPDATED_PICTURE_CONTENT_TYPE);
        assertThat(testBabyProfile.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testBabyProfile.getSign()).isEqualTo(DEFAULT_SIGN);
        assertThat(testBabyProfile.getMain()).isEqualTo(DEFAULT_MAIN);
        assertThat(testBabyProfile.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void fullUpdateBabyProfileWithPatch() throws Exception {
        // Initialize the database
        babyProfileRepository.saveAndFlush(babyProfile);

        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();

        // Update the babyProfile using partial update
        BabyProfile partialUpdatedBabyProfile = new BabyProfile();
        partialUpdatedBabyProfile.setId(babyProfile.getId());

        partialUpdatedBabyProfile
            .name(UPDATED_NAME)
            .picture(UPDATED_PICTURE)
            .pictureContentType(UPDATED_PICTURE_CONTENT_TYPE)
            .birthday(UPDATED_BIRTHDAY)
            .sign(UPDATED_SIGN)
            .main(UPDATED_MAIN)
            .userId(UPDATED_USER_ID);

        restBabyProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBabyProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBabyProfile))
                    .with(SecurityMockMvcRequestPostProcessors.user(new CustomUser("user", "1234", babyProfile.getUserId(), "ROLE_USER")))
            )
            .andExpect(status().isOk());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
        BabyProfile testBabyProfile = babyProfileList.get(babyProfileList.size() - 1);
        assertThat(testBabyProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBabyProfile.getPicture()).isEqualTo(UPDATED_PICTURE);
        assertThat(testBabyProfile.getPictureContentType()).isEqualTo(UPDATED_PICTURE_CONTENT_TYPE);
        assertThat(testBabyProfile.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testBabyProfile.getSign()).isEqualTo(UPDATED_SIGN);
        assertThat(testBabyProfile.getMain()).isEqualTo(UPDATED_MAIN);
        assertThat(testBabyProfile.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void patchNonExistingBabyProfile() throws Exception {
        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();
        babyProfile.setId(count.incrementAndGet());

        // Create the BabyProfile
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBabyProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, babyProfileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBabyProfile() throws Exception {
        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();
        babyProfile.setId(count.incrementAndGet());

        // Create the BabyProfile
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBabyProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBabyProfile() throws Exception {
        int databaseSizeBeforeUpdate = babyProfileRepository.findAll().size();
        babyProfile.setId(count.incrementAndGet());

        // Create the BabyProfile
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(babyProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBabyProfileMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BabyProfile in the database
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBabyProfile() throws Exception {
        // Initialize the database
        babyProfileRepository.saveAndFlush(babyProfile);

        int databaseSizeBeforeDelete = babyProfileRepository.findAll().size();

        // Delete the babyProfile
        restBabyProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, babyProfile.getId()).accept(MediaType.APPLICATION_JSON).with(user("admin").roles("ADMIN")))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        assertThat(babyProfileList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void shouldRemainOnlyOneBabyProfileForSameUserAsMain() throws Exception {
        // given
        babyProfileRepository.saveAndFlush(createEntity(em).main(true));
        babyProfileRepository.saveAndFlush(createEntity(em).main(true));
        babyProfileRepository.saveAndFlush(createEntity(em).main(true));

        // when
        // Create the BabyProfile
        BabyProfileDTO babyProfileDTO = babyProfileMapper.toDto(createEntity(em).main(true));
        restBabyProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(babyProfileDTO))
            )
            .andExpect(status().isCreated());
        // then
        List<BabyProfile> babyProfileList = babyProfileRepository.findAll();
        BabyProfile testBabyProfile = babyProfileList.get(babyProfileList.size() - 1);
        assertThat(babyProfileRepository.findByUserId(Pageable.unpaged(), DEFAULT_USER_ID))
            .isNotEmpty()
            .allSatisfy(
                new Consumer<BabyProfile>() {
                    @Override
                    public void accept(BabyProfile babyProfile) {
                        if (testBabyProfile.getId() == babyProfile.getId()) {
                            assertThat(babyProfile.getMain()).isTrue();
                        } else {
                            assertThat(babyProfile.getMain()).isFalse();
                        }
                    }
                }
            );
    }
}
