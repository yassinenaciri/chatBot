package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.repository.TacheRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link TacheResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class TacheResourceIT {

    private static final String DEFAULT_INTITULE = "AAAAAAAAAA";
    private static final String UPDATED_INTITULE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_DEBUT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_DEBUT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_FIN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_FIN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/taches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Tache tache;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createEntity() {
        Tache tache = new Tache()
            .intitule(DEFAULT_INTITULE)
            .description(DEFAULT_DESCRIPTION)
            .dateDebut(DEFAULT_DATE_DEBUT)
            .dateFin(DEFAULT_DATE_FIN);
        return tache;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createUpdatedEntity() {
        Tache tache = new Tache()
            .intitule(UPDATED_INTITULE)
            .description(UPDATED_DESCRIPTION)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN);
        return tache;
    }

    @BeforeEach
    public void initTest() {
        tacheRepository.deleteAll().block();
        tache = createEntity();
    }

    @Test
    void createTache() throws Exception {
        int databaseSizeBeforeCreate = tacheRepository.findAll().collectList().block().size();
        // Create the Tache
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate + 1);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getIntitule()).isEqualTo(DEFAULT_INTITULE);
        assertThat(testTache.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTache.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testTache.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
    }

    @Test
    void createTacheWithExistingId() throws Exception {
        // Create the Tache with an existing ID
        tache.setId("existing_id");

        int databaseSizeBeforeCreate = tacheRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkIntituleIsRequired() throws Exception {
        int databaseSizeBeforeTest = tacheRepository.findAll().collectList().block().size();
        // set the field null
        tache.setIntitule(null);

        // Create the Tache, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = tacheRepository.findAll().collectList().block().size();
        // set the field null
        tache.setDescription(null);

        // Create the Tache, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllTachesAsStream() {
        // Initialize the database
        tacheRepository.save(tache).block();

        List<Tache> tacheList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Tache.class)
            .getResponseBody()
            .filter(tache::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tacheList).isNotNull();
        assertThat(tacheList).hasSize(1);
        Tache testTache = tacheList.get(0);
        assertThat(testTache.getIntitule()).isEqualTo(DEFAULT_INTITULE);
        assertThat(testTache.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTache.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testTache.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
    }

    @Test
    void getAllTaches() {
        // Initialize the database
        tacheRepository.save(tache).block();

        // Get all the tacheList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(tache.getId()))
            .jsonPath("$.[*].intitule")
            .value(hasItem(DEFAULT_INTITULE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].dateDebut")
            .value(hasItem(DEFAULT_DATE_DEBUT.toString()))
            .jsonPath("$.[*].dateFin")
            .value(hasItem(DEFAULT_DATE_FIN.toString()));
    }

    @Test
    void getTache() {
        // Initialize the database
        tacheRepository.save(tache).block();

        // Get the tache
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tache.getId()))
            .jsonPath("$.intitule")
            .value(is(DEFAULT_INTITULE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.dateDebut")
            .value(is(DEFAULT_DATE_DEBUT.toString()))
            .jsonPath("$.dateFin")
            .value(is(DEFAULT_DATE_FIN.toString()));
    }

    @Test
    void getNonExistingTache() {
        // Get the tache
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTache() throws Exception {
        // Initialize the database
        tacheRepository.save(tache).block();

        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();

        // Update the tache
        Tache updatedTache = tacheRepository.findById(tache.getId()).block();
        updatedTache.intitule(UPDATED_INTITULE).description(UPDATED_DESCRIPTION).dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTache.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTache))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getIntitule()).isEqualTo(UPDATED_INTITULE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testTache.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
    }

    @Test
    void putNonExistingTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.save(tache).block();

        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache.intitule(UPDATED_INTITULE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTache.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTache))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getIntitule()).isEqualTo(UPDATED_INTITULE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testTache.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
    }

    @Test
    void fullUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.save(tache).block();

        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache
            .intitule(UPDATED_INTITULE)
            .description(UPDATED_DESCRIPTION)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTache.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTache))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getIntitule()).isEqualTo(UPDATED_INTITULE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testTache.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
    }

    @Test
    void patchNonExistingTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tache))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTache() {
        // Initialize the database
        tacheRepository.save(tache).block();

        int databaseSizeBeforeDelete = tacheRepository.findAll().collectList().block().size();

        // Delete the tache
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
