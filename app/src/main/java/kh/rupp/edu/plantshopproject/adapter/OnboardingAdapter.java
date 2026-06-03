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
import kh.rupp.edu.plantshopproject.model.OnboardingItem;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingVH> {

    private final List<OnboardingItem> items;

    public OnboardingAdapter(List<OnboardingItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OnboardingVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingVH holder, int position) {
        OnboardingItem item = items.get(position);
        holder.image.setImageResource(item.imageRes);
        holder.title.setText(item.title);
        holder.subtitle.setText(item.subtitle);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class OnboardingVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, subtitle;

        OnboardingVH(@NonNull View itemView) {
            super(itemView);
            image    = itemView.findViewById(R.id.iv_illustration);
            title    = itemView.findViewById(R.id.tv_title);
            subtitle = itemView.findViewById(R.id.tv_subtitle);
        }
    }
}
