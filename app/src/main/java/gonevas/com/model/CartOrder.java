package gonevas.com.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CartOrder {

    public String id = "";

    public String order_id = "";
    public String parent_id = "";
    public String number = "";
    public String order_key = "";
    public String created_via = "";
    public String version = "";
    public String date_modified_gmt = "";
    public String currency = "";
    public String date_created = "";
    public String date_modified = "";
    public String discount_total = "";
    public String total = "";
    public String discount_tax = "";
    public String shipping_tax = "";
    public String shipping_total = "";
    public String cart_tax = "";
    public String status = "";
    public String total_tax = "";
    public Boolean prices_include_tax = false;
    public String customer_id = "";
    public String customer_ip_address = "";
    public String customer_note = "";
    public String order_json = "";


    public BillingModel billing = new BillingModel();

    public ShippingModel shipping = new ShippingModel();

    public String payment_method = "";
    public String payment_method_title = "";
    public String transaction_id = "";
    public String date_paid = "";
    public String date_completed = "";
    public String time = "";
    public String cart_hash = "";
    public boolean set_paid = false;

    public List<CartOrderLineItems> line_items = new ArrayList<>();

    public void init() {
    }

    public String billing_json = "";
    public String shipping_json = "";
    public String line_items_json = "";


    public void to_json() {
        this.billing_json = new Gson().toJson(this.billing);
        this.shipping_json = new Gson().toJson(this.shipping);
        this.line_items_json = new Gson().toJson(this.line_items);
    }

    public void from_json() {
        this.billing = new Gson().fromJson(this.billing_json, BillingModel.class);
        this.shipping = new Gson().fromJson(this.shipping_json, ShippingModel.class);
    }


}
