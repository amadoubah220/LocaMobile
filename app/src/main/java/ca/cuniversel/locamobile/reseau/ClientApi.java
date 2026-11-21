package ca.cuniversel.locamobile.reseau;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Exercice 1, étape 6 — singleton : Retrofit est construit une seule fois.
 * 10.0.2.2 = l'ordinateur hôte vu depuis l'émulateur (127.0.0.1 désignerait l'émulateur lui-même).
 */
public final class ClientApi {

    private static final String URL_BASE = "http://10.0.2.2:8080/";
    private static LocaMobileApi instance;

    private ClientApi() { }

    public static synchronized LocaMobileApi get() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(LocaMobileApi.class);
        }
        return instance;
    }
}
