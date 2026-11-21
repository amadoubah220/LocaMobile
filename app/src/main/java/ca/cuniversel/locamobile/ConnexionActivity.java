package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.ConnexionRequete;
import ca.cuniversel.locamobile.reseau.Profil;
import ca.cuniversel.locamobile.reseau.TokenReponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";

    private EditText champCourriel, champMotDePasse;
    private TextView resultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        champCourriel = findViewById(R.id.champCourriel);
        champMotDePasse = findViewById(R.id.champMotDePasse);
        resultat = findViewById(R.id.resultat);
        Button boutonConnecter = findViewById(R.id.boutonConnecter);

        boutonConnecter.setOnClickListener(v -> connecter());
    }

    // Exercice 4, étape 5
    private void connecter() {
        String courriel = champCourriel.getText().toString().trim();
        String motDePasse = champMotDePasse.getText().toString();

        boolean ok = true;
        if (courriel.isEmpty()) { champCourriel.setError("Courriel obligatoire"); ok = false; }
        if (motDePasse.isEmpty()) { champMotDePasse.setError("Mot de passe obligatoire"); ok = false; }
        if (!ok) return;

        resultat.setText("Connexion…");

        // Temps 1 : courriel + mot de passe, une seule fois, dans le corps du POST
        ClientApi.get().connecter(new ConnexionRequete(courriel, motDePasse))
                .enqueue(new Callback<TokenReponse>() {
            @Override
            public void onResponse(@NonNull Call<TokenReponse> call,
                                   @NonNull Response<TokenReponse> reponse) {
                Log.d(TAG, "connexion -> code " + reponse.code());
                TokenReponse t = reponse.body();
                if (reponse.isSuccessful() && t != null) {
                    // On range le jeton et le nom (jamais le mot de passe)
                    Session.enregistrer(ConnexionActivity.this, t.token, t.nom);
                    // Le jeton existe enfin : c'est ICI qu'on appelle api/moi
                    chargerProfil();
                } else {
                    // Exercice 4, étape 7 : le message du serveur est dans errorBody()
                    resultat.setText("Erreur " + reponse.code() + " : " + messageServeur(reponse));
                }
            }

            @Override
            public void onFailure(@NonNull Call<TokenReponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure connexion : " + t.getMessage());
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    // Exercice 4, étapes 5-6 : temps 2 — présenter le jeton à api/moi
    private void chargerProfil() {
        String entete = "Bearer " + Session.jeton(this);

        ClientApi.get().monProfil(entete).enqueue(new Callback<Profil>() {
            @Override
            public void onResponse(@NonNull Call<Profil> call, @NonNull Response<Profil> reponse) {
                Log.d(TAG, "api/moi -> code " + reponse.code());
                Profil p = reponse.body();
                if (reponse.isSuccessful() && p != null) {
                    resultat.setText("Connecté (code " + reponse.code() + ")\n\n"
                            + "Nom : " + p.nom + "\n"
                            + "Rôle : " + p.role + "\n"
                            + "Courriel : " + p.courriel);
                } else {
                    resultat.setText("api/moi : erreur " + reponse.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profil> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure api/moi : " + t.getMessage());
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    /** Lit le champ « message » du JSON d'erreur, sans écrire de classe. */
    private String messageServeur(Response<?> reponse) {
        try {
            if (reponse.errorBody() == null) return "(aucun message)";
            String texte = reponse.errorBody().string();
            JsonObject json = new Gson().fromJson(texte, JsonObject.class);
            if (json != null && json.has("message")) {
                return json.get("message").getAsString();
            }
            return texte;
        } catch (Exception e) {
            return "(message illisible)";
        }
    }
}
