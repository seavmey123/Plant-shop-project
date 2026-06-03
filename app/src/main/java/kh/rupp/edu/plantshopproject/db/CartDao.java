package kh.rupp.edu.plantshopproject.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CartItem item);

    @Update
    void update(CartItem item);

    @Delete
    void delete(CartItem item);

    @Query("SELECT * FROM cart")
    LiveData<List<CartItem>> getAllItems();

    @Query("SELECT * FROM cart WHERE productId = :id LIMIT 1")
    CartItem getItemById(int id);

    @Query("SELECT SUM(price * quantity) FROM cart")
    LiveData<Double> getTotalPrice();

    @Query("SELECT SUM(quantity) FROM cart")
    LiveData<Integer> getTotalCount();

    @Query("DELETE FROM cart")
    void clearCart();
}
