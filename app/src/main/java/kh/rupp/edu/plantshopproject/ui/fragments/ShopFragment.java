package kh.rupp.edu.plantshopproject.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import kh.rupp.edu.plantshopproject.model.PlantResponse;
import kh.rupp.edu.plantshopproject.ui.ProductDetailActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopFragment extends Fragment {

    private static final String API_KEY = "sk-NRCo6a2151bb5bdb517930";

    private RecyclerView rvProducts;
    private PlantAdapter adapter;
    private List<Plant> allPlants = new ArrayList<>();

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

        // Search filter
        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    adapter.updateData(allPlants);
                } else {
                    searchPlants(query);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        adapter.setOnPlantClickListener(new PlantAdapter.OnPlantClickListener() {
            @Override public void onPlantClick(Plant plant) { openDetail(plant); }
            @Override public void onAddToCartClick(Plant plant) { addToCart(plant); }
        });

        loadPlants();
    }

    // API #2 — Load all plants
    private void loadPlants() {
        ApiClient.getService().getAllPlants(API_KEY, 1)
                .enqueue(new Callback<PlantResponse>() {
                    @Override
                    public void onResponse(Call<PlantResponse> call,
                                           Response<PlantResponse> response) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {
                            allPlants = response.body().getData();
                            adapter.updateData(allPlants);
                        } else {
                            loadFakePlants();
                        }
                    }

                    @Override
                    public void onFailure(Call<PlantResponse> call, Throwable t) {
                        loadFakePlants();
                    }
                });
    }

    // API #3 — Search plants by name
    private void searchPlants(String query) {
        ApiClient.getService().searchPlants(API_KEY, query)
                .enqueue(new Callback<PlantResponse>() {
                    @Override
                    public void onResponse(Call<PlantResponse> call,
                                           Response<PlantResponse> response) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {
                            adapter.updateData(response.body().getData());
                        } else {
                            filterLocal(query);
                        }
                    }

                    @Override
                    public void onFailure(Call<PlantResponse> call, Throwable t) {
                        filterLocal(query);
                    }
                });
    }

    // Local filter fallback
    private void filterLocal(String query) {
        List<Plant> filtered = new ArrayList<>();
        for (Plant p : allPlants) {
            if (p.getCommonName() != null &&
                    p.getCommonName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(p);
            }
        }
        adapter.updateData(filtered);
    }

    // Fake data when no internet
    private void loadFakePlants() {
        List<Plant> fakePlants = new ArrayList<>();
        fakePlants.add(createFakePlant(1, "Monstera Deliciosa", "Perennial", "Average",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Monstera_deliciosa3.jpg/800px-Monstera_deliciosa3.jpg"));
        fakePlants.add(createFakePlant(2, "Snake Plant", "Perennial", "Minimum",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Sansevieria_trifasciata_%27Laurentii%27.jpg/800px-Sansevieria_trifasciata_%27Laurentii%27.jpg"));
        fakePlants.add(createFakePlant(3, "Peace Lily", "Perennial", "Average",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Spathiphyllum_cochlearispathum_RTBG.jpg/800px-Spathiphyllum_cochlearispathum_RTBG.jpg"));
        fakePlants.add(createFakePlant(4, "Pothos", "Perennial", "Average",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Epipremnum_aureum_31082012.jpg/800px-Epipremnum_aureum_31082012.jpg"));
        fakePlants.add(createFakePlant(5, "Fiddle Leaf Fig", "Perennial", "Average",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Ficus_lyrata.jpg/800px-Ficus_lyrata.jpg"));
        fakePlants.add(createFakePlant(6, "Aloe Vera", "Perennial", "Minimum",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/Aloe_vera_flower_inset.png/800px-Aloe_vera_flower_inset.png"));
        fakePlants.add(createFakePlant(7, "ZZ Plant", "Perennial", "Minimum",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/Zamioculcas_zamiifolia.jpg/800px-Zamioculcas_zamiifolia.jpg"));
        fakePlants.add(createFakePlant(8, "Rubber Plant", "Perennial", "Average",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Ficus_elastica_for_the_web.JPG/800px-Ficus_elastica_for_the_web.JPG"));

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
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra("product_id",     plant.getId());
        intent.putExtra("product_title",  plant.getCommonName());
        intent.putExtra("product_price",  12.00);
        intent.putExtra("product_image",  plant.getImageUrl());
        intent.putExtra("product_desc",
                "Watering: " + plant.getWatering()
                        + "\nSunlight: " + plant.getSunlight()
                        + "\nCycle: " + plant.getCycle());
        intent.putExtra("product_cat",    plant.getCycle());
        intent.putExtra("product_rating", 4.0f);
        intent.putExtra("product_reviews", 50);
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