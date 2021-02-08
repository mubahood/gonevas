package gonevas.com.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static gonevas.com.StartActivity.PHONE_NUMBER_TABLE;

@Entity(tableName = PHONE_NUMBER_TABLE)
public class PhoneNumberModel {

    @NonNull
    @PrimaryKey
    public String id = "";

    public String phone_number = "";
    public String country = "";
    public String verification_code = "";
    public String code_request_time = "";
    public String code_verification_time = "";
    public boolean is_verified = false;
}
