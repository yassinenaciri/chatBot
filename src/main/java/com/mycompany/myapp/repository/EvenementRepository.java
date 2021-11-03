package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Evenement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Evenement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EvenementRepository extends ReactiveMongoRepository<Evenement, String> {}
