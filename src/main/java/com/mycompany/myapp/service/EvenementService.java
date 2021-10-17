package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Evenement;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Evenement}.
 */
public interface EvenementService {
    /**
     * Save a evenement.
     *
     * @param evenement the entity to save.
     * @return the persisted entity.
     */
    Mono<Evenement> save(Evenement evenement);

    /**
     * Partially updates a evenement.
     *
     * @param evenement the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Evenement> partialUpdate(Evenement evenement);

    /**
     * Get all the evenements.
     *
     * @return the list of entities.
     */
    Flux<Evenement> findAll();

    /**
     * Returns the number of evenements available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" evenement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Evenement> findOne(String id);

    /**
     * Delete the "id" evenement.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
