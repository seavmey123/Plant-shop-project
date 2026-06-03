package kh.rupp.edu.plantshopproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductVH> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    private final Context context;
    private List<Product> products;
    private OnProductClickListener listener;

    public ProductAdapter(Context context, List<Product> products) {
        this.context  = context;
        this.products = products;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Product> newList) {
        this.products = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_product_card, parent, false);
        return new ProductVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.getTitle());
        holder.tvCategory.setText(product.getCategory());
        holder.tvPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.tvUnit.setText("1 item");

        Glide.with(context)
                .load(product.getImage())
                .placeholder(R.drawable.bg_product_img)
                .into(holder.ivProduct);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });

        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCartClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class ProductVH extends RecyclerView.ViewHolder {
        ImageView ivProduct, btnAdd;
        TextView tvName, tvCategory, tvPrice, tvUnit;

        ProductVH(@NonNull View itemView) {
            super(itemView);
            ivProduct  = itemView.findViewById(R.id.iv_product);
            btnAdd     = itemView.findViewById(R.id.btn_add);
            tvName     = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvPrice    = itemView.findViewById(R.id.tv_price);
            tvUnit     = itemView.findViewById(R.id.tv_unit);
        }
    }
}
