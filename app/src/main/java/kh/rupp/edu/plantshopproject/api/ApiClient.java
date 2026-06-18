package kh.rupp.edu.plantshopproject.api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // ⚠️ CHANGED THIS LINE: Pointing to your local MAMP server folder instead of Perenual
    private static final String BASE_URL = "http://10.0.2.2:8888/plant_shop_api/";
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getService() {
        return getInstance().create(ApiService.class);
    }

    // Keep FakeStore for login only (Leaves your login system perfectly untouched!)
    private static final String FAKESTORE_URL = "https://fakestoreapi.com/";
    private static Retrofit fakestoreRetrofit;

    public static Retrofit getFakestoreInstance() {
        if (fakestoreRetrofit == null) {
            fakestoreRetrofit = new Retrofit.Builder()
                    .baseUrl(FAKESTORE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return fakestoreRetrofit;
    }

    public static ApiService getFakestoreService() {
        return getFakestoreInstance().create(ApiService.class);
    }
}