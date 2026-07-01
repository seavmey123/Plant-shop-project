package kh.rupp.edu.plantshopproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kh.rupp.edu.plantshopproject.R;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerVH> {

    public static class BannerItem {

        public String title;
        public String subtitle;
        public int imageRes;

        public BannerItem(String title,
                          String subtitle,
                          int imageRes) {
            this.title = title;
            this.subtitle = subtitle;
            this.imageRes = imageRes;
        }
    }

    private final List<BannerItem> items;

    public BannerAdapter(List<BannerItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public BannerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerVH holder, int position) {

        BannerItem item = items.get(position);

        holder.tvTitle.setText(item.title);
        holder.tvSub.setText(item.subtitle);

        // Display local drawable image
        holder.ivBanner.setImageResource(item.imageRes);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BannerVH extends RecyclerView.ViewHolder {

        ImageView ivBanner;
        TextView tvTitle;
        TextView tvSub;

        BannerVH(@NonNull View itemView) {
            super(itemView);

            ivBanner = itemView.findViewById(R.id.iv_banner);
            tvTitle = itemView.findViewById(R.id.tv_banner_title);
            tvSub = itemView.findViewById(R.id.tv_banner_sub);
        }
    }
}