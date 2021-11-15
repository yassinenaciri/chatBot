package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.User;
import java.util.ArrayList;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Tache entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TacheRepository extends ReactiveMongoRepository<Tache, String> {
    Flux<Tache> findAllByUser(User user);
    //Flux<Tache> findAllByIntitule(String intitule);
}
