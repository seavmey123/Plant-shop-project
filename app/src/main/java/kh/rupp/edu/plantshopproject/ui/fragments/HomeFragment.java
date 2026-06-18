package kh.rupp.edu.plantshopproject.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.adapter.PlantAdapter;
import kh.rupp.edu.plantshopproject.api.ApiClient;
import kh.rupp.edu.plantshopproject.db.AppDatabase;
import kh.rupp.edu.plantshopproject.db.CartItem;
import kh.rupp.edu.plantshopproject.model.Plant;
import kh.rupp.edu.plantshopproject.ui.ProductDetailActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvFlashSales;
    private PlantAdapter productAdapter;
    private PlantAdapter flashSaleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvProducts = view.findViewById(R.id.rv_products);
        rvFlashSales = view.findViewById(R.id.rv_flash_sales);

        // Products grid
        productAdapter = new PlantAdapter(requireContext(), new ArrayList<>());
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(productAdapter);

        // Flash sales horizontal
        flashSaleAdapter = new PlantAdapter(requireContext(), new ArrayList<>());
        rvFlashSales.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFlashSales.setAdapter(flashSaleAdapter);

        // Click listeners
        productAdapter.setOnPlantClickListener(new PlantAdapter.OnPlantClickListener() {
            @Override public void onPlantClick(Plant plant) { openDetail(plant); }
            @Override public void onAddToCartClick(Plant plant) { addToCart(plant); }
        });

        flashSaleAdapter.setOnPlantClickListener(new PlantAdapter.OnPlantClickListener() {
            @Override public void onPlantClick(Plant plant) { openDetail(plant); }
            @Override public void onAddToCartClick(Plant plant) { addToCart(plant); }
        });

        loadPlants();
    }

    private void loadPlants() {
        // ⚠️ UPDATED: Using the new clean List<Plant> interface signature directly matching MAMP
        ApiClient.getService().getPlants().enqueue(new Callback<List<Plant>>() {
            @Override
            public void onResponse(@NonNull Call<List<Plant>> call,
                                   @NonNull Response<List<Plant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Plant> serverPlants = response.body();

                    // Update both of your layouts with your real MAMP data entries
                    productAdapter.updateData(serverPlants);
                    flashSaleAdapter.updateData(serverPlants);
                } else {
                    // MAMP error fallback
                    Log.e("API_FAIL", "Response code: " + response.code());
                    loadFakePlants();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Plant>> call, @NonNull Throwable t) {
                // Connection or MAMP server offline fallback
                Log.e("API_FAIL", "Network error link dropped: " + t.getMessage());
                loadFakePlants();
            }
        });
    }

    private void loadFakePlants() {
        List<Plant> emptyList = new ArrayList<>();
        productAdapter.updateData(emptyList);
        flashSaleAdapter.updateData(emptyList);
    }

    private void openDetail(Plant plant) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra("product_id",    plant.getId());
        intent.putExtra("product_title", plant.getCommonName());
        intent.putExtra("product_price", 12.00);
        intent.putExtra("product_image", plant.getImageUrl());
        intent.putExtra("product_desc",  "Watering: " + plant.getWatering()
                + "\nSunlight: " + plant.getSunlight()
                + "\nCycle: " + plant.getCycle());
        intent.putExtra("product_cat",   plant.getCycle());
        startActivity(intent);
    }

    private void addToCart(Plant plant) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            CartItem existing = db.cartDao().getItemById(plant.getId());

            if (existing != null) {
                existing.quantity++;
                db.cartDao().update(existing);
            } else {
                db.cartDao().insert(new CartItem(
                        plant.getId(),
                        plant.getCommonName(),
                        12.00,
                        plant.getImageUrl(),
                        plant.getCycle(),
                        1
                ));
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(),
                                plant.getCommonName() + " added to cart 🌿",
                                Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}