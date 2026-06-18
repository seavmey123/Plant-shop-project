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
import kh.rupp.edu.plantshopproject.model.Plant;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantVH> {

    public interface OnPlantClickListener {
        void onPlantClick(Plant plant);
        void onAddToCartClick(Plant plant);
    }

    private final Context context;
    private List<Plant> plants;
    private OnPlantClickListener listener;

    public PlantAdapter(Context context, List<Plant> plants) {
        this.context = context;
        this.plants  = plants;
    }

    public void setOnPlantClickListener(OnPlantClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Plant> newList) {
        this.plants = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_product_card, parent, false);
        return new PlantVH(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PlantVH holder, int position) {
        Plant plant = plants.get(position);

        holder.tvName.setText(plant.getCommonName());
        holder.tvCategory.setText(plant.getCycle() != null ? plant.getCycle() : "Plant");
        holder.tvPrice.setText("$" + (10 + (position * 3)));
        holder.tvUnit.setText("1 pot");

        // ── CHOOSE THE CORRECT LIVE IMAGE URL ──
        String finalImageUrl = null;

        // 1. First check if our live MAMP nested object exists
        if (plant.getDefaultImage() != null && plant.getDefaultImage().getMediumUrl() != null && !plant.getDefaultImage().getMediumUrl().isEmpty()) {
            finalImageUrl = plant.getDefaultImage().getMediumUrl();
        }
        // 2. Otherwise, fall back to the direct string property field
        else {
            finalImageUrl = plant.getImageUrl();
        }

        // Load it into Glide
        Glide.with(context)
                .load(finalImageUrl)
                .placeholder(R.drawable.bg_product_img)
                .error(R.drawable.bg_product_img)
                .into(holder.ivProduct);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPlantClick(plant);
        });

        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCartClick(plant);
        });
    }

    @Override
    public int getItemCount() {
        return plants != null ? plants.size() : 0;
    }

    static class PlantVH extends RecyclerView.ViewHolder {
        ImageView ivProduct, btnAdd;
        TextView tvName, tvCategory, tvPrice, tvUnit;

        PlantVH(@NonNull View itemView) {
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