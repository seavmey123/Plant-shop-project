package kh.rupp.edu.plantshopproject.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart")
public class CartItem implements kh.rupp.edu.plantshopproject.adapter.CartItem {

    @PrimaryKey
    public int productId;
    public String title;
    public double price;
    public String image;
    public String category;
    public int quantity;

    public CartItem(int productId, String title, double price,
                    String image, String category, int quantity) {
        this.productId = productId;
        this.title     = title;
        this.price     = price;
        this.image     = image;
        this.category  = category;
        this.quantity  = quantity;
    }
}
