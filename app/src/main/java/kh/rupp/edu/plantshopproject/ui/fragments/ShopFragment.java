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
import kh.rupp.edu.plantshopproject.adapter.ProductAdapter;
import kh.rupp.edu.plantshopproject.api.ApiClient;
import kh.rupp.edu.plantshopproject.db.AppDatabase;
import kh.rupp.edu.plantshopproject.db.CartItem;
import kh.rupp.edu.plantshopproject.model.Product;
import kh.rupp.edu.plantshopproject.ui.ProductDetailActivity;import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProducts = view.findViewById(R.id.rv_products);
        adapter    = new ProductAdapter(requireContext(), new ArrayList<>());
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(adapter);

        // Search filter
        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterProducts(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        adapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override public void onProductClick(Product product) { openDetail(product); }
            @Override public void onAddToCartClick(Product product) { addToCart(product); }
        });

        loadProducts();
    }

    private void loadProducts() {
        ApiClient.getService().getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    adapter.updateData(allProducts);
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                p.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(p);
            }
        }
        adapter.updateData(filtered);
    }

    private void openDetail(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
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
                existing.quantity++;
                db.cartDao().update(existing);
            } else {
                db.cartDao().insert(new CartItem(
                        product.getId(), product.getTitle(), product.getPrice(),
                        product.getImage(), product.getCategory(), 1));
            }
            if (getActivity() != null)
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show());
        }).start();
    }
}
