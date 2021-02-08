package gonevas.com.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static gonevas.com.StartActivity.CART_TABLE;

@Entity(tableName = CART_TABLE)
public class CartModel {

    @NonNull
    @PrimaryKey
    public String product_id = "";

    public int quantity = 0;
    public boolean is_variable = false;
    public String selected_variable_id = "";
    public String variable_text = "";

    @Ignore
    public Product product = new Product();
}
