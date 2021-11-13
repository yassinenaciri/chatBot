package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.TacheService;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Tache}.
 */
@Service
public class TacheServiceImpl implements TacheService {

    private final Logger log = LoggerFactory.getLogger(TacheServiceImpl.class);

    private final TacheRepository tacheRepository;
    private final UserRepository userRepository;

    public TacheServiceImpl(TacheRepository tacheRepository, UserRepository userRepository) {
        this.tacheRepository = tacheRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Tache> save(Tache tache) {
        log.debug("Request to save Tache : {}", tache);
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(
                login -> {
                    return userRepository
                        .findOneByLogin(login)
                        .flatMap(
                            user -> {
                                tache.setUser(user);
                                return tacheRepository.save(tache);
                            }
                        );
                }
            );
    }

    @Override
    public Mono<Tache> partialUpdate(Tache tache) {
        log.debug("Request to partially update Tache : {}", tache);

        return tacheRepository
            .findById(tache.getId())
            .map(
                existingTache -> {
                    if (tache.getIntitule() != null) {
                        existingTache.setIntitule(tache.getIntitule());
                    }
                    if (tache.getDescription() != null) {
                        existingTache.setDescription(tache.getDescription());
                    }
                    if (tache.getDateDebut() != null) {
                        existingTache.setDateDebut(tache.getDateDebut());
                    }
                    if (tache.getDateFin() != null) {
                        existingTache.setDateFin(tache.getDateFin());
                    }

                    return existingTache;
                }
            )
            .flatMap(tacheRepository::save);
    }

    @Override
    public Flux<Tache> findAll() {
        log.debug("Request to get all Taches");
        return tacheRepository.findAll();
    }

    public Mono<Long> countAll() {
        return tacheRepository.count();
    }

    @Override
    public Mono<Tache> findOne(String id) {
        log.debug("Request to get Tache : {}", id);
        return tacheRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Tache : {}", id);
        return tacheRepository.deleteById(id);
    }
}
