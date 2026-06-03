package kh.rupp.edu.plantshopproject.api;

import java.util.List;
import kh.rupp.edu.plantshopproject.model.LoginRequest;
import kh.rupp.edu.plantshopproject.model.LoginResponse;
import kh.rupp.edu.plantshopproject.model.Product;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @GET("products")
    Call<List<Product>> getAllProducts();

    @GET("products/category/{category}")
    Call<List<Product>> getProductsByCategory(@Path("category") String category);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int id);

    @GET("products/categories")
    Call<List<String>> getCategories();

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest body);
}
