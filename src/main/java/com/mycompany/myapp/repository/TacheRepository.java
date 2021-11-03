package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tache;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Tache entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TacheRepository extends ReactiveMongoRepository<Tache, String> {}
