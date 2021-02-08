package gonevas.com.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static gonevas.com.StartActivity.SETTINGS_TABLE;

@Entity(tableName = SETTINGS_TABLE)
public class MySettings {

    @NonNull
    @PrimaryKey
    public String settings_id = "1";

    public String last_load = "0";
    public String app_version = "1";
    public String user_id = "0";
    public String other_data = "";
    public String install_date = "";

}
