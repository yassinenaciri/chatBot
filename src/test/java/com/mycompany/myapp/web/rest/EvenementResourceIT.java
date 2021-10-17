package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Evenement;
import com.mycompany.myapp.repository.EvenementRepository;
import java.time.Duration;
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
 * Integration tests for the {@link EvenementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class EvenementResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LOCALISATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCALISATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/evenements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Evenement evenement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evenement createEntity() {
        Evenement evenement = new Evenement().titre(DEFAULT_TITRE).description(DEFAULT_DESCRIPTION).localisation(DEFAULT_LOCALISATION);
        return evenement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evenement createUpdatedEntity() {
        Evenement evenement = new Evenement().titre(UPDATED_TITRE).description(UPDATED_DESCRIPTION).localisation(UPDATED_LOCALISATION);
        return evenement;
    }

    @BeforeEach
    public void initTest() {
        evenementRepository.deleteAll().block();
        evenement = createEntity();
    }

    @Test
    void createEvenement() throws Exception {
        int databaseSizeBeforeCreate = evenementRepository.findAll().collectList().block().size();
        // Create the Evenement
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evenement))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeCreate + 1);
        Evenement testEvenement = evenementList.get(evenementList.size() - 1);
        assertThat(testEvenement.getTitre()).isEqualTo(DEFAULT_TITRE);
        assertThat(testEvenement.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEvenement.getLocalisation()).isEqualTo(DEFAULT_LOCALISATION);
    }

    @Test
    void createEvenementWithExistingId() throws Exception {
        // Create the Evenement with an existing ID
        evenement.setId("existing_id");

        int databaseSizeBeforeCreate = evenementRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evenement))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitreIsRequired() throws Exception {
        int databaseSizeBeforeTest = evenementRepository.findAll().collectList().block().size();
        // set the field null
        evenement.setTitre(null);

        // Create the Evenement, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evenement))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllEvenementsAsStream() {
        // Initialize the database
        evenementRepository.save(evenement).block();

        List<Evenement> evenementList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Evenement.class)
            .getResponseBody()
            .filter(evenement::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(evenementList).isNotNull();
        assertThat(evenementList).hasSize(1);
        Evenement testEvenement = evenementList.get(0);
        assertThat(testEvenement.getTitre()).isEqualTo(DEFAULT_TITRE);
        assertThat(testEvenement.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEvenement.getLocalisation()).isEqualTo(DEFAULT_LOCALISATION);
    }

    @Test
    void getAllEvenements() {
        // Initialize the database
        evenementRepository.save(evenement).block();

        // Get all the evenementList
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
            .value(hasItem(evenement.getId()))
            .jsonPath("$.[*].titre")
            .value(hasItem(DEFAULT_TITRE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].localisation")
            .value(hasItem(DEFAULT_LOCALISATION));
    }

    @Test
    void getEvenement() {
        // Initialize the database
        evenementRepository.save(evenement).block();

        // Get the evenement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, evenement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(evenement.getId()))
            .jsonPath("$.titre")
            .value(is(DEFAULT_TITRE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.localisation")
            .value(is(DEFAULT_LOCALISATION));
    }

    @Test
    void getNonExistingEvenement() {
        // Get the evenement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEvenement() throws Exception {
        // Initialize the database
        evenementRepository.save(evenement).block();

        int databaseSizeBeforeUpdate = evenementRepository.findAll().collectList().block().size();

        // Update the evenement
        Evenement updatedEvenement = evenementRepository.findById(evenement.getId()).block();
        updatedEvenement.titre(UPDATED_TITRE).description(UPDATED_DESCRIPTION).localisation(UPDATED_LOCALISATION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEvenement.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEvenement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeUpdate);
        Evenement testEvenement = evenementList.get(evenementList.size() - 1);
        assertThat(testEvenement.getTitre()).isEqualTo(UPDATED_TITRE);
        assertThat(testEvenement.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvenement.getLocalisation()).isEqualTo(UPDATED_LOCALISATION);
    }

    @Test
    void putNonExistingEvenement() throws Exception {
        int databaseSizeBeforeUpdate = evenementRepository.findAll().collectList().block().size();
        evenement.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, evenement.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evenement))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEvenement() throws Exception {
        int databaseSizeBeforeUpdate = evenementRepository.findAll().collectList().block().size();
        evenement.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evenement))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEvenement() throws Exception {
        int databaseSizeBeforeUpdate = evenementRepository.findAll().collectList().block().size();
        evenement.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evenement))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEvenementWithPatch() throws Exception {
        // Initialize the database
        evenementRepository.save(evenement).block();

        int databaseSizeBeforeUpdate = evenementRepository.findAll().collectList().block().size();

        // Update the evenement using partial update
        Evenement partialUpdatedEvenement = new Evenement();
        partialUpdatedEvenement.setId(evenement.getId());

        partialUpdatedEvenement.description(UPDATED_DESCRIPTION).localisation(UPDATED_LOCALISATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvenement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvenement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeUpdate);
        Evenement testEvenement = evenementList.get(evenementList.size() - 1);
        assertThat(testEvenement.getTitre()).isEqualTo(DEFAULT_TITRE);
        assertThat(testEvenement.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvenement.getLocalisation()).isEqualTo(UPDATED_LOCALISATION);
    }

    @Test
    void fullUpdateEvenementWithPatch() throws Exception {
        // Initialize the database
        evenementRepository.save(evenement).block();

        int databaseSizeBeforeUpdate = evenementRepository.findAll().collectList().block().size();

        // Update the evenement using partial update
        Evenement partialUpdatedEvenement = new Evenement();
        partialUpdatedEvenement.setId(evenement.getId());

        partialUpdatedEvenement.titre(UPDATED_TITRE).description(UPDATED_DESCRIPTION).localisation(UPDATED_LOCALISATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvenement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvenement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeUpdate);
        Evenement testEvenement = evenementList.get(evenementList.size() - 1);
        assertThat(testEvenement.getTitre()).isEqualTo(UPDATED_TITRE);
        assertThat(testEvenement.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvenement.getLocalisation()).isEqualTo(UPDATED_LOCALISATION);
    }

    @Test
    void patchNonExistingEvenement() throws Exception {
        int databaseSizeBeforeUpdate = evenementRepository.findAll().collectList().block().size();
        evenement.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, evenement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(evenement))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEvenement() throws Exception {
        int databaseSizeBeforeUpdate = evenementRepository.findAll().collectList().block().size();
        evenement.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(evenement))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEvenement() throws Exception {
        int databaseSizeBeforeUpdate = evenementRepository.findAll().collectList().block().size();
        evenement.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(evenement))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Evenement in the database
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEvenement() {
        // Initialize the database
        evenementRepository.save(evenement).block();

        int databaseSizeBeforeDelete = evenementRepository.findAll().collectList().block().size();

        // Delete the evenement
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, evenement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Evenement> evenementList = evenementRepository.findAll().collectList().block();
        assertThat(evenementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
