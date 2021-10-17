package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Creneaux;
import com.mycompany.myapp.repository.CreneauxRepository;
import com.mycompany.myapp.service.CreneauxService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Creneaux}.
 */
@Service
public class CreneauxServiceImpl implements CreneauxService {

    private final Logger log = LoggerFactory.getLogger(CreneauxServiceImpl.class);

    private final CreneauxRepository creneauxRepository;

    public CreneauxServiceImpl(CreneauxRepository creneauxRepository) {
        this.creneauxRepository = creneauxRepository;
    }

    @Override
    public Mono<Creneaux> save(Creneaux creneaux) {
        log.debug("Request to save Creneaux : {}", creneaux);
        return creneauxRepository.save(creneaux);
    }

    @Override
    public Mono<Creneaux> partialUpdate(Creneaux creneaux) {
        log.debug("Request to partially update Creneaux : {}", creneaux);

        return creneauxRepository
            .findById(creneaux.getId())
            .map(existingCreneaux -> {
                if (creneaux.getDateDebut() != null) {
                    existingCreneaux.setDateDebut(creneaux.getDateDebut());
                }
                if (creneaux.getDateFin() != null) {
                    existingCreneaux.setDateFin(creneaux.getDateFin());
                }

                return existingCreneaux;
            })
            .flatMap(creneauxRepository::save);
    }

    @Override
    public Flux<Creneaux> findAll() {
        log.debug("Request to get all Creneaux");
        return creneauxRepository.findAll();
    }

    public Mono<Long> countAll() {
        return creneauxRepository.count();
    }

    @Override
    public Mono<Creneaux> findOne(String id) {
        log.debug("Request to get Creneaux : {}", id);
        return creneauxRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Creneaux : {}", id);
        return creneauxRepository.deleteById(id);
    }
}
