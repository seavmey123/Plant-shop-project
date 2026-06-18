package kh.rupp.edu.plantshopproject.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class ShopFragment extends Fragment {

    private RecyclerView rvProducts;
    private PlantAdapter adapter;
    private List<Plant> allPlants = new ArrayList<>(); // Stores live MAMP items

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProducts = view.findViewById(R.id.rv_products);
        adapter    = new PlantAdapter(requireContext(), new ArrayList<>());
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(adapter);

        // Live MAMP Search filter
        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterLocal(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        adapter.setOnPlantClickListener(new PlantAdapter.OnPlantClickListener() {
            @Override public void onPlantClick(Plant plant) { openDetail(plant); }
            @Override public void onAddToCartClick(Plant plant) { addToCart(plant); }
        });

        loadPlantsFromMAMP();
    }

    //  CHANGED: Talk to your local database get_plants.php script instead of Perenual
    private void loadPlantsFromMAMP() {
        ApiClient.getService().getPlants().enqueue(new Callback<List<Plant>>() {
            @Override
            public void onResponse(@NonNull Call<List<Plant>> call, @NonNull Response<List<Plant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPlants = response.body();
                    adapter.updateData(allPlants);
                } else {
                    Log.e("MAMP_SHOP", "Response failed, loading fallback data");
                    loadFakePlants();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Plant>> call, @NonNull Throwable t) {
                Log.e("MAMP_SHOP", "Connection error: " + t.getMessage());
                loadFakePlants();
            }
        });
    }

    // Performs live client-side filtering on your active database list
    private void filterLocal(String query) {
        if (query.isEmpty()) {
            adapter.updateData(allPlants);
            return;
        }

        List<Plant> filtered = new ArrayList<>();
        for (Plant p : allPlants) {
            if (p.getCommonName() != null &&
                    p.getCommonName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(p);
            }
        }
        adapter.updateData(filtered);
    }

    // Kept as an offline error fallback list
    private void loadFakePlants() {
        List<Plant> fakePlants = new ArrayList<>();
        fakePlants.add(createFakePlant(1, "Monstera Deliciosa", "Perennial", "Average", null));
        fakePlants.add(createFakePlant(2, "Snake Plant", "Perennial", "Minimum", null));
        fakePlants.add(createFakePlant(3, "Peace Lily", "Perennial", "Average", null));
        fakePlants.add(createFakePlant(4, "Pothos", "Perennial", "Average", null));

        allPlants = fakePlants;
        adapter.updateData(fakePlants);
    }

    private Plant createFakePlant(int id, String name, String cycle,
                                  String watering, String imageUrl) {
        Plant plant = new Plant();
        plant.setId(id);
        plant.setCommonName(name);
        plant.setCycle(cycle);
        plant.setWatering(watering);
        plant.setImageUrl(imageUrl);
        return plant;
    }

    private void openDetail(Plant plant) {
        String finalImg = (plant.getDefaultImage() != null) ? plant.getDefaultImage().getMediumUrl() : plant.getImageUrl();

        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra("product_id",     plant.getId());
        intent.putExtra("product_title",  plant.getCommonName());
        intent.putExtra("product_price",  12.00);
        intent.putExtra("product_image",  finalImg);
        intent.putExtra("product_desc",   "Cycle: " + plant.getCycle());
        intent.putExtra("product_cat",    plant.getCycle());
        intent.putExtra("product_rating", 4.5f);
        intent.putExtra("product_reviews", 18);
        startActivity(intent);
    }

    private void addToCart(Plant plant) {
        String finalImg = (plant.getDefaultImage() != null) ? plant.getDefaultImage().getMediumUrl() : plant.getImageUrl();

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
                        finalImg,
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