# Atelier 8 — Affectation, désaffectation et cascade

> À compléter : remplissez la colonne « Code obtenu », collez les requêtes SQL de votre console et ajoutez les
> captures dans `docs/swagger/`.

Préparation : supprimez la base (`DROP DATABASE autoloc_db;`) car le schéma a changé, lancez l'application,
puis ouvrez <http://localhost:8081/swagger-ui.html>. Créez d'abord les données via Swagger :
une agence (`POST /api/agences`), un employé (`POST /api/employes`), un véhicule (`POST /api/vehicules`),
un équipement (`POST /api/equipements`) et deux réservations (`POST /api/reservations`). Tous ont l'id 1, sauf
la deuxième réservation qui a l'id 2.

## 1. Relations traitées

Seuls les champs de relation ont été ajoutés aux entités ; aucun autre attribut n'a été modifié.

| Relation | Type | Côté propriétaire | mappedBy | Cascade | orphanRemoval | Chargement |
|---|---|---|---|---|---|---|
| Agence – Employe | OneToMany / ManyToOne | `Employe.agence` (`employe.agence_id`, nullable) | `Agence.employes` | aucune | non | ManyToOne EAGER (défaut) / OneToMany LAZY |
| Vehicule – Equipement | ManyToMany | `Vehicule.equipements` (table `vehicule_equipement`) | `Equipement.vehicules` | aucune | — | LAZY |
| Reservation – Contrat | OneToOne | `Contrat.reservation` (`contrat.reservation_id`, UNIQUE) | `Reservation.contrat` | ALL (Reservation → Contrat) | non | Contrat → Reservation LAZY / Reservation → Contrat EAGER |
| Agence – Vehicule | OneToMany / ManyToOne | `Vehicule.agence` (`vehicule.agence_id`, nullable) | `Agence.vehicules` | aucune | non | ManyToOne EAGER (défaut) / OneToMany LAZY |
| Contrat – Paiement | OneToMany, composition | `Paiement.contrat` (`paiement.contrat_id`, NOT NULL) | `Contrat.paiements` | ALL | oui | LAZY |

Méthodes de synchronisation : `Agence.addEmploye/removeEmploye`, `Vehicule.addEquipement/removeEquipement`,
`Reservation.etablirContrat/retirerContrat`, `Contrat.addPaiement/removePaiement`.
Boucles JSON : `@JsonIgnoreProperties` (Agence ↔ Employe), `@JsonIgnore` (`Equipement.vehicules`),
`@JsonManagedReference` / `@JsonBackReference` (Reservation → Contrat → Paiement).

## 2. Endpoints

| Méthode | URI | Effet | Codes |
|---|---|---|---|
| POST | `/api/agences/{agenceId}/employes/{employeId}` | Affecte l'employé | 200, 404, 409 |
| DELETE | `/api/agences/{agenceId}/employes/{employeId}` | Désaffecte l'employé | 204, 404, 409 |
| POST | `/api/vehicules/{id}/equipements/{equipementId}` | Affecte l'équipement | 200, 404, 409 |
| DELETE | `/api/vehicules/{id}/equipements/{equipementId}` | Retire l'équipement | 204, 404, 409 |
| POST / DELETE | `/api/agences/{agenceId}/vehicules/{vehiculeId}` | Affecte / désaffecte un véhicule | 200 / 204, 404, 409 |
| PUT | `/api/agences/{agenceCibleId}/vehicules/{vehiculeId}` | Transfère le véhicule (met à jour `vehicule.agence_id`) | 200, 404, 409 |
| POST | `/api/reservations/{id}/contrat` | Crée le contrat avec ses paiements | 201, 400, 404, 409 |
| POST | `/api/contrats/{id}/paiements` | Ajoute un paiement | 201, 400, 404 |
| DELETE | `/api/contrats/{id}/paiements/{paiementId}` | Retire (supprime) le paiement | 204, 404, 409 |

Erreurs : 404 introuvable (`ResourceNotFoundException`), 409 règle de gestion (`OperationRefuseeException`),
400 donnée invalide (`DonneeInvalideException`).

## 3. Les dix scénarios

| N° | Requête | Attendu | Code obtenu | Capture |
|---|---|---|---|---|
| 1 | `POST /api/agences/1/employes/1` puis `GET /api/agences/1` | 200, l'employé apparaît dans l'agence et `GET /api/employes/1` montre l'agence | | `01.png` |
| 2 | `POST /api/agences/1/employes/1` une seconde fois | 409 | | `02.png` |
| 3 | `DELETE /api/agences/1/employes/1` puis `GET /api/employes/1` | 204 puis 200, `agence: null` | | `03.png` |
| 4 | `POST` puis `DELETE /api/vehicules/1/equipements/1` | 200 puis 204, équipement conservé | | `04.png` |
| 5 | `POST /api/reservations/1/contrat` (corps 1) | 201 | | `05.png` |
| 6 | `DELETE /api/contrats/1/paiements/1` | 204, `GET /api/paiements/1` → 404 | | `06.png` |
| 7 | `POST /api/reservations/1/contrat` une seconde fois | 409 | | `07.png` |
| 8 | `DELETE /api/contrats/1` | 204, paiements supprimés | | `08.png` |
| 9 | `POST /api/reservations/2/contrat` (corps 2) | 400, aucun contrat créé | | `09.png` |
| 10 | `POST /api/agences/1/employes/999` | 404 | | `10.png` |

Corps 1 (pour le corps 2, remplacez le montant du deuxième paiement par `-5.00`) :

```json
{
  "dateSignature": "2026-11-20",
  "montantTotal": 300.00,
  "valide": true,
  "paiements": [
    { "montant": 100.00, "datePaiement": "2026-11-20T10:00:00", "modePaiement": "CARTE" },
    { "montant": 150.00, "datePaiement": "2026-11-20T10:00:00", "modePaiement": "VIREMENT" },
    { "montant": 50.00,  "datePaiement": "2026-11-20T10:00:00", "modePaiement": "ESPECES" }
  ]
}
```

## 4. Requêtes SQL (attendues, à remplacer par les vôtres)

- **Scénario 4** : `insert into vehicule_equipement (vehicule_id, equipement_id) values (?, ?)` puis
  `delete from vehicule_equipement where vehicule_id=? and equipement_id=?`. Seule la table de jointure change ;
  l'équipement reste.
- **Scénario 5** : un `insert into contrat ...`, puis trois `insert into paiement ...` au commit. C'est la cascade
  ALL qui les déclenche, sans aucun `save` sur les paiements.
- **Scénario 6** : `delete from paiement where id_paiement=?`. L'`orphanRemoval` supprime le paiement retiré de la liste.
- **Scénario 9** : `insert into contrat ...`, puis l'exception sur le 2e paiement, puis un rollback. Aucune ligne n'est conservée.

## 5. Questions

1. **Synchroniser les deux côtés** : Hibernate n'écrit la clé étrangère qu'à partir du côté propriétaire, alors
   que le côté inverse est celui qu'on lit en mémoire et en JSON. Sans synchronisation, la base et les objets se
   contredisent, et la cascade comme l'orphanRemoval (qui partent de la collection) ne fonctionnent pas.
2. **Désaffecter ou supprimer** : désaffecter supprime le lien (`agence_id = NULL`, ligne de jointure
   supprimée) et l'entité est conservée. Supprimer efface l'entité elle-même. Dans une composition
   (Contrat – Paiement), retirer un paiement le supprime.
3. **@Transactional** : il garantit l'atomicité, car tout est validé ou tout est annulé sur une
   RuntimeException (scénario 9). Il fournit aussi un seul contexte de persistance : le dirty checking évite
   les `save`, et les collections LAZY sont accessibles.
4. **ManyToMany** : `Vehicule` est propriétaire (`@JoinTable`). La relation donne la table
   `vehicule_equipement (vehicule_id, equipement_id)`.

## 6. Anomalies SonarQube for IDE

> À compléter avec votre analyse dans IntelliJ.

| Anomalie | Correction |
|---|---|
| `update` qui faisait `save` de l'objet reçu et écrasait les liens | L'entité existante est chargée et seuls les champs simples sont copiés |
| `server.servlet.context-path=/api` en plus de `@RequestMapping("/api/...")`, d'où des URI `/api/api/...` | `context-path` retiré |
