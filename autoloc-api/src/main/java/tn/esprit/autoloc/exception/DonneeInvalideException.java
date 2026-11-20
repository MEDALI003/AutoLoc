package tn.esprit.autoloc.exception;

/**
 * Donnée reçue invalide (ex. paiement sans montant positif). Traduite en HTTP 400 Bad Request.
 * Étant une RuntimeException, elle provoque le rollback de la transaction en cours.
 */
public class DonneeInvalideException extends RuntimeException {

    public DonneeInvalideException(String message) {
        super(message);
    }
}
