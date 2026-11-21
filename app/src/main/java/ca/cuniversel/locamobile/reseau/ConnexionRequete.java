package ca.cuniversel.locamobile.reseau;

/** Exercice 4, étape 1 — corps du POST api/auth/connexion (Gson -> JSON). */
public class ConnexionRequete {
    public String courriel;
    public String motDePasse;

    public ConnexionRequete(String courriel, String motDePasse) {
        this.courriel = courriel;
        this.motDePasse = motDePasse;
    }
}
