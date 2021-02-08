package gonevas.com.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static gonevas.com.StartActivity.ADS_TABLE;

@Entity(tableName = ADS_TABLE)
public class Ad {

    @NonNull
    @PrimaryKey
    public int ad_id = 0;

    public String section = "";
    public int position = 0;
    public String category = "";
    public String title = "";
    public String details = "";
    public String photo = "";


}
