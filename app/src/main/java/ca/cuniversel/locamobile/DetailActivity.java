package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    /** Exercice 3, étape 3 : le SEUL extra transporté par l'Intent. */
    public static final String CLE_ID = "id_logement";

    private static final String TAG = "LocaMobile";

    private TextView detailTitre, detailVille, detailPrix, detailVoyageurs, detailProprietaire, etat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailTitre = findViewById(R.id.detailTitre);
        detailVille = findViewById(R.id.detailVille);
        detailPrix = findViewById(R.id.detailPrix);
        detailVoyageurs = findViewById(R.id.detailVoyageurs);
        detailProprietaire = findViewById(R.id.detailProprietaire);
        etat = findViewById(R.id.etat);

        // Exercice 3, étape 4 : on lit seulement l'id, le serveur donne le reste
        int id = getIntent().getIntExtra(CLE_ID, -1);
        Log.d(TAG, "Fiche demandée pour l'id " + id);
        etat.setText("Chargement…");

        ClientApi.get().trouverLogement(id).enqueue(new Callback<Logement>() {
            @Override
            public void onResponse(@NonNull Call<Logement> call, @NonNull Response<Logement> reponse) {
                Log.d(TAG, "Fiche : " + call.request().url() + " -> code " + reponse.code());
                Logement l = reponse.body();
                if (reponse.isSuccessful() && l != null) {
                    etat.setText("");
                    detailTitre.setText(l.titre);
                    detailVille.setText(l.ville);
                    detailPrix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));
                    detailVoyageurs.setText("Voyageurs : " + l.nbVoyageurs);
                    detailProprietaire.setText("Propriétaire : " + l.proprietaireNom);
                } else if (reponse.code() == 404) {
                    // Exercice 3, étape 5 : un 404 n'est pas une panne
                    etat.setText("Ce logement n'existe plus (404).");
                } else {
                    etat.setText("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Logement> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure fiche : " + t.getMessage());
                etat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }
}
