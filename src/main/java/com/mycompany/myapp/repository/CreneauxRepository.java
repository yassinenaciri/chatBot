package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Creneaux;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Creneaux entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CreneauxRepository extends ReactiveMongoRepository<Creneaux, String> {}
