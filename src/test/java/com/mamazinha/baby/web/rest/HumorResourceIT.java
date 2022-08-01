package com.mamazinha.baby.web.rest;

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
import com.mamazinha.baby.domain.Humor;
import com.mamazinha.baby.repository.HumorRepository;
import com.mamazinha.baby.service.dto.HumorDTO;
import com.mamazinha.baby.service.mapper.HumorMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link HumorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HumorResourceIT {

    private static final Integer DEFAULT_VALUE = 1;
    private static final Integer UPDATED_VALUE = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_EMOTICO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_EMOTICO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_EMOTICO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_EMOTICO_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/humors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HumorRepository humorRepository;

    @Autowired
    private HumorMapper humorMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHumorMockMvc;

    private Humor humor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Humor createEntity(EntityManager em) {
        Humor humor = new Humor()
            .value(DEFAULT_VALUE)
            .description(DEFAULT_DESCRIPTION)
            .emotico(DEFAULT_EMOTICO)
            .emoticoContentType(DEFAULT_EMOTICO_CONTENT_TYPE);
        return humor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Humor createUpdatedEntity(EntityManager em) {
        Humor humor = new Humor()
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION)
            .emotico(UPDATED_EMOTICO)
            .emoticoContentType(UPDATED_EMOTICO_CONTENT_TYPE);
        return humor;
    }

    @BeforeEach
    public void initTest() {
        humor = createEntity(em);
    }

    @Test
    @Transactional
    void createHumor() throws Exception {
        int databaseSizeBeforeCreate = humorRepository.findAll().size();
        // Create the Humor
        HumorDTO humorDTO = humorMapper.toDto(humor);
        restHumorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humorDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isCreated());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeCreate + 1);
        Humor testHumor = humorList.get(humorList.size() - 1);
        assertThat(testHumor.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testHumor.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testHumor.getEmotico()).isEqualTo(DEFAULT_EMOTICO);
        assertThat(testHumor.getEmoticoContentType()).isEqualTo(DEFAULT_EMOTICO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createHumorWithExistingId() throws Exception {
        // Create the Humor with an existing ID
        humor.setId(1L);
        HumorDTO humorDTO = humorMapper.toDto(humor);

        int databaseSizeBeforeCreate = humorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHumorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humorDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isBadRequest());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = humorRepository.findAll().size();
        // set the field null
        humor.setValue(null);

        // Create the Humor, which fails.
        HumorDTO humorDTO = humorMapper.toDto(humor);

        restHumorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humorDTO)))
            .andExpect(status().isBadRequest());

        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = humorRepository.findAll().size();
        // set the field null
        humor.setDescription(null);

        // Create the Humor, which fails.
        HumorDTO humorDTO = humorMapper.toDto(humor);

        restHumorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humorDTO)))
            .andExpect(status().isBadRequest());

        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHumors() throws Exception {
        // Initialize the database
        humorRepository.saveAndFlush(humor);

        // Get all the humorList
        restHumorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(humor.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].emoticoContentType").value(hasItem(DEFAULT_EMOTICO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].emotico").value(hasItem(Base64Utils.encodeToString(DEFAULT_EMOTICO))));
    }

    @Test
    @Transactional
    void getHumor() throws Exception {
        // Initialize the database
        humorRepository.saveAndFlush(humor);

        // Get the humor
        restHumorMockMvc
            .perform(get(ENTITY_API_URL_ID, humor.getId()).with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(humor.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.emoticoContentType").value(DEFAULT_EMOTICO_CONTENT_TYPE))
            .andExpect(jsonPath("$.emotico").value(Base64Utils.encodeToString(DEFAULT_EMOTICO)));
    }

    @Test
    @Transactional
    void getNonExistingHumor() throws Exception {
        // Get the humor
        restHumorMockMvc
            .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE).with(user("admin").roles("ADMIN")))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHumor() throws Exception {
        // Initialize the database
        humorRepository.saveAndFlush(humor);

        int databaseSizeBeforeUpdate = humorRepository.findAll().size();

        // Update the humor
        Humor updatedHumor = humorRepository.findById(humor.getId()).get();
        // Disconnect from session so that the updates on updatedHumor are not directly saved in db
        em.detach(updatedHumor);
        updatedHumor
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION)
            .emotico(UPDATED_EMOTICO)
            .emoticoContentType(UPDATED_EMOTICO_CONTENT_TYPE);
        HumorDTO humorDTO = humorMapper.toDto(updatedHumor);

        restHumorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, humorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humorDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isOk());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeUpdate);
        Humor testHumor = humorList.get(humorList.size() - 1);
        assertThat(testHumor.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testHumor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testHumor.getEmotico()).isEqualTo(UPDATED_EMOTICO);
        assertThat(testHumor.getEmoticoContentType()).isEqualTo(UPDATED_EMOTICO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingHumor() throws Exception {
        int databaseSizeBeforeUpdate = humorRepository.findAll().size();
        humor.setId(count.incrementAndGet());

        // Create the Humor
        HumorDTO humorDTO = humorMapper.toDto(humor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHumorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, humorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humorDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isBadRequest());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHumor() throws Exception {
        int databaseSizeBeforeUpdate = humorRepository.findAll().size();
        humor.setId(count.incrementAndGet());

        // Create the Humor
        HumorDTO humorDTO = humorMapper.toDto(humor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humorDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isBadRequest());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHumor() throws Exception {
        int databaseSizeBeforeUpdate = humorRepository.findAll().size();
        humor.setId(count.incrementAndGet());

        // Create the Humor
        HumorDTO humorDTO = humorMapper.toDto(humor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumorMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humorDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHumorWithPatch() throws Exception {
        // Initialize the database
        humorRepository.saveAndFlush(humor);

        int databaseSizeBeforeUpdate = humorRepository.findAll().size();

        // Update the humor using partial update
        Humor partialUpdatedHumor = new Humor();
        partialUpdatedHumor.setId(humor.getId());

        partialUpdatedHumor
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION)
            .emotico(UPDATED_EMOTICO)
            .emoticoContentType(UPDATED_EMOTICO_CONTENT_TYPE);

        restHumorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHumor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHumor))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isOk());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeUpdate);
        Humor testHumor = humorList.get(humorList.size() - 1);
        assertThat(testHumor.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testHumor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testHumor.getEmotico()).isEqualTo(UPDATED_EMOTICO);
        assertThat(testHumor.getEmoticoContentType()).isEqualTo(UPDATED_EMOTICO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateHumorWithPatch() throws Exception {
        // Initialize the database
        humorRepository.saveAndFlush(humor);

        int databaseSizeBeforeUpdate = humorRepository.findAll().size();

        // Update the humor using partial update
        Humor partialUpdatedHumor = new Humor();
        partialUpdatedHumor.setId(humor.getId());

        partialUpdatedHumor
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION)
            .emotico(UPDATED_EMOTICO)
            .emoticoContentType(UPDATED_EMOTICO_CONTENT_TYPE);

        restHumorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHumor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHumor))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isOk());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeUpdate);
        Humor testHumor = humorList.get(humorList.size() - 1);
        assertThat(testHumor.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testHumor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testHumor.getEmotico()).isEqualTo(UPDATED_EMOTICO);
        assertThat(testHumor.getEmoticoContentType()).isEqualTo(UPDATED_EMOTICO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingHumor() throws Exception {
        int databaseSizeBeforeUpdate = humorRepository.findAll().size();
        humor.setId(count.incrementAndGet());

        // Create the Humor
        HumorDTO humorDTO = humorMapper.toDto(humor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHumorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, humorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humorDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isBadRequest());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHumor() throws Exception {
        int databaseSizeBeforeUpdate = humorRepository.findAll().size();
        humor.setId(count.incrementAndGet());

        // Create the Humor
        HumorDTO humorDTO = humorMapper.toDto(humor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humorDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isBadRequest());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHumor() throws Exception {
        int databaseSizeBeforeUpdate = humorRepository.findAll().size();
        humor.setId(count.incrementAndGet());

        // Create the Humor
        HumorDTO humorDTO = humorMapper.toDto(humor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumorMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humorDTO))
                    .with(user("admin").roles("ADMIN"))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Humor in the database
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHumor() throws Exception {
        // Initialize the database
        humorRepository.saveAndFlush(humor);

        int databaseSizeBeforeDelete = humorRepository.findAll().size();

        // Delete the humor
        restHumorMockMvc
            .perform(delete(ENTITY_API_URL_ID, humor.getId()).accept(MediaType.APPLICATION_JSON).with(user("admin").roles("ADMIN")))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Humor> humorList = humorRepository.findAll();
        assertThat(humorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
