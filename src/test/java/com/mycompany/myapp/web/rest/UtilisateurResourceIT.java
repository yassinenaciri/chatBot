package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Utilisateur;
import com.mycompany.myapp.repository.UtilisateurRepository;
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
 * Integration tests for the {@link UtilisateurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class UtilisateurResourceIT {

    private static final String DEFAULT_NOM_COMPLET = "AAAAAAAAAA";
    private static final String UPDATED_NOM_COMPLET = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/utilisateurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Utilisateur utilisateur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Utilisateur createEntity() {
        Utilisateur utilisateur = new Utilisateur().nomComplet(DEFAULT_NOM_COMPLET);
        return utilisateur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Utilisateur createUpdatedEntity() {
        Utilisateur utilisateur = new Utilisateur().nomComplet(UPDATED_NOM_COMPLET);
        return utilisateur;
    }

    @BeforeEach
    public void initTest() {
        utilisateurRepository.deleteAll().block();
        utilisateur = createEntity();
    }

    @Test
    void createUtilisateur() throws Exception {
        int databaseSizeBeforeCreate = utilisateurRepository.findAll().collectList().block().size();
        // Create the Utilisateur
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(utilisateur))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeCreate + 1);
        Utilisateur testUtilisateur = utilisateurList.get(utilisateurList.size() - 1);
        assertThat(testUtilisateur.getNomComplet()).isEqualTo(DEFAULT_NOM_COMPLET);
    }

    @Test
    void createUtilisateurWithExistingId() throws Exception {
        // Create the Utilisateur with an existing ID
        utilisateur.setId("existing_id");

        int databaseSizeBeforeCreate = utilisateurRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(utilisateur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomCompletIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilisateurRepository.findAll().collectList().block().size();
        // set the field null
        utilisateur.setNomComplet(null);

        // Create the Utilisateur, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(utilisateur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllUtilisateursAsStream() {
        // Initialize the database
        utilisateurRepository.save(utilisateur).block();

        List<Utilisateur> utilisateurList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Utilisateur.class)
            .getResponseBody()
            .filter(utilisateur::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(utilisateurList).isNotNull();
        assertThat(utilisateurList).hasSize(1);
        Utilisateur testUtilisateur = utilisateurList.get(0);
        assertThat(testUtilisateur.getNomComplet()).isEqualTo(DEFAULT_NOM_COMPLET);
    }

    @Test
    void getAllUtilisateurs() {
        // Initialize the database
        utilisateurRepository.save(utilisateur).block();

        // Get all the utilisateurList
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
            .value(hasItem(utilisateur.getId()))
            .jsonPath("$.[*].nomComplet")
            .value(hasItem(DEFAULT_NOM_COMPLET));
    }

    @Test
    void getUtilisateur() {
        // Initialize the database
        utilisateurRepository.save(utilisateur).block();

        // Get the utilisateur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, utilisateur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(utilisateur.getId()))
            .jsonPath("$.nomComplet")
            .value(is(DEFAULT_NOM_COMPLET));
    }

    @Test
    void getNonExistingUtilisateur() {
        // Get the utilisateur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUtilisateur() throws Exception {
        // Initialize the database
        utilisateurRepository.save(utilisateur).block();

        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().collectList().block().size();

        // Update the utilisateur
        Utilisateur updatedUtilisateur = utilisateurRepository.findById(utilisateur.getId()).block();
        updatedUtilisateur.nomComplet(UPDATED_NOM_COMPLET);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUtilisateur.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUtilisateur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
        Utilisateur testUtilisateur = utilisateurList.get(utilisateurList.size() - 1);
        assertThat(testUtilisateur.getNomComplet()).isEqualTo(UPDATED_NOM_COMPLET);
    }

    @Test
    void putNonExistingUtilisateur() throws Exception {
        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().collectList().block().size();
        utilisateur.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, utilisateur.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(utilisateur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUtilisateur() throws Exception {
        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().collectList().block().size();
        utilisateur.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(utilisateur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUtilisateur() throws Exception {
        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().collectList().block().size();
        utilisateur.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(utilisateur))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUtilisateurWithPatch() throws Exception {
        // Initialize the database
        utilisateurRepository.save(utilisateur).block();

        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().collectList().block().size();

        // Update the utilisateur using partial update
        Utilisateur partialUpdatedUtilisateur = new Utilisateur();
        partialUpdatedUtilisateur.setId(utilisateur.getId());

        partialUpdatedUtilisateur.nomComplet(UPDATED_NOM_COMPLET);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUtilisateur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilisateur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
        Utilisateur testUtilisateur = utilisateurList.get(utilisateurList.size() - 1);
        assertThat(testUtilisateur.getNomComplet()).isEqualTo(UPDATED_NOM_COMPLET);
    }

    @Test
    void fullUpdateUtilisateurWithPatch() throws Exception {
        // Initialize the database
        utilisateurRepository.save(utilisateur).block();

        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().collectList().block().size();

        // Update the utilisateur using partial update
        Utilisateur partialUpdatedUtilisateur = new Utilisateur();
        partialUpdatedUtilisateur.setId(utilisateur.getId());

        partialUpdatedUtilisateur.nomComplet(UPDATED_NOM_COMPLET);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUtilisateur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilisateur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
        Utilisateur testUtilisateur = utilisateurList.get(utilisateurList.size() - 1);
        assertThat(testUtilisateur.getNomComplet()).isEqualTo(UPDATED_NOM_COMPLET);
    }

    @Test
    void patchNonExistingUtilisateur() throws Exception {
        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().collectList().block().size();
        utilisateur.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, utilisateur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(utilisateur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUtilisateur() throws Exception {
        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().collectList().block().size();
        utilisateur.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(utilisateur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUtilisateur() throws Exception {
        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().collectList().block().size();
        utilisateur.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(utilisateur))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUtilisateur() {
        // Initialize the database
        utilisateurRepository.save(utilisateur).block();

        int databaseSizeBeforeDelete = utilisateurRepository.findAll().collectList().block().size();

        // Delete the utilisateur
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, utilisateur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll().collectList().block();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
