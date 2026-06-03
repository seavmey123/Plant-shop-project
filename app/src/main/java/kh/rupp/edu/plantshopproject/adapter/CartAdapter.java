package kh.rupp.edu.plantshopproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.db.CartItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartVH> {

    public interface OnRemoveClickListener {
        void onRemove(CartItem item);
    }

    private final Context context;
    private List<CartItem> items;
    private final OnRemoveClickListener removeListener;

    public CartAdapter(Context context, List<CartItem> items,
                       OnRemoveClickListener removeListener) {
        this.context        = context;
        this.items          = items;
        this.removeListener = removeListener;
    }

    public void updateData(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_cart, parent, false);
        return new CartVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartVH holder, int position) {
        CartItem item = items.get(position);

        holder.tvName.setText(item.title);
        holder.tvPrice.setText(String.format("$%.2f", item.price));
        holder.tvQty.setText("Qty: " + item.quantity);
        holder.tvSubtotal.setText(String.format("$%.2f", item.price * item.quantity));

        Glide.with(context)
                .load(item.image)
                .placeholder(R.drawable.bg_product_img)
                .into(holder.ivProduct);

        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null) removeListener.onRemove(item);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class CartVH extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        LinearLayout btnRemove;
        TextView tvName, tvPrice, tvQty, tvSubtotal;

        CartVH(@NonNull View itemView) {
            super(itemView);
            ivProduct  = itemView.findViewById(R.id.iv_product);
            btnRemove  = itemView.findViewById(R.id.btn_remove);
            tvName     = itemView.findViewById(R.id.tv_name);
            tvPrice    = itemView.findViewById(R.id.tv_price);
            tvQty      = itemView.findViewById(R.id.tv_qty);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
        }
    }
}
