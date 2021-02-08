package gonevas.com.db;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

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

public class DatabaseRepository {
    private static final String TAG = "DatabaseRepository";
    public Database database;
    public DbInterface dbInterface;

    private LiveData<List<Ad>> ads;
    private LiveData<List<CartModel>> cart;
    private LiveData<List<UserModel>> users;
    private LiveData<List<Order>> orders;
    Context context;

    public DatabaseRepository(Context context) {
        this.context = context;
        database = Database.getGetInstance(context);
        dbInterface = database.dbInterface();
        cart = dbInterface.get_cart();
        users = dbInterface.get_users();
        orders = dbInterface.getOrders();
    }

    public void save_orders_list(final List<Order> orders) {

        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    dbInterface.clear_orders();
                    Log.d(TAG, "SUCCESSFULLY DELETED orders => " + orders.size());
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO DELETE BECAUSE: " + e.getMessage());
                }

                try {
                    dbInterface.clear_orders();
                    dbInterface.save_orders(orders);
                    Log.d(TAG, "SUCCESSFULLY SAVED orders => " + orders.size());
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO SAVE save_product_list BECAUSE: " + e.getMessage());
                }
            }
        });
    }


    public void delete_all_phone_numbers() {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dbInterface.delete_all_phone_numbers();
                    Log.d(TAG, "All phones deleted : ");
                } catch (Exception e) {
                    Log.d(TAG, "FAILED to delete all posts: " + e.getMessage());
                }
            }
        });
    }

    public void save_phone_number(final PhoneNumberModel phoneNumberModel) {

        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dbInterface.delete_all_phone_numbers();
                    Log.d(TAG, "All phones deleted : ");
                } catch (Exception e) {
                    Log.d(TAG, "FAILED to delete all posts: " + e.getMessage());
                }

                try {
                    dbInterface.save_phone_number(phoneNumberModel);
                    Log.d(TAG, "SUCCESSFULLY SAVED phoneNumberModel => ");
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO SAVE save_product_list BECAUSE: " + e.getMessage());
                }
            }
        });
    }


    public void save_user(final UserModel userModel) {

        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dbInterface.delete_all_user();
                    Log.d(TAG, "All phones deleted : ");
                } catch (Exception e) {
                    Log.d(TAG, "FAILED to delete all posts: " + e.getMessage());
                }

                try {
                    userModel.billing_json = gson.toJson(userModel.billing);
                    userModel.shipping_json = gson.toJson(userModel.shipping);
                    dbInterface.save_user(userModel);
                    Log.d(TAG, "SUCCESSFULLY SAVED phoneNumberModel => ");
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO SAVE save_product_list BECAUSE: " + e.getMessage());
                }
            }
        });
    }

    Gson gson = new Gson();

    public void update_to_cart(final CartModel cartModel) {


        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dbInterface.update_cart(cartModel);
                    Log.d(TAG, "SUCCESSFULLY UPDATED CART ===>  => " + cartModel.product_id);
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO UPDATED CART BECAUSE: " + e.getMessage());
                }
            }
        });


    }

    public void add_to_cart(final CartModel cartModel) {


        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dbInterface.add_to_cart(cartModel);
                    Log.d(TAG, "SUCCESSFULLY SAVED CART ===>  => " + cartModel.product_id);
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO SAVE save_product_list BECAUSE: " + e.getMessage());
                }
            }
        });

    }

    public void remove_item_from_cart(final CartModel cartModel) {


        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dbInterface.remove_item_from_cart(cartModel.product_id);
                    Log.d(TAG, "SUCCESSFULLY REMOVED CART ===>  => " + cartModel.product.name);
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO SAVE save_product_list BECAUSE: " + e.getMessage());
                }
            }
        });

    }


    public void empty_cart() {

        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    dbInterface.empty_cart();
                    Log.d(TAG, "SUCCESSFULLY EMPTYED CART ===>  => ");
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO EMPTYED CART BECAUSE: " + e.getMessage());
                }
            }
        });


    }

    public void clear_cart() {

        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    dbInterface.clear_cart();
                    Log.d(TAG, "SUCCESSFULLY EMPTYED CART ===>  => ");
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO EMPTYED CART BECAUSE: " + e.getMessage());
                }
            }
        });


    }

    public void delete_all_user() {

        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    dbInterface.delete_all_user();
                    Log.d(TAG, "SUCCESSFULLY EMPTYED CART ===>  => ");
                } catch (Exception e) {
                    Log.d(TAG, "FAILED TO EMPTYED CART BECAUSE: " + e.getMessage());
                }
            }
        });


    }


    public LiveData<PhoneNumberModel> get_phone_number() {
        return dbInterface.get_phone_number();
    }

    public LiveData<MySettings> get_settings() {
        return dbInterface.get_settings();
    }


    public LiveData<List<Ad>> getAds() {
        return dbInterface.get_ads();
    }

    public LiveData<List<Product>> getProducts() {
        return dbInterface.get_products();
    }

    public LiveData<List<UserModel>> get_logged_in_user() {
        return users;
    }

    public LiveData<List<CartModel>> getCart() {
        return cart;
    }


    public LiveData<Order> getOrdersById(String ordersId) {
        return dbInterface.getOrdersById(ordersId);
    }

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    public LiveData<List<SearchResults>> get_search_results() {
        return dbInterface.get_search_results();
    }


    public Product get_product(String id) {
        return dbInterface.get_product(id);
    }

    public void update_product(final Product product, final boolean delete_before_insert) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                if (product == null)
                    return;

                if (delete_before_insert) {
                    dbInterface.delete_product(product);
                    try {
                        dbInterface.save_product(product);
                        Log.d(TAG, "run: PRODUCT LIST SVED SUCCESSFULL");
                    } catch (Exception e) {
                        Log.d(TAG, "run: FAILED TO SAVE PRODUCT LIST BECAUSE " + e.getMessage());
                    }
                    return;
                } else {

                    try {
                        dbInterface.update_product(product);
                    } catch (Exception e) {

                    }
                    return;
                }


            }
        });
    }

    public void save_product_list(final List<Product> products, final boolean delete_before_insert) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                if (delete_before_insert) {
                    dbInterface.delete_all_products();
                    try {
                        dbInterface.save_product_list(products);
                        Log.d(TAG, "run: PRODUCT LIST SVED SUCCESSFULL");
                    } catch (Exception e) {
                        Log.d(TAG, "run: FAILED TO SAVE PRODUCT LIST BECAUSE " + e.getMessage());
                    }
                    return;
                } else {

                    for (Product p : products) {
                        if (get_product(p.id) == null) {
                            try {
                                dbInterface.save_product(p);
                                Log.d(TAG, "run: SUCCESS SAVED " + p.id + " ==> " + p.name);
                            } catch (Exception e) {
                                Log.d(TAG, "run: FAILED TO SAVED " + p.id + " ==> " + p.name);
                            }
                        } else {
                            try {
                                dbInterface.update_product(p);
                                Log.d(TAG, "run: SUCCESS UPDATED " + p.id + " ==> " + p.name);
                            } catch (Exception e) {
                                Log.d(TAG, "run: FAILED TO UPDATE " + p.id + " ==> " + p.name);
                            }
                        }
                    }
                    return;
                }


            }
        });
    }

    public void save_categories_list(final List<ProductCategory> categories) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (categories.size() > 1) {
                        dbInterface.delete_all_categories();
                        dbInterface.save_categories(categories);
                        Log.d(TAG, "run: CATEGORY SUCCESSFULLY SAVED " + categories.get(0).name);
                    }
                } catch (Exception e) {

                }

            }
        });
    }

    public void save_ads_list(final List<Ad> ads) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ads.size() > 1) {
                        dbInterface.delete_all_ads();
                        dbInterface.save_ads(ads);
                        Log.d(TAG, "run: ads SUCCESSFULLY SAVED " + ads.get(0).title);
                    }
                } catch (Exception e) {

                }

            }
        });
    }

    public void save_settings(final MySettings mySettings) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mySettings != null) {
                        dbInterface.delete_Settings();
                        dbInterface.save_settings(mySettings);
                    }
                } catch (Exception e) {

                }

            }
        });
    }

    public void save_search_list(final List<SearchResults> searchResults) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (searchResults.size() > 1) {
                        dbInterface.delete_all_search();
                        dbInterface.save_search_list(searchResults);
                        Log.d(TAG, "run: ads SUCCESSFULLY SAVED " + searchResults.get(0).keyword);
                    }
                } catch (Exception e) {

                }

            }
        });
    }


}
