package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Utilisateur;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Utilisateur}.
 */
public interface UtilisateurService {
    /**
     * Save a utilisateur.
     *
     * @param utilisateur the entity to save.
     * @return the persisted entity.
     */
    Mono<Utilisateur> save(Utilisateur utilisateur);

    /**
     * Partially updates a utilisateur.
     *
     * @param utilisateur the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Utilisateur> partialUpdate(Utilisateur utilisateur);

    /**
     * Get all the utilisateurs.
     *
     * @return the list of entities.
     */
    Flux<Utilisateur> findAll();

    /**
     * Returns the number of utilisateurs available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" utilisateur.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Utilisateur> findOne(String id);

    /**
     * Delete the "id" utilisateur.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
