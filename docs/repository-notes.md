# Atelier 3 — Notes sur la couche Repository

## 1. Choix d'interface

Les neuf repositories (package `tn.esprit.autoloc.repository`) étendent `JpaRepository<Entité, Long>` :
CRUD complet, `findAll` renvoie une `List`, tri/pagination et `saveAndFlush` disponibles.
Aucune annotation `@Repository` : Spring Data génère le proxy au démarrage.

| Interface | Étend | Justification |
|---|---|---|
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet, `findAll` renvoie une `List`, pagination des agences. |
| IEmployeRepository | JpaRepository<Employe, Long> | Idem ; tri possible par nom/rôle. |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | Idem ; pagination/tri du parc (tarif, catégorie). Renommé depuis `VehiculeRepository` (convention `I…`). |
| IEquipementRepository | JpaRepository<Equipement, Long> | Idem ; petite table de référence, `findAll` en `List`. |
| IClientRepository | JpaRepository<Client, Long> | Idem ; pagination des clients. |
| IReservationRepository | JpaRepository<Reservation, Long> | Idem ; tri par dates. |
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. |
| IPaiementRepository | JpaRepository<Paiement, Long> | Lecture des paiements. En composition, le code métier crée/retire un paiement via le `Contrat`. |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | Idem ; tri par `dateDebut`. |

Point de vigilance : `deleteAllInBatch` / `deleteAllByIdInBatch` contournent le contexte de persistance
(pas de cascade ni d'`orphanRemoval`) ; à éviter sur `Contrat` une fois la composition avec `Paiement` en place.

## 2. Anomalies SonarQube for IDE

> À vérifier/compléter avec votre propre analyse dans IntelliJ (capture du panneau à joindre).

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| `import jakarta.persistence.*;` dans les 9 entités | `java:S2208` — pas d'import générique : on ne voit plus ce dont la classe dépend, risque de collision de noms. | Remplacé par les imports explicites (`Entity`, `Table`, `Id`, `GeneratedValue`, `GenerationType`, `Column`, + `Enumerated`/`EnumType` si besoin). |
| `VehiculeRepository` : nom non conforme à la convention du projet | Convention AutoLoc `I…Repository` (critère d'évaluation, pas une règle Sonar). | Renommé en `IVehiculeRepository` ; injection mise à jour dans `DemoDataInitializer`. |
| Test `contextLoads()` sans assertion | `java:S2699` — un test doit contenir au moins une assertion. | Non modifié : test généré par Spring Initializr dont le but est de vérifier le démarrage du contexte. À signaler / ignorer si Sonar le remonte. |
| `pom.xml` : balise `<n>` au lieu de `<name>` et balises vides (`<license/>`, `<developer/>`, `<scm>`…) | Élément mal nommé et métadonnées vides = bruit / code mort dans le POM (règles XML de SonarQube). | `<n>` corrigé en `<name>`, balises vides supprimées. |
