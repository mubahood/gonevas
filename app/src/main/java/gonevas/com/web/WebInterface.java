package gonevas.com.web;


import java.util.List;
import java.util.Map;

import gonevas.com.model.MyResponse;
import gonevas.com.model.Product;
import gonevas.com.model.ProductCategory;
import gonevas.com.model.ResponseBasicData;
import gonevas.com.model.SearchResults;
import gonevas.com.model.UserModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface WebInterface {


    @GET("api1/products/categories")
    Call<List<ProductCategory>> get_categories();


    @GET("wp-json/mama/v1/basic")
    Call<ResponseBasicData> get_basic_data(
    );

    @GET("wp-json/mama/v1/products")
    Call<List<Product>> get_products(
            @QueryMap Map<String, String> options
    );

    @GET("wp-json/mama/v1/products")
    Call<List<Product>> get_products_by_search(
            @Query("s") String search
    );

    @GET("wp-json/mama/v1/search_keywords")
    Call<List<SearchResults>> get_search_keywords(
    );

    @GET("wp-json/mama/v1/products")
    Call<List<Product>> get_products_by_category(
            @Query("category") String category
    );


    @GET("wp-json/mama/v1/create_customer")
    Call<MyResponse> create_customer(
            @Query("data") String get_customer
    );

    @GET("wp-json/mama/v1/create_order")
    Call<MyResponse> create_order(
            @Query("data") String order_json
    );

    @GET("wp-json/mama/v1/customer_orders")
    Call<MyResponse> customer_orders(
            @Query("customer_id") String customer_id
    );

    @GET("wp-json/mama/v1/customers")
    Call<List<UserModel>> get_customer_by_phone(
            @Query("phone") String phone_number
    );


    @GET("api1/v1/orders")
    Call<MyResponse> get_orders(
            @Query("customer") String customer
    );


   /* @GET("api1")
    Call<ArrayList<OrderModel>> get_orders(
            @Query("get_orders") String get_customer,
            @Query("customer") String username_val
    );

    @GET("api1")
    Call<My_response> register_new_customer(
            @Query("register_customer") String register_customer,
            @Query("data") String data
    );

    @GET("api1")
    Call<My_response> submit_order(
            @Query("submit_order") String register_customer,
            @Query("data") String data
    );*/
}
