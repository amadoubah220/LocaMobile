package ca.cuniversel.locamobile.reseau;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Exercice 1, étape 5 — la liste des endpoints. */
public interface LocaMobileApi {

    @GET("api/logements")
    Call<List<Logement>> listerLogements();

    // Exercice 3, étape 1 : trouverLogement(3) -> api/logements/3
    @GET("api/logements/{id}")
    Call<Logement> trouverLogement(@Path("id") int id);

    // Exercice 3, étape 2 : chercherParVille("Gatineau") -> api/logements?ville=Gatineau
    @GET("api/logements")
    Call<List<Logement>> chercherParVille(@Query("ville") String ville);

    // Exercice 4, étape 2 : temps 1 — courriel + mot de passe dans le corps du POST
    @POST("api/auth/connexion")
    Call<TokenReponse> connecter(@Body ConnexionRequete requete);

    // Exercice 4, étape 2 : temps 2 — le jeton dans l'en-tête Authorization
    @GET("api/moi")
    Call<Profil> monProfil(@Header("Authorization") String entete);
}
