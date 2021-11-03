package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Utilisateur;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Utilisateur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UtilisateurRepository extends ReactiveMongoRepository<Utilisateur, String> {}
