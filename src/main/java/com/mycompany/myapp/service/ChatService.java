package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
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

    public Mono<String> getMeetings() {
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
                                            String reponse = "The differents meetings that you have are: \n";
                                            for (Tache tache : tach) {
                                                if (tache.getIntitule().contains("meeting")) {
                                                    reponse +=
                                                        "\n-" +
                                                        tache.getDescription() +
                                                        " from " +
                                                        tache.getDateDebut() +
                                                        " to " +
                                                        tache.getDateFin();
                                                    reponse += '\n';
                                                }
                                            }
                                            System.out.println(reponse);
                                            return Mono.just(reponse);
                                        }
                                    );
                            }
                        );
                }
            );
    }

    public Mono<String> getTasks() {
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
                                            String reponse = "The differents tasks that you have are: \n";
                                            for (Tache tache : tach) {
                                                if (tache.getIntitule().contains("task")) {
                                                    reponse +=
                                                        "\n" +
                                                        "-" +
                                                        tache.getDescription() +
                                                        " starting from : " +
                                                        tache.getDateDebut() +
                                                        " to the deadline " +
                                                        tache.getDateFin();
                                                    reponse += '\n';
                                                }
                                            }
                                            System.out.println(reponse);
                                            return Mono.just(reponse);
                                        }
                                    );
                            }
                        );
                }
            );
    }

    public Mono<String> getTachesBetween(Instant dateDebut, Instant dateFin) {
        Mono<List<Tache>> taches;
        taches = tacheRepository.findAll().collectList();
        return taches.flatMap(
            tach -> {
                String reponse = "The events that you have between " + dateDebut + " and " + dateFin + " are: \n";
                for (Tache tache : tach) {
                    if (tache.getDateDebut().isAfter(dateDebut) && tache.getDateFin().isBefore(dateFin)) {
                        reponse += '-';
                        reponse += tache.getDescription();
                        reponse += '\n';
                    }
                }
                System.out.println(reponse);
                return Mono.just(reponse);
            }
        );
    }
}
