package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Utilisateur.
 */
@Document(collection = "utilisateur")
public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull(message = "must not be null")
    @Field("nom_complet")
    private String nomComplet;

    @DBRef
    @Field("compte")
    private User compte;

    @DBRef
    @Field("taches")
    @JsonIgnoreProperties(value = { "utilisateur" }, allowSetters = true)
    private Set<Tache> taches = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Utilisateur id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomComplet() {
        return this.nomComplet;
    }

    public Utilisateur nomComplet(String nomComplet) {
        this.setNomComplet(nomComplet);
        return this;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public User getCompte() {
        return this.compte;
    }

    public void setCompte(User user) {
        this.compte = user;
    }

    public Utilisateur compte(User user) {
        this.setCompte(user);
        return this;
    }

    public Set<Tache> getTaches() {
        return this.taches;
    }

    public void setTaches(Set<Tache> taches) {
        if (this.taches != null) {
            this.taches.forEach(i -> i.setUtilisateur(null));
        }
        if (taches != null) {
            taches.forEach(i -> i.setUtilisateur(this));
        }
        this.taches = taches;
    }

    public Utilisateur taches(Set<Tache> taches) {
        this.setTaches(taches);
        return this;
    }

    public Utilisateur addTaches(Tache tache) {
        this.taches.add(tache);
        tache.setUtilisateur(this);
        return this;
    }

    public Utilisateur removeTaches(Tache tache) {
        this.taches.remove(tache);
        tache.setUtilisateur(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Utilisateur)) {
            return false;
        }
        return id != null && id.equals(((Utilisateur) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Utilisateur{" +
            "id=" + getId() +
            ", nomComplet='" + getNomComplet() + "'" +
            "}";
    }
}
