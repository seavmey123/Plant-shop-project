package kh.rupp.edu.plantshopproject.api;

import java.util.List;
import kh.rupp.edu.plantshopproject.model.LoginRequest;
import kh.rupp.edu.plantshopproject.model.LoginResponse;
import kh.rupp.edu.plantshopproject.model.Plant;
import kh.rupp.edu.plantshopproject.model.PlantResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ── LOCAL MAMP DATABASE API ──

    // Call this to get all products for the grid lists
    @GET("get_plants.php")
    Call<List<Plant>> getPlants();

    // Call this when clicking a card to open a specific product detail
    @GET("get_plants.php")
    Call<Plant> getPlantById(@Query("id") int plantId);


    // ── OLD PLANT API (Perenual - kept so old methods don't break) ──

    @GET("species-list")
    Call<PlantResponse> getAllPlants(
            @Query("key") String apiKey,
            @Query("page") int page
    );

    @GET("species-list")
    Call<PlantResponse> searchPlants(
            @Query("key") String apiKey,
            @Query("q") String query
    );

    @GET("species/details/{id}")
    Call<Plant> getPlantDetail(
            @Query("key") String apiKey,
            @Path("id") int id
    );

    // ── FAKESTORE API (login only - perfectly preserved) ──
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest body);
}