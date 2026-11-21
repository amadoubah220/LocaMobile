package ca.cuniversel.locamobile;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Exercice 4, étape 3 — petit carnet clé -> valeur privé à l'application.
 * On n'y range QUE le jeton et le nom, jamais le mot de passe.
 */
public final class Session {

    private static final String FICHIER = "session";
    private static final String CLE_JETON = "jeton";
    private static final String CLE_NOM = "nom";

    private Session() { }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(FICHIER, Context.MODE_PRIVATE);
    }

    public static void enregistrer(Context context, String jeton, String nom) {
        prefs(context).edit()
                .putString(CLE_JETON, jeton)
                .putString(CLE_NOM, nom)
                .apply();
    }

    /** null si personne n'est connecté. */
    public static String jeton(Context context) {
        return prefs(context).getString(CLE_JETON, null);
    }

    public static String nom(Context context) {
        return prefs(context).getString(CLE_NOM, null);
    }
}
