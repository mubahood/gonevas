package gonevas.com.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import static gonevas.com.StartActivity.USERS_TABLE;

@Entity(tableName = USERS_TABLE)
public class UserModel {

    @NonNull
    @PrimaryKey
    public String id = "";


    public String date_created = "";
    public String user_id = "";
    public String phone = "";
    public String email = "";
    public String first_name = "";
    public String last_name = "";
    public String password = "";
    public String role = "";
    public String username = "";


    @Ignore
    public BillingModel billing = new BillingModel();

    @Ignore
    public ShippingModel shipping = new ShippingModel();

    public String billing_json = "";
    public String shipping_json = "";

    public String avatar_url = "";
    public Boolean is_paying_customer = false;
    public boolean phone_verified = false;

    public void init() {
        Gson gson = new Gson();
        this.billing_json = gson.toJson(this.billing);
        this.shipping_json = gson.toJson(this.shipping);
    }

    public void prepare_objects() {
        Gson gson = new Gson();
        this.billing = gson.fromJson(this.billing_json, BillingModel.class);
        this.shipping = gson.fromJson(this.shipping_json, ShippingModel.class);
    }
}
