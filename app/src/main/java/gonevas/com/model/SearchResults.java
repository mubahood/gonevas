package gonevas.com.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static gonevas.com.StartActivity.SEARCH_TABLE;

@Entity(tableName = SEARCH_TABLE)
public class SearchResults {
    @NonNull
    @PrimaryKey
    public String search_id = "";
    public String keyword = "";
    public String search_time = "";
    public String search_by = "";
    public int results_num = 0;
}
