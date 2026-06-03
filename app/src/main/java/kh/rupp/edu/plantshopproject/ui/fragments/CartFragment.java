package kh.rupp.edu.plantshopproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.adapter.CartAdapter;
import kh.rupp.edu.plantshopproject.db.AppDatabase;
import kh.rupp.edu.plantshopproject.db.CartItem;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private CartAdapter cartAdapter;
    private TextView tvTotal, tvEmpty, tvGrandTotal, tvItemCount;
    private Button btnCheckout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCart       = view.findViewById(R.id.rv_cart);
        tvTotal      = view.findViewById(R.id.tv_total);
        tvGrandTotal = view.findViewById(R.id.tv_grand_total);
        tvEmpty      = view.findViewById(R.id.tv_empty);
        tvItemCount  = view.findViewById(R.id.tv_item_count);
        btnCheckout  = view.findViewById(R.id.btn_checkout);

        // Setup adapter
        cartAdapter = new CartAdapter(
                requireContext(),
                new ArrayList<>(),
                item -> removeFromCart(item)
        );

        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(cartAdapter);

        // Observe cart items from Room DB
        AppDatabase db = AppDatabase.getInstance(requireContext());

        db.cartDao().getAllItems().observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvCart.setVisibility(View.GONE);
                tvTotal.setText("$0.00");
                tvGrandTotal.setText("$0.00");
                tvItemCount.setText("0 items");
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvCart.setVisibility(View.VISIBLE);
                cartAdapter.updateData(items);
                tvItemCount.setText(items.size() + " items");
            }
        });

        // Observe total price
        db.cartDao().getTotalPrice().observe(getViewLifecycleOwner(), total -> {
            double t = total != null ? total : 0.0;
            tvTotal.setText(String.format("$%.2f", t));
            tvGrandTotal.setText(String.format("$%.2f", t));
        });

        // Checkout
        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(requireContext(),
                    "✅ Order placed! Thank you.", Toast.LENGTH_LONG).show();

            // Clear cart after order
            new Thread(() -> {
                AppDatabase.getInstance(requireContext()).cartDao().clearCart();
            }).start();
        });
    }

    private void removeFromCart(CartItem item) {
        new Thread(() -> {
            AppDatabase.getInstance(requireContext()).cartDao().delete(item);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(),
                                "Removed from cart", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
