package kh.rupp.edu.plantshopproject.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.db.AppDatabase;
import kh.rupp.edu.plantshopproject.db.CartItem;

public class ProductDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private int productId;
    private String productTitle, productImage, productCategory;
    private double productPrice;
    private TextView tvQuantity, tvTotalPrice, tvHeart;
    private boolean isWishlisted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Get data from intent
        productId       = getIntent().getIntExtra("product_id", 0);
        productTitle    = getIntent().getStringExtra("product_title");
        productPrice    = getIntent().getDoubleExtra("product_price", 0.0);
        productImage    = getIntent().getStringExtra("product_image");
        productCategory = getIntent().getStringExtra("product_cat");
        String desc     = getIntent().getStringExtra("product_desc");
        float rating    = getIntent().getFloatExtra("product_rating", 4.0f);
        int reviewCount = getIntent().getIntExtra("product_reviews", 0);

        // Bind views
        ImageView ivProduct    = findViewById(R.id.iv_product);
        TextView tvName        = findViewById(R.id.tv_name);
        TextView tvPrice       = findViewById(R.id.tv_price);
        TextView tvCategory    = findViewById(R.id.tv_category);
        TextView tvDescription = findViewById(R.id.tv_description);
        TextView tvRating      = findViewById(R.id.tv_rating);
        TextView tvReviewCount = findViewById(R.id.tv_review_count);
        RatingBar ratingBar    = findViewById(R.id.rating_bar);
        tvQuantity             = findViewById(R.id.tv_quantity);
        tvTotalPrice           = findViewById(R.id.tv_total_price);
        tvHeart                = findViewById(R.id.tv_heart);

        LinearLayout btnBack     = findViewById(R.id.btn_back);
        LinearLayout btnMinus    = findViewById(R.id.btn_minus);
        LinearLayout btnPlus     = findViewById(R.id.btn_plus);
        LinearLayout btnWishlist = findViewById(R.id.btn_wishlist);
        Button btnAddToCart      = findViewById(R.id.btn_add_to_cart);

        // Set data
        Glide.with(this).load(productImage).into(ivProduct);
        tvName.setText(productTitle);
        tvPrice.setText(String.format("$%.2f", productPrice));
        tvCategory.setText(productCategory != null
                ? productCategory.toUpperCase() : "");
        tvDescription.setText(desc);
        ratingBar.setRating(rating);
        tvRating.setText(String.format("%.1f", rating));
        tvReviewCount.setText("(" + reviewCount + " reviews)");
        updateTotal();

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Wishlist toggle
        btnWishlist.setOnClickListener(v -> {
            isWishlisted = !isWishlisted;
            tvHeart.setText(isWishlisted ? "♥" : "♡");
            Toast.makeText(this,
                    isWishlisted ? "Added to wishlist ♥" : "Removed from wishlist",
                    Toast.LENGTH_SHORT).show();
        });

        // Quantity minus
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotal();
            }
        });

        // Quantity plus
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateTotal();
        });

        // Add to cart
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void updateTotal() {
        if (tvTotalPrice != null) {
            tvTotalPrice.setText(String.format("$%.2f", productPrice * quantity));
        }
    }

    private void addToCart() {
        int qty = quantity;
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            CartItem existing = db.cartDao().getItemById(productId);
            if (existing != null) {
                existing.quantity += qty;
                db.cartDao().update(existing);
            } else {
                db.cartDao().insert(new CartItem(
                        productId, productTitle, productPrice,
                        productImage, productCategory, qty));
            }
            runOnUiThread(() -> {
                Toast.makeText(this,
                        qty + " item(s) added to cart! 🛒",
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
