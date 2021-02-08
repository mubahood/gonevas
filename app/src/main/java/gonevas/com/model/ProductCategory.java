package gonevas.com.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static gonevas.com.StartActivity.CATEGORY_TABLE;

@Entity(tableName = CATEGORY_TABLE)
public class ProductCategory {
    @NonNull
    @PrimaryKey
    public Integer id = 1;

    public String name = "";
    public String slug = "";
    public String description = "";
    public Integer count = 0;
    public String photo = "";

    public ProductCategory() {

    }
}
