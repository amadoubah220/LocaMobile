package ca.cuniversel.locamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";

    private LinearLayout conteneurLogements;
    private TextView messageErreur;
    private EditText champVille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        conteneurLogements = findViewById(R.id.conteneurLogements);
        messageErreur = findViewById(R.id.messageErreur);
        champVille = findViewById(R.id.champVille);
        Button boutonChercher = findViewById(R.id.boutonChercher);

        // Exercice 3, étape 8 : le bouton relance le chargement avec la ville saisie
        boutonChercher.setOnClickListener(v -> chargerLogements());

        // Exercice 4, étape 4 : le bouton « Connexion » ouvre ConnexionActivity
        Button boutonConnexion = findViewById(R.id.boutonConnexion);
        boutonConnexion.setOnClickListener(v ->
                startActivity(new Intent(this, ConnexionActivity.class)));
    }

    // Exercice 2, étape 7 : l'appel est fait à chaque retour sur l'écran
    @Override
    protected void onResume() {
        super.onResume();
        chargerLogements();
    }

    private void chargerLogements() {
        String ville = champVille.getText().toString().trim();

        // Exercice 3, étape 8 : champ vide -> liste complète ; sinon -> recherche
        Call<List<Logement>> appel = ville.isEmpty()
                ? ClientApi.get().listerLogements()
                : ClientApi.get().chercherParVille(ville);

        Log.d(TAG, "1 - avant enqueue");
        appel.enqueue(new Callback<List<Logement>>() {
            @Override
            public void onResponse(@NonNull Call<List<Logement>> call,
                                   @NonNull Response<List<Logement>> reponse) {
                Log.d(TAG, "3 - début de onResponse");
                List<Logement> logements = reponse.body();
                Log.d(TAG, call.request().url() + " -> code " + reponse.code() + ", "
                        + (logements == null ? 0 : logements.size()) + " logements");

                // Exercice 2, étape 6 : onResponse ne veut pas dire « succès »
                if (reponse.isSuccessful() && logements != null) {
                    if (logements.isEmpty() && !ville.isEmpty()) {
                        afficherErreur("Aucun logement à " + ville + ".");
                    } else {
                        afficher(logements);
                    }
                } else {
                    afficherErreur("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Logement>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure : " + t.getMessage());
                afficherErreur("Serveur injoignable : " + t.getMessage());
            }
        });
        Log.d(TAG, "2 - après enqueue");
    }

    // Exercice 2, étape 3 : une carte par logement reçu
    private void afficher(List<Logement> logements) {
        conteneurLogements.removeAllViews();
        messageErreur.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Logement l : logements) {
            View carte = inflater.inflate(R.layout.item_logement, conteneurLogements, false);

            TextView titre = carte.findViewById(R.id.carteTitre);
            TextView ville = carte.findViewById(R.id.carteVille);
            TextView prix = carte.findViewById(R.id.cartePrix);

            titre.setText(l.titre);
            ville.setText(l.ville);
            // Exercice 2, étape 4 : « 145,50 $ / nuit »
            prix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));

            // Exercice 3, étape 3 : un seul extra, l'id
            carte.setOnClickListener(v -> {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(DetailActivity.CLE_ID, l.id);
                startActivity(intent);
            });

            conteneurLogements.addView(carte);
        }
    }

    // Exercice 2, étape 5
    private void afficherErreur(String texte) {
        conteneurLogements.removeAllViews();
        messageErreur.setText(texte);
        messageErreur.setVisibility(View.VISIBLE);
    }
}
