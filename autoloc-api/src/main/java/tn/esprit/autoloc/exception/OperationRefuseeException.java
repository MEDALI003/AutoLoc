package tn.esprit.autoloc.exception;

/**
 * Opération refusée par une règle de gestion (lien déjà existant, lien inexistant,
 * second contrat pour une réservation...). Traduite en HTTP 409 Conflict.
 */
public class OperationRefuseeException extends RuntimeException {

    public OperationRefuseeException(String message) {
        super(message);
    }
}
