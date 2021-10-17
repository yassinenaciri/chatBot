package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Utilisateur;
import com.mycompany.myapp.repository.UtilisateurRepository;
import com.mycompany.myapp.service.UtilisateurService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Utilisateur}.
 */
@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final Logger log = LoggerFactory.getLogger(UtilisateurServiceImpl.class);

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public Mono<Utilisateur> save(Utilisateur utilisateur) {
        log.debug("Request to save Utilisateur : {}", utilisateur);
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Mono<Utilisateur> partialUpdate(Utilisateur utilisateur) {
        log.debug("Request to partially update Utilisateur : {}", utilisateur);

        return utilisateurRepository
            .findById(utilisateur.getId())
            .map(existingUtilisateur -> {
                if (utilisateur.getDateDebut() != null) {
                    existingUtilisateur.setDateDebut(utilisateur.getDateDebut());
                }
                if (utilisateur.getDateFin() != null) {
                    existingUtilisateur.setDateFin(utilisateur.getDateFin());
                }

                return existingUtilisateur;
            })
            .flatMap(utilisateurRepository::save);
    }

    @Override
    public Flux<Utilisateur> findAll() {
        log.debug("Request to get all Utilisateurs");
        return utilisateurRepository.findAll();
    }

    public Mono<Long> countAll() {
        return utilisateurRepository.count();
    }

    @Override
    public Mono<Utilisateur> findOne(String id) {
        log.debug("Request to get Utilisateur : {}", id);
        return utilisateurRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Utilisateur : {}", id);
        return utilisateurRepository.deleteById(id);
    }
}
