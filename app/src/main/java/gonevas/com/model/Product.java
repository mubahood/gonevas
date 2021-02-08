package gonevas.com.model;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gonevas.com.StartActivity.PRODUCT_TABLE;

@Entity(tableName = PRODUCT_TABLE)
public class Product {

    @NonNull
    @PrimaryKey
    public String id = "";

    public String product_id = "";
    public String name = "";
    public String slug = "";
    public String permalink = "";
    public String date_created = "";
    public String date_modified = "";
    public String type = "";
    public String status = "";
    public String featured = "";
    public String description = "";
    public String short_description = "";
    public String sku = "";
    public String price = "";
    public String regular_price = "";
    public String sale_price = "";
    public String price_html = "";
    public String on_sale = "";
    public String purchasable = "";
    public String total_sales = "";
    public String virtual = "";
    public String downloadable = "";
    public String external_url = "";
    public String stock_quantity = "";
    public String stock_status = "";
    public String weight = "";
    public String dimensions = "";
    public String shipping_required = "";
    public String reviews_allowed = "";
    public String average_rating = "";
    public String category_id = "";
    public String category_name = "";
    public String category_slug = "";
    public String width = "";
    public String length = "";
    public String height = "";
    public String image_id = "";
    public String purchase_note = "";
    public String rating_count = "";
    public String parent_id = "";
    public String attributes_json = "";

    public String cart_price = "";

    @Ignore
    public List<String> related_ids = new ArrayList<>();

    @Ignore
    public List<Variable> variations = new ArrayList<>();

    @Ignore
    private List<Integer> category_ids = new ArrayList<>();


    @Ignore
    private List<Integer> tag_ids = new ArrayList<>();

    @Ignore
    public List<Long> upsell_ids = new ArrayList<>();

    @Ignore
    public List<ProductCategory> categories = new ArrayList<>();

    public String categories_json = "";

    @Ignore
    public List<ProductTag> tags = new ArrayList<>();
    public String tags_json = "";
    public String image_json = "";
    public String images_json = "";

    @Ignore
    public Image image = new Image();

    @Ignore
    public List<ProductAttribute> default_attributes = new ArrayList<>();

    @Ignore
    public List<Image> images = new ArrayList<>();

    @Ignore
    public List<ProductAttribute> attributes = new ArrayList<>();

    public String default_attributes_json = "";
    public String image_thumbnail = "";
    public String image_full = "";
    public String variations_json = "[]";

    public void init_product() {
        if (this.name == null)
            name = "";

        if (this.slug == null)
            slug = "";

        if (this.permalink == null)
            permalink = "";

        if (this.date_created == null)
            date_created = "";

        if (this.date_modified == null)
            date_modified = "";

        if (cart_price == null) {
            cart_price = this.sale_price;
        }
        if (cart_price.length() < 1) {
            cart_price = this.sale_price;
        }

        if (cart_price.length() < 1) {
            cart_price = this.price;
        }
        if (cart_price.length() < 1) {
            cart_price = this.regular_price;
        }

        if (cart_price.length() < 1) {
            cart_price = "1";
        }


        if (this.type == null) {
            type = "simple";
        } else {
            type = this.type;
            if (this.type.equals("variable")) {
                try {
                    Log.d(TAG, "init_product: variations_json ===> " + this.variations_json);
                    this.variations = Arrays.asList(new Gson().fromJson(this.variations_json, Variable[].class));
                    Log.d(TAG, "SUUCESS: variations_json ===> " + variations.size());
                } catch (Exception e) {
                    this.variations = new ArrayList<>();
                    Log.d(TAG, "FAILED: variations_json ===> " + e.getCause());

                }
            }
        }


        if (this.on_sale == null)
            on_sale = "0";

        if (this.on_sale == null)
            on_sale = "0";

        if (this.purchasable == null)
            purchasable = "0";

        if (this.purchasable == null)
            purchasable = "0";

        if (this.total_sales == null)
            total_sales = "0";

        if (this.total_sales == null)
            total_sales = "0";

        if (this.virtual == null)
            virtual = "0";

        if (this.downloadable == null)
            downloadable = "0";

        if (this.external_url == null)
            external_url = "0";

        if (this.stock_quantity == null)
            stock_quantity = "0";

        if (this.stock_status == null)
            stock_status = "";

        if (this.weight == null)
            weight = "0";

        if (this.dimensions == null)
            dimensions = "0";

        if (this.shipping_required == null)
            shipping_required = "0";

        if (this.reviews_allowed == null)
            reviews_allowed = "0";

        if (this.average_rating == null)
            average_rating = "0";

        if (this.category_id == null)
            category_id = "0";

        if (this.category_name == null)
            category_name = "0";

        if (this.category_slug == null)
            category_slug = "0";

        if (this.width == null)
            width = "0";

        if (this.length == null)
            length = "0";

        if (this.height == null)
            height = "0";

        if (this.image_id == null)
            image_id = "";

        if (this.purchase_note == null)
            purchase_note = "";

        if (this.rating_count == null)
            rating_count = "";

        if (this.parent_id == null)
            parent_id = "";

        if (this.rating_count == null)
            rating_count = "";

        if (this.related_ids == null)
            related_ids = new ArrayList<>();

        if (this.category_ids == null)
            category_ids = new ArrayList<>();

        if (this.tag_ids == null)
            tag_ids = new ArrayList<>();

        if (this.upsell_ids == null)
            upsell_ids = new ArrayList<>();

        if (this.categories == null)
            categories = new ArrayList<>();

        if (this.tags == null)
            tags = new ArrayList<>();

        if (this.tags_json == null)
            tags_json = "";

        if (this.image == null)
            image = new Image();

        if (this.image_json == null)
            image_json = "{}";

        if (this.images_json == null)
            images_json = "[]";

        if (this.default_attributes == null)
            default_attributes = new ArrayList<>();

        if (this.images == null)
            images = new ArrayList<>();

        if (this.attributes == null)
            attributes = new ArrayList<>();

        if (this.default_attributes_json == null)
            default_attributes_json = "[]";

        if (this.variations_json == null)
            variations_json = "[]";

        if (this.image_thumbnail == null)
            image_thumbnail = "[]";

        if (this.image_full == null)
            image_full = "[]";


        Log.d(TAG, "init_product: INITING PRO ==> " + this.attributes_json.length());

        if (this.categories_json.length() > 3) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            try {
                this.categories = Arrays.asList(gson.fromJson(this.categories_json, ProductCategory[].class));
                Log.d(TAG, "SUUCESSSSSS: tags_json ===> " + this.categories.size());
            } catch (Exception e) {
                Log.d(TAG, " SUUCESSSSSS tags_json FAILED BECAUSE " + e.getMessage());
            }
        } else {
            Log.d(TAG, ": attributes_json TOO SHOR IMAGE TEXT  ==> " + images_json);
        }

        if (this.tags_json.length() > 3) {
            try {
                this.tags = Arrays.asList(new Gson().fromJson(this.tags_json, ProductTag[].class));
                Log.d(TAG, "SUUCESS: tags_json ===> " + this.tags.size());
            } catch (Exception e) {
                Log.d(TAG, " tags_json FAILED BECAUSE " + e.getMessage());
            }
        } else {
            Log.d(TAG, ": attributes_json TOO SHOR IMAGE TEXT  ==> " + images_json);
        }

        if (this.attributes_json.length() > 3) {
            try {
                this.attributes = Arrays.asList(new Gson().fromJson(this.attributes_json, ProductAttribute[].class));
                Log.d(TAG, "SUUCESS: attributes_json ===> " + images.size());
            } catch (Exception e) {
                Log.d(TAG, " attributes_json FAILED BECAUSE " + e.getMessage());
            }
        } else {
            Log.d(TAG, ": attributes_json TOO SHOR IMAGE TEXT  ==> " + images_json);
        }


        if (this.images_json.length() > 3) {
            try {
                this.images = Arrays.asList(new Gson().fromJson(this.images_json, Image[].class));
                Log.d(TAG, "SUUCESS: ===> " + images.size());
            } catch (Exception e) {
                Log.d(TAG, "FAILED BECAUSE " + e.getMessage());
            }
        } else {
            Log.d(TAG, ": TOO SHOR IMAGE TEXT  ==> " + images_json);
        }

    }

    private static final String TAG = "ProductMubahood";
}