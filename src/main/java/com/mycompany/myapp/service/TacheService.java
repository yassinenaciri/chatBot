package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Tache;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Tache}.
 */
public interface TacheService {
    /**
     * Save a tache.
     *
     * @param tache the entity to save.
     * @return the persisted entity.
     */
    Mono<Tache> save(Tache tache);

    /**
     * Partially updates a tache.
     *
     * @param tache the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Tache> partialUpdate(Tache tache);

    /**
     * Get all the taches.
     *
     * @return the list of entities.
     */
    Flux<Tache> findAll();

    /**
     * Returns the number of taches available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" tache.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Tache> findOne(String id);

    /**
     * Delete the "id" tache.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
