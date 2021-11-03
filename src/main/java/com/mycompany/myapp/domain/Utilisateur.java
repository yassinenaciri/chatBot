package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
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
    @Field("date_debut")
    private Instant dateDebut;

    @Field("date_fin")
    private Instant dateFin;

    @DBRef
    @Field("evenements")
    @JsonIgnoreProperties(value = { "creneaux", "employee" }, allowSetters = true)
    private Set<Evenement> evenements = new HashSet<>();

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

    public Instant getDateDebut() {
        return this.dateDebut;
    }

    public Utilisateur dateDebut(Instant dateDebut) {
        this.setDateDebut(dateDebut);
        return this;
    }

    public void setDateDebut(Instant dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Instant getDateFin() {
        return this.dateFin;
    }

    public Utilisateur dateFin(Instant dateFin) {
        this.setDateFin(dateFin);
        return this;
    }

    public void setDateFin(Instant dateFin) {
        this.dateFin = dateFin;
    }

    public Set<Evenement> getEvenements() {
        return this.evenements;
    }

    public void setEvenements(Set<Evenement> evenements) {
        if (this.evenements != null) {
            this.evenements.forEach(i -> i.setEmployee(null));
        }
        if (evenements != null) {
            evenements.forEach(i -> i.setEmployee(this));
        }
        this.evenements = evenements;
    }

    public Utilisateur evenements(Set<Evenement> evenements) {
        this.setEvenements(evenements);
        return this;
    }

    public Utilisateur addEvenements(Evenement evenement) {
        this.evenements.add(evenement);
        evenement.setEmployee(this);
        return this;
    }

    public Utilisateur removeEvenements(Evenement evenement) {
        this.evenements.remove(evenement);
        evenement.setEmployee(null);
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
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            "}";
    }
}
