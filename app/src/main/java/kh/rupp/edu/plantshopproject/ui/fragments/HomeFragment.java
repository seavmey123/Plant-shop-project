package kh.rupp.edu.plantshopproject.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import kh.rupp.edu.plantshopproject.adapter.FlashSaleAdapter;
import kh.rupp.edu.plantshopproject.adapter.ProductAdapter;
import kh.rupp.edu.plantshopproject.api.ApiClient;
import kh.rupp.edu.plantshopproject.db.AppDatabase;
import kh.rupp.edu.plantshopproject.db.CartItem;
import kh.rupp.edu.plantshopproject.model.Product;
import kh.rupp.edu.plantshopproject.ui.ProductDetailActivity;import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvProducts, rvFlashSales;
    private ProductAdapter productAdapter;
    private FlashSaleAdapter flashSaleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProducts   = view.findViewById(R.id.rv_products);
        rvFlashSales = view.findViewById(R.id.rv_flash_sales);

        // Setup products grid
        productAdapter = new ProductAdapter(requireContext(), new ArrayList<>());
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(productAdapter);

        // Setup flash sales horizontal
        flashSaleAdapter = new FlashSaleAdapter(requireContext(), new ArrayList<>());
        rvFlashSales.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFlashSales.setAdapter(flashSaleAdapter);

        // Click listeners
        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                openDetail(product);
            }
            @Override
            public void onAddToCartClick(Product product) {
                addToCart(product);
            }
        });

        flashSaleAdapter.setOnItemClickListener(new FlashSaleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                openDetail(product);
            }
            @Override
            public void onAddToCartClick(Product product) {
                addToCart(product);
            }
        });

        loadProducts();
    }

    private void loadProducts() {
        ApiClient.getService().getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> all = response.body();
                    productAdapter.updateData(all);
                    flashSaleAdapter.updateData(all);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDetail(Product product) {
        Intent intent = new Intent(requireContext(), kh.rupp.edu.plantshopproject.ui.fragments.ProductDetailActivity.class);
        intent.putExtra("product_id",    product.getId());
        intent.putExtra("product_title", product.getTitle());
        intent.putExtra("product_price", product.getPrice());
        intent.putExtra("product_image", product.getImage());
        intent.putExtra("product_desc",  product.getDescription());
        intent.putExtra("product_cat",   product.getCategory());
        startActivity(intent);
    }

    private void addToCart(Product product) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            CartItem existing = db.cartDao().getItemById(product.getId());

            if (existing != null) {
                existing.quantity += 1;
                db.cartDao().update(existing);
            } else {
                CartItem item = new CartItem(
                        product.getId(),
                        product.getTitle(),
                        product.getPrice(),
                        product.getImage(),
                        product.getCategory(),
                        1
                );
                db.cartDao().insert(item);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(),
                                product.getTitle() + " added to cart",
                                Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
