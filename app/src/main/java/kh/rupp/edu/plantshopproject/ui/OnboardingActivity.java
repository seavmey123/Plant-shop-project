package kh.rupp.edu.plantshopproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;
import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.adapter.OnboardingAdapter;
import kh.rupp.edu.plantshopproject.model.OnboardingItem;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext;
    private LinearLayout llDots;
    private List<OnboardingItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewpager_onboarding);
        btnNext   = findViewById(R.id.btn_next);
        llDots    = findViewById(R.id.ll_dots);

        items = new ArrayList<>();
        items.add(new OnboardingItem(
                R.drawable.ic_launcher_foreground,
                "Welcome to PlanShop",
                "Discover thousands of products right at your fingertips"));
        items.add(new OnboardingItem(
                R.drawable.ic_launcher_foreground,
                "Find Things Easily",
                "Browse by category and find exactly what you need"));
        items.add(new OnboardingItem(
                R.drawable.ic_launcher_foreground,
                "Fast Delivery",
                "Get your orders delivered fast anywhere, anytime"));

        viewPager.setAdapter(new OnboardingAdapter(items));
        setupDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setupDots(position);
                btnNext.setText(position == items.size() - 1 ? "Get Started" : "Next");
            }
        });

        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < items.size() - 1) {
                viewPager.setCurrentItem(current + 1);
            } else {
                startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setupDots(int activeIndex) {
        llDots.removeAllViews();
        for (int i = 0; i < items.size(); i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    i == activeIndex ? dpToPx(24) : dpToPx(8), dpToPx(8)
            );
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(
                    i == activeIndex
                            ? R.drawable.bg_dot_active
                            : R.drawable.bg_dot_inactive
            );
            llDots.addView(dot);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
