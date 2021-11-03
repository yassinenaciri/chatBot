package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Evenement;
import com.mycompany.myapp.repository.EvenementRepository;
import com.mycompany.myapp.service.EvenementService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Evenement}.
 */
@Service
public class EvenementServiceImpl implements EvenementService {

    private final Logger log = LoggerFactory.getLogger(EvenementServiceImpl.class);

    private final EvenementRepository evenementRepository;

    public EvenementServiceImpl(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    @Override
    public Mono<Evenement> save(Evenement evenement) {
        log.debug("Request to save Evenement : {}", evenement);
        return evenementRepository.save(evenement);
    }

    @Override
    public Mono<Evenement> partialUpdate(Evenement evenement) {
        log.debug("Request to partially update Evenement : {}", evenement);

        return evenementRepository
            .findById(evenement.getId())
            .map(existingEvenement -> {
                if (evenement.getTitre() != null) {
                    existingEvenement.setTitre(evenement.getTitre());
                }
                if (evenement.getDescription() != null) {
                    existingEvenement.setDescription(evenement.getDescription());
                }
                if (evenement.getLocalisation() != null) {
                    existingEvenement.setLocalisation(evenement.getLocalisation());
                }

                return existingEvenement;
            })
            .flatMap(evenementRepository::save);
    }

    @Override
    public Flux<Evenement> findAll() {
        log.debug("Request to get all Evenements");
        return evenementRepository.findAll();
    }

    public Mono<Long> countAll() {
        return evenementRepository.count();
    }

    @Override
    public Mono<Evenement> findOne(String id) {
        log.debug("Request to get Evenement : {}", id);
        return evenementRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Evenement : {}", id);
        return evenementRepository.deleteById(id);
    }
}
