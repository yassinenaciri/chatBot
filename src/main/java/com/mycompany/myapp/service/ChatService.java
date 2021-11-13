package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChatService {

    private final UserRepository userRepository;
    private final TacheRepository tacheRepository;

    public ChatService(UserRepository userRepository, TacheRepository tacheRepository) {
        this.userRepository = userRepository;
        this.tacheRepository = tacheRepository;
    }

    public Mono<String> getAllTaches() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(
                login -> {
                    return userRepository
                        .findOneByLogin(login)
                        .flatMap(
                            user -> {
                                return tacheRepository
                                    .findAllByUser(user)
                                    .collectList()
                                    .flatMap(
                                        tach -> {
                                            String reponse = "The events that you have are: \n";
                                            for (Tache tache : tach) {
                                                reponse += '-';
                                                reponse += tache.getIntitule();
                                                reponse += '\n';
                                            }
                                            System.out.println(reponse);
                                            return Mono.just(reponse);
                                        }
                                    );
                            }
                        );
                }
            );
        /*taches=tacheRepository.findAll().collectList();
        return taches.flatMap(tach->{
            String reponse="The events that you have are: \n";
            for (Tache tache:tach
            ) {
                reponse+='-';
                reponse+=tache.getIntitule();
                reponse+='\n';
            }
            System.out.println(reponse);
            return Mono.just(reponse);
        });*/

    }

    public Mono<String> getAllTaches(Instant dateDebut, Instant dateFin) {
        Mono<List<Tache>> taches;
        taches = tacheRepository.findAll().collectList();
        return taches.flatMap(
            tach -> {
                String reponse = "The events that you have are: \n";
                for (Tache tache : tach) {
                    if (tache.getDateDebut().isAfter(dateDebut) && tache.getDateDebut().isBefore(dateFin)) {
                        reponse += '-';
                        reponse += tache.getIntitule();
                        reponse += '\n';
                    }
                }
                System.out.println(reponse);
                return Mono.just(reponse);
            }
        );
    }
}
