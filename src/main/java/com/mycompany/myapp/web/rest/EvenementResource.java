package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Evenement;
import com.mycompany.myapp.repository.EvenementRepository;
import com.mycompany.myapp.service.EvenementService;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Evenement}.
 */
@RestController
@RequestMapping("/api")
public class EvenementResource {

    private final Logger log = LoggerFactory.getLogger(EvenementResource.class);

    private static final String ENTITY_NAME = "evenement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EvenementService evenementService;

    private final EvenementRepository evenementRepository;

    public EvenementResource(EvenementService evenementService, EvenementRepository evenementRepository) {
        this.evenementService = evenementService;
        this.evenementRepository = evenementRepository;
    }

    /**
     * {@code POST  /evenements} : Create a new evenement.
     *
     * @param evenement the evenement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new evenement, or with status {@code 400 (Bad Request)} if the evenement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/evenements")
    public Mono<ResponseEntity<Evenement>> createEvenement(@Valid @RequestBody Evenement evenement) throws URISyntaxException {
        log.debug("REST request to save Evenement : {}", evenement);
        if (evenement.getId() != null) {
            throw new BadRequestAlertException("A new evenement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return evenementService
            .save(evenement)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/evenements/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /evenements/:id} : Updates an existing evenement.
     *
     * @param id the id of the evenement to save.
     * @param evenement the evenement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evenement,
     * or with status {@code 400 (Bad Request)} if the evenement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the evenement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/evenements/{id}")
    public Mono<ResponseEntity<Evenement>> updateEvenement(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Evenement evenement
    ) throws URISyntaxException {
        log.debug("REST request to update Evenement : {}, {}", id, evenement);
        if (evenement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evenement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return evenementRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return evenementService
                    .save(evenement)
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
     * {@code PATCH  /evenements/:id} : Partial updates given fields of an existing evenement, field will ignore if it is null
     *
     * @param id the id of the evenement to save.
     * @param evenement the evenement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evenement,
     * or with status {@code 400 (Bad Request)} if the evenement is not valid,
     * or with status {@code 404 (Not Found)} if the evenement is not found,
     * or with status {@code 500 (Internal Server Error)} if the evenement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/evenements/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Evenement>> partialUpdateEvenement(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Evenement evenement
    ) throws URISyntaxException {
        log.debug("REST request to partial update Evenement partially : {}, {}", id, evenement);
        if (evenement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evenement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return evenementRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Evenement> result = evenementService.partialUpdate(evenement);

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
     * {@code GET  /evenements} : get all the evenements.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of evenements in body.
     */
    @GetMapping("/evenements")
    public Mono<List<Evenement>> getAllEvenements() {
        log.debug("REST request to get all Evenements");
        return evenementService.findAll().collectList();
    }

    /**
     * {@code GET  /evenements} : get all the evenements as a stream.
     * @return the {@link Flux} of evenements.
     */
    @GetMapping(value = "/evenements", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Evenement> getAllEvenementsAsStream() {
        log.debug("REST request to get all Evenements as a stream");
        return evenementService.findAll();
    }

    /**
     * {@code GET  /evenements/:id} : get the "id" evenement.
     *
     * @param id the id of the evenement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the evenement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/evenements/{id}")
    public Mono<ResponseEntity<Evenement>> getEvenement(@PathVariable String id) {
        log.debug("REST request to get Evenement : {}", id);
        Mono<Evenement> evenement = evenementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(evenement);
    }

    /**
     * {@code DELETE  /evenements/:id} : delete the "id" evenement.
     *
     * @param id the id of the evenement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/evenements/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteEvenement(@PathVariable String id) {
        log.debug("REST request to delete Evenement : {}", id);
        return evenementService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
