package gonevas.com.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import gonevas.com.model.Ad;
import gonevas.com.model.CartModel;
import gonevas.com.model.MySettings;
import gonevas.com.model.Order;
import gonevas.com.model.PhoneNumberModel;
import gonevas.com.model.Product;
import gonevas.com.model.ProductCategory;
import gonevas.com.model.SearchResults;
import gonevas.com.model.UserModel;

import static gonevas.com.StartActivity.ADS_TABLE;
import static gonevas.com.StartActivity.CART_TABLE;
import static gonevas.com.StartActivity.CATEGORY_TABLE;
import static gonevas.com.StartActivity.ORDER_TABLE;
import static gonevas.com.StartActivity.PHONE_NUMBER_TABLE;
import static gonevas.com.StartActivity.PRODUCT_TABLE;
import static gonevas.com.StartActivity.SEARCH_TABLE;
import static gonevas.com.StartActivity.SETTINGS_TABLE;
import static gonevas.com.StartActivity.USERS_TABLE;

@Dao
public interface DbInterface {


    @Insert
    void save_orders(List<Order> orders);

    @Insert
    void save_product(Product product);

    @Insert
    void save_categories(List<ProductCategory> categories);

    @Insert
    void save_search_list(List<SearchResults> searchResults);

    @Insert
    void save_ads(List<Ad> ads);

    @Insert
    void save_settings(MySettings mySettings);

    @Update
    void update_product(Product product);

    @Delete
    void delete_product(Product product);

    @Query("DELETE FROM " + ORDER_TABLE)
    void clear_orders();

    @Insert
    void save_phone_number(PhoneNumberModel phoneNumberModel);

    @Insert
    void add_to_cart(CartModel cartModel);


    @Query("DELETE FROM " + PHONE_NUMBER_TABLE + " WHERE 1")
    void delete_all_phone_numbers();

    @Query("DELETE FROM " + PRODUCT_TABLE)
    void delete_all_products();


    @Query("DELETE FROM " + USERS_TABLE)
    void delete_all_user();


    @Query("SELECT * FROM " + PRODUCT_TABLE + " ORDER BY id DESC")
    LiveData<List<Product>> get_products();


    @Query("SELECT * FROM " + ADS_TABLE + " ORDER BY ad_id DESC")
    LiveData<List<Ad>> get_ads();

    @Query("SELECT * FROM " + CART_TABLE)
    LiveData<List<CartModel>> get_cart();

    @Query("SELECT * FROM " + PHONE_NUMBER_TABLE + " WHERE id = '1'")
    LiveData<PhoneNumberModel> get_phone_number();

    @Query("SELECT * FROM " + SETTINGS_TABLE + " WHERE settings_id = '1'")
    LiveData<MySettings> get_settings();


    @Update
    void update_cart(CartModel cartModel);


    @Query("DELETE FROM " + CART_TABLE)
    void clear_cart();


    @Query("DELETE FROM " + CART_TABLE + " WHERE product_id = :product_id")
    void remove_item_from_cart(String product_id);

    @Query("DELETE FROM " + CART_TABLE)
    void empty_cart();

    @Query("SELECT * FROM " + USERS_TABLE)
    LiveData<List<UserModel>> get_users();

    @Insert
    void save_user(UserModel userModel);

    @Query("SELECT * FROM " + ORDER_TABLE + " ORDER BY id DESC ")
    LiveData<List<Order>> getOrders();

    @Query("SELECT * FROM " + ORDER_TABLE + " WHERE id  = :ordersId")
    LiveData<Order> getOrdersById(String ordersId);

    @Query("SELECT * FROM " + PRODUCT_TABLE + " WHERE id = :id")
    Product get_product(String id);

    @Query("SELECT * FROM " + CATEGORY_TABLE + " ORDER BY count DESC")
    LiveData<List<ProductCategory>> get_categories();

    @Insert
    void save_product_list(List<Product> products);

    @Query("DELETE FROM " + SEARCH_TABLE)
    void delete_all_search();

    @Query("DELETE FROM " + ADS_TABLE)
    void delete_all_ads();

    @Query("DELETE FROM " + SETTINGS_TABLE)
    void delete_Settings();

    @Query("DELETE FROM " + CATEGORY_TABLE)
    void delete_all_categories();


    @Query("SELECT * FROM " + SEARCH_TABLE + " ORDER BY search_id DESC")
    LiveData<List<SearchResults>> get_search_results();


    @Query("SELECT * FROM " + PRODUCT_TABLE + " WHERE id = :id")
    LiveData<Product> get_product_by_id(String id);


}
