package kh.rupp.edu.plantshopproject.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.adapter.BannerAdapter;
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

    // Fix Problem 2: Class-level variables for safe lifecycle management
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;

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

        // Products Grid
        productAdapter = new PlantAdapter(requireContext(), new ArrayList<>());
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(productAdapter);

        // Flash Sales
        flashSaleAdapter = new PlantAdapter(requireContext(), new ArrayList<>());
        rvFlashSales.setLayoutManager(
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));
        rvFlashSales.setAdapter(flashSaleAdapter);

        // Product Click
        productAdapter.setOnPlantClickListener(new PlantAdapter.OnPlantClickListener() {
            @Override
            public void onPlantClick(Plant plant) {
                openDetail(plant);
            }

            @Override
            public void onAddToCartClick(Plant plant) {
                addToCart(plant);
            }
        });

        // Flash Sale Click
        flashSaleAdapter.setOnPlantClickListener(new PlantAdapter.OnPlantClickListener() {
            @Override
            public void onPlantClick(Plant plant) {
                openDetail(plant);
            }

            @Override
            public void onAddToCartClick(Plant plant) {
                addToCart(plant);
            }
        });

        // Banner
        setupBanner(view);

        // Load Plants
        loadPlants();
    }

    private void loadPlants() {
        ApiClient.getService().getPlants().enqueue(new Callback<List<Plant>>() {
            @Override
            public void onResponse(@NonNull Call<List<Plant>> call,
                                   @NonNull Response<List<Plant>> response) {
                if (!isAdded()) return; // Extra safety guard for network callbacks

                if (response.isSuccessful() && response.body() != null) {
                    List<Plant> serverPlants = response.body();
                    productAdapter.updateData(serverPlants);
                    flashSaleAdapter.updateData(serverPlants);
                } else {
                    Log.e("API_FAIL", "Response code: " + response.code());
                    loadFakePlants();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Plant>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Log.e("API_FAIL", t.getMessage() != null ? t.getMessage() : "Unknown error");
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
        intent.putExtra("product_id", plant.getId());
        intent.putExtra("product_title", plant.getCommonName());
        intent.putExtra("product_price", 12.00);
        intent.putExtra("product_image", plant.getImageUrl());
        intent.putExtra("product_desc",
                "Watering: " + plant.getWatering()
                        + "\nSunlight: " + plant.getSunlight()
                        + "\nCycle: " + plant.getCycle());

        intent.putExtra("product_cat", plant.getCycle());
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
                getActivity().runOnUiThread(() -> {
                    if (isAdded()) {
                        Toast.makeText(
                                requireContext(),
                                plant.getCommonName() + " added to cart 🌿",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        }).start();
    }

    // ==========================
    // Banner
    // ==========================

    private void setupBanner(View view) {
        ViewPager2 vpBanner = view.findViewById(R.id.vp_banner);

        List<BannerAdapter.BannerItem> banners = Arrays.asList(
                new BannerAdapter.BannerItem(
                        "Up to 20% Offer Sale",
                        "Enjoy PlanShop special offer",
                        R.drawable.banner_1
                ),
                new BannerAdapter.BannerItem(
                        "New Arrivals 🌿",
                        "Fresh plants just arrived — shop now!",
                        R.drawable.banner_2
                ),
                new BannerAdapter.BannerItem(
                        "Free Delivery",
                        "Free delivery on orders above $50",
                        R.drawable.banner_4
                )
        );

        BannerAdapter bannerAdapter = new BannerAdapter(banners);
        vpBanner.setAdapter(bannerAdapter);

        // Fix Problem 2 & 4: Initializing global Runnable with context attachment checks
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || getContext() == null) {
                    return;
                }

                int next = (vpBanner.getCurrentItem() + 1) % banners.size();
                vpBanner.setCurrentItem(next, true);

                bannerHandler.postDelayed(this, 3000);
            }
        };

        bannerHandler.postDelayed(bannerRunnable, 3000);

        LinearLayout llDots = view.findViewById(R.id.ll_banner_dots);
        setupBannerDots(llDots, 0, banners.size());

        vpBanner.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        setupBannerDots(llDots, position, banners.size());
                    }
                });
    }

    // Fix Problem 1: Safe context resolution and fragment attachment checks
    private void setupBannerDots(LinearLayout llDots, int activeIndex, int total) {
        if (!isAdded() || getContext() == null) {
            return;
        }

        llDots.removeAllViews();

        for (int i = 0; i < total; i++) {
            android.widget.ImageView dot = new android.widget.ImageView(getContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    i == activeIndex ? dpToPx(20) : dpToPx(8),
                    dpToPx(8));

            params.setMargins(dpToPx(3), 0, dpToPx(3), 0);
            dot.setLayoutParams(params);

            dot.setBackgroundResource(
                    i == activeIndex
                            ? R.drawable.bg_dot_active
                            : R.drawable.bg_dot_inactive);

            llDots.addView(dot);
        }
    }

    private int dpToPx(int dp) {
        if (!isAdded() || getContext() == null) return 0;
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    // Fix Problem 3: Kill the loop cleanly to eliminate the memory leak completely
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }
}