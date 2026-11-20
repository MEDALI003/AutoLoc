package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 50)
    private String ville;

    @Column(nullable = false)
    private String adresse;

    @Column(length = 20)
    private String telephone;

    @OneToMany(mappedBy = "agence")
    @JsonIgnoreProperties("agence")
    private List<Employe> employes = new ArrayList<>();

    @OneToMany(mappedBy = "agence")
    @JsonIgnoreProperties("agence")
    private List<Vehicule> vehicules = new ArrayList<>();

    public void addEmploye(Employe employe) {
        employes.add(employe);
        employe.setAgence(this);
    }

    public void removeEmploye(Employe employe) {
        employes.remove(employe);
        employe.setAgence(null);
    }

    public void addVehicule(Vehicule vehicule) {
        vehicules.add(vehicule);
        vehicule.setAgence(this);
    }

    public void removeVehicule(Vehicule vehicule) {
        vehicules.remove(vehicule);
        vehicule.setAgence(null);
    }
}
