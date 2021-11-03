package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Creneaux;
import com.mycompany.myapp.repository.CreneauxRepository;
import com.mycompany.myapp.service.CreneauxService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Creneaux}.
 */
@RestController
@RequestMapping("/api")
public class CreneauxResource {

    private final Logger log = LoggerFactory.getLogger(CreneauxResource.class);

    private static final String ENTITY_NAME = "creneaux";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CreneauxService creneauxService;

    private final CreneauxRepository creneauxRepository;

    public CreneauxResource(CreneauxService creneauxService, CreneauxRepository creneauxRepository) {
        this.creneauxService = creneauxService;
        this.creneauxRepository = creneauxRepository;
    }

    /**
     * {@code POST  /creneaux} : Create a new creneaux.
     *
     * @param creneaux the creneaux to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new creneaux, or with status {@code 400 (Bad Request)} if the creneaux has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/creneaux")
    public Mono<ResponseEntity<Creneaux>> createCreneaux(@Valid @RequestBody Creneaux creneaux) throws URISyntaxException {
        log.debug("REST request to save Creneaux : {}", creneaux);
        if (creneaux.getId() != null) {
            throw new BadRequestAlertException("A new creneaux cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return creneauxService
            .save(creneaux)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/creneaux/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /creneaux/:id} : Updates an existing creneaux.
     *
     * @param id the id of the creneaux to save.
     * @param creneaux the creneaux to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated creneaux,
     * or with status {@code 400 (Bad Request)} if the creneaux is not valid,
     * or with status {@code 500 (Internal Server Error)} if the creneaux couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/creneaux/{id}")
    public Mono<ResponseEntity<Creneaux>> updateCreneaux(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Creneaux creneaux
    ) throws URISyntaxException {
        log.debug("REST request to update Creneaux : {}, {}", id, creneaux);
        if (creneaux.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, creneaux.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return creneauxRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return creneauxService
                    .save(creneaux)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /creneaux/:id} : Partial updates given fields of an existing creneaux, field will ignore if it is null
     *
     * @param id the id of the creneaux to save.
     * @param creneaux the creneaux to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated creneaux,
     * or with status {@code 400 (Bad Request)} if the creneaux is not valid,
     * or with status {@code 404 (Not Found)} if the creneaux is not found,
     * or with status {@code 500 (Internal Server Error)} if the creneaux couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/creneaux/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Creneaux>> partialUpdateCreneaux(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Creneaux creneaux
    ) throws URISyntaxException {
        log.debug("REST request to partial update Creneaux partially : {}, {}", id, creneaux);
        if (creneaux.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, creneaux.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return creneauxRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Creneaux> result = creneauxService.partialUpdate(creneaux);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /creneaux} : get all the creneaux.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of creneaux in body.
     */
    @GetMapping("/creneaux")
    public Mono<List<Creneaux>> getAllCreneaux() {
        log.debug("REST request to get all Creneaux");
        return creneauxService.findAll().collectList();
    }

    /**
     * {@code GET  /creneaux} : get all the creneaux as a stream.
     * @return the {@link Flux} of creneaux.
     */
    @GetMapping(value = "/creneaux", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Creneaux> getAllCreneauxAsStream() {
        log.debug("REST request to get all Creneaux as a stream");
        return creneauxService.findAll();
    }

    /**
     * {@code GET  /creneaux/:id} : get the "id" creneaux.
     *
     * @param id the id of the creneaux to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the creneaux, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/creneaux/{id}")
    public Mono<ResponseEntity<Creneaux>> getCreneaux(@PathVariable String id) {
        log.debug("REST request to get Creneaux : {}", id);
        Mono<Creneaux> creneaux = creneauxService.findOne(id);
        return ResponseUtil.wrapOrNotFound(creneaux);
    }

    /**
     * {@code DELETE  /creneaux/:id} : delete the "id" creneaux.
     *
     * @param id the id of the creneaux to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/creneaux/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCreneaux(@PathVariable String id) {
        log.debug("REST request to delete Creneaux : {}", id);
        return creneauxService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
