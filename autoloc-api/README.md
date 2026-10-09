# autoloc-api

API Spring Boot du projet **AutoLoc** (UP ASI — Atelier 1, Séance 2).

## Stack
Java 17 · Maven · Spring Boot 3.3 · Spring Data JPA · MySQL · Lombok · Validation · DevTools

## Lancer le projet

1. **MySQL** démarré sur `localhost:3306`. La base `autoloc_db` est créée automatiquement
   (`createDatabaseIfNotExist=true`).
2. **Mot de passe** : il n'est pas versionné. Définir la variable d'environnement `DB_PASSWORD`
   - IntelliJ : *Run → Edit Configurations → Environment variables* → `DB_PASSWORD=votre_mot_de_passe`
   - Terminal : `export DB_PASSWORD=...` (Linux/Mac) ou `set DB_PASSWORD=...` (Windows)
   - Optionnel : `DB_USERNAME` (par défaut `root`)
3. **IntelliJ** : *Settings → Build, Execution, Deployment → Compiler → Annotation Processors →
   Enable annotation processing* + plugin Lombok installé.
4. Lancer `AutolocApiApplication` (▶) ou `mvn spring-boot:run`.
5. Vérifier dans la console : `Started AutolocApiApplication in … seconds`
   et les requêtes `create table …` pour les 9 tables.

### Profil dev (facultatif)
Active la base `autoloc_dev_db`, des logs plus verbeux et l'insertion de 3 véhicules de démonstration :

```
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
(IntelliJ : *Active profiles* = `dev`)

## Structure

```
tn.esprit.autoloc
├── config          DemoDataLoader (CommandLineRunner, profil dev)
├── domain          9 entités JPA + 5 énumérations
├── repository      VehiculeRepository
├── service         (Atelier 4)
├── web.controller  (Atelier 5)
└── web.dto         (Atelier 6)
```

## Entités (sans associations — ajoutées en Atelier 2)

| Entité       | Attributs |
|--------------|-----------|
| Vehicule     | idVehicule, immatriculation, marque, modele, categorie, tarifJournalier, statut |
| Agence       | idAgence, nom, ville, adresse, telephone |
| Client       | idClient, nom, prenom, email, telephone, numPermis, dateInscription |
| Employe      | idEmploye, nom, prenom, role |
| Equipement   | idEquipement, libelle |
| Reservation  | idReservation, dateDebut, dateFin, statut |
| Contrat      | idContrat, dateSignature, montantTotal, valide |
| Paiement     | idPaiement, montant, datePaiement, modePaiement |
| Maintenance  | idMaintenance, dateDebut, dateFin, description |

Énumérations : `StatutVehicule`, `CategorieVehicule`, `RoleEmploye`, `StatutReservation`, `ModePaiement`.

Toutes les entités utilisent `@GeneratedValue(strategy = GenerationType.IDENTITY)` et un Lombok ciblé
(`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`) — pas de `@Data`, pour éviter les
boucles `equals/hashCode/toString` une fois les associations bidirectionnelles ajoutées.
