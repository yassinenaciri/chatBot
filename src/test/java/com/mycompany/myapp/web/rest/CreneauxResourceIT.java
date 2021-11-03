package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Creneaux;
import com.mycompany.myapp.repository.CreneauxRepository;
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
 * Integration tests for the {@link CreneauxResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CreneauxResourceIT {

    private static final Instant DEFAULT_DATE_DEBUT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_DEBUT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_FIN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_FIN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/creneaux";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CreneauxRepository creneauxRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Creneaux creneaux;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Creneaux createEntity() {
        Creneaux creneaux = new Creneaux().dateDebut(DEFAULT_DATE_DEBUT).dateFin(DEFAULT_DATE_FIN);
        return creneaux;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Creneaux createUpdatedEntity() {
        Creneaux creneaux = new Creneaux().dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN);
        return creneaux;
    }

    @BeforeEach
    public void initTest() {
        creneauxRepository.deleteAll().block();
        creneaux = createEntity();
    }

    @Test
    void createCreneaux() throws Exception {
        int databaseSizeBeforeCreate = creneauxRepository.findAll().collectList().block().size();
        // Create the Creneaux
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(creneaux))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeCreate + 1);
        Creneaux testCreneaux = creneauxList.get(creneauxList.size() - 1);
        assertThat(testCreneaux.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testCreneaux.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
    }

    @Test
    void createCreneauxWithExistingId() throws Exception {
        // Create the Creneaux with an existing ID
        creneaux.setId("existing_id");

        int databaseSizeBeforeCreate = creneauxRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(creneaux))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDateDebutIsRequired() throws Exception {
        int databaseSizeBeforeTest = creneauxRepository.findAll().collectList().block().size();
        // set the field null
        creneaux.setDateDebut(null);

        // Create the Creneaux, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(creneaux))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCreneauxAsStream() {
        // Initialize the database
        creneauxRepository.save(creneaux).block();

        List<Creneaux> creneauxList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Creneaux.class)
            .getResponseBody()
            .filter(creneaux::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(creneauxList).isNotNull();
        assertThat(creneauxList).hasSize(1);
        Creneaux testCreneaux = creneauxList.get(0);
        assertThat(testCreneaux.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testCreneaux.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
    }

    @Test
    void getAllCreneaux() {
        // Initialize the database
        creneauxRepository.save(creneaux).block();

        // Get all the creneauxList
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
            .value(hasItem(creneaux.getId()))
            .jsonPath("$.[*].dateDebut")
            .value(hasItem(DEFAULT_DATE_DEBUT.toString()))
            .jsonPath("$.[*].dateFin")
            .value(hasItem(DEFAULT_DATE_FIN.toString()));
    }

    @Test
    void getCreneaux() {
        // Initialize the database
        creneauxRepository.save(creneaux).block();

        // Get the creneaux
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, creneaux.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(creneaux.getId()))
            .jsonPath("$.dateDebut")
            .value(is(DEFAULT_DATE_DEBUT.toString()))
            .jsonPath("$.dateFin")
            .value(is(DEFAULT_DATE_FIN.toString()));
    }

    @Test
    void getNonExistingCreneaux() {
        // Get the creneaux
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCreneaux() throws Exception {
        // Initialize the database
        creneauxRepository.save(creneaux).block();

        int databaseSizeBeforeUpdate = creneauxRepository.findAll().collectList().block().size();

        // Update the creneaux
        Creneaux updatedCreneaux = creneauxRepository.findById(creneaux.getId()).block();
        updatedCreneaux.dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCreneaux.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCreneaux))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeUpdate);
        Creneaux testCreneaux = creneauxList.get(creneauxList.size() - 1);
        assertThat(testCreneaux.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testCreneaux.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
    }

    @Test
    void putNonExistingCreneaux() throws Exception {
        int databaseSizeBeforeUpdate = creneauxRepository.findAll().collectList().block().size();
        creneaux.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, creneaux.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(creneaux))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCreneaux() throws Exception {
        int databaseSizeBeforeUpdate = creneauxRepository.findAll().collectList().block().size();
        creneaux.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(creneaux))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCreneaux() throws Exception {
        int databaseSizeBeforeUpdate = creneauxRepository.findAll().collectList().block().size();
        creneaux.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(creneaux))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCreneauxWithPatch() throws Exception {
        // Initialize the database
        creneauxRepository.save(creneaux).block();

        int databaseSizeBeforeUpdate = creneauxRepository.findAll().collectList().block().size();

        // Update the creneaux using partial update
        Creneaux partialUpdatedCreneaux = new Creneaux();
        partialUpdatedCreneaux.setId(creneaux.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCreneaux.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCreneaux))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeUpdate);
        Creneaux testCreneaux = creneauxList.get(creneauxList.size() - 1);
        assertThat(testCreneaux.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testCreneaux.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
    }

    @Test
    void fullUpdateCreneauxWithPatch() throws Exception {
        // Initialize the database
        creneauxRepository.save(creneaux).block();

        int databaseSizeBeforeUpdate = creneauxRepository.findAll().collectList().block().size();

        // Update the creneaux using partial update
        Creneaux partialUpdatedCreneaux = new Creneaux();
        partialUpdatedCreneaux.setId(creneaux.getId());

        partialUpdatedCreneaux.dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCreneaux.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCreneaux))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeUpdate);
        Creneaux testCreneaux = creneauxList.get(creneauxList.size() - 1);
        assertThat(testCreneaux.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testCreneaux.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
    }

    @Test
    void patchNonExistingCreneaux() throws Exception {
        int databaseSizeBeforeUpdate = creneauxRepository.findAll().collectList().block().size();
        creneaux.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, creneaux.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(creneaux))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCreneaux() throws Exception {
        int databaseSizeBeforeUpdate = creneauxRepository.findAll().collectList().block().size();
        creneaux.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(creneaux))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCreneaux() throws Exception {
        int databaseSizeBeforeUpdate = creneauxRepository.findAll().collectList().block().size();
        creneaux.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(creneaux))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Creneaux in the database
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCreneaux() {
        // Initialize the database
        creneauxRepository.save(creneaux).block();

        int databaseSizeBeforeDelete = creneauxRepository.findAll().collectList().block().size();

        // Delete the creneaux
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, creneaux.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Creneaux> creneauxList = creneauxRepository.findAll().collectList().block();
        assertThat(creneauxList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
