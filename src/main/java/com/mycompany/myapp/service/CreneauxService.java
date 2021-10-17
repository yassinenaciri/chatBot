package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Creneaux;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Creneaux}.
 */
public interface CreneauxService {
    /**
     * Save a creneaux.
     *
     * @param creneaux the entity to save.
     * @return the persisted entity.
     */
    Mono<Creneaux> save(Creneaux creneaux);

    /**
     * Partially updates a creneaux.
     *
     * @param creneaux the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Creneaux> partialUpdate(Creneaux creneaux);

    /**
     * Get all the creneaux.
     *
     * @return the list of entities.
     */
    Flux<Creneaux> findAll();

    /**
     * Returns the number of creneaux available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" creneaux.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Creneaux> findOne(String id);

    /**
     * Delete the "id" creneaux.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
