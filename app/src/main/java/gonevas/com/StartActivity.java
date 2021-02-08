package gonevas.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.activity.SplashActivity;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.MySettings;
import gonevas.com.model.Product;
import gonevas.com.model.ResponseBasicData;
import gonevas.com.model.UserModel;
import gonevas.com.web.WebInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StartActivity extends AppCompatActivity {


    public static final String PRODUCTS_COLLECTION = "products";
    public static final String CATEGORIES_COLLECTION = "categories";
    public static final String CATEGORY_TABLE = "CATEGORY_TABLE";
    public static final String ADS_TABLE = "ADS_TABLE";
    public static final String ADS_COLLECTION = "ads";
    public static final String SEARCH_TABLE = "SEARCH_TABLE";
    public static final String USERS_COLLECTION = "users";
    List<Product> products = new ArrayList<>();
    public static final String ORDERS_COLLECTION = "orders";
    public static final String PHONE_NUMBER_TABLE = "PHONE_NUMBER_TABLE";
    public static final String CART_TABLE = "cart_table";
    public static final String ORDER_TABLE = "ORDER_TABLE";
    public static final String PRODUCT_TABLE = "PRODUCT_TABLE";
    public static final String USERS_TABLE = "USERS_TABLE";
    public static final String SETTINGS_TABLE = "SETTINGS_TABLE";
    public static final String DATABASE_NAME = "gonevas_db";
    public static final String BASE_URL = "https://gonevas.com/";
    private static final String TAG = "Mubahood";
    TextView data_view;
    Context context;
    DatabaseRepository databaseRepository;
    String string = "";


    UserModel newModel;
    String upload_data = "";
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .build();
    Retrofit shop_retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    WebInterface shopInterface = shop_retrofit.create(WebInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        open_pro();
        if (true)
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);
        context = this;



       /* startMain();
        if (true)
            return;
        databaseRepository = new DatabaseRepository(context);
        step_1_get_products();

        submit_data = findViewById(R.id.submit_data);
        data_box = findViewById(R.id.data_box);

        //startMain();

        /*Product testPro = new Product();
        Toast.makeText(context, "Posting..", Toast.LENGTH_SHORT).show();

        databaseRepository = new DatabaseRepository(context);

        data_view = findViewById(R.id.data_view);






        data_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Long clicked", Toast.LENGTH_SHORT).show();

                try {

                    data_view.setText("SUCCESS SAVE ==> " + products.size());
                } catch (Exception e) {
                    data_view.setText("Failed ==> " + e.getMessage());
                    Log.d(TAG, "onLongClick: FAILED ==>  " + e.getMessage());
                }
                return false;
            }
        });*/

        databaseRepository = new DatabaseRepository(context);
        load_settings();

    }

    void open_pro() {
        //lIntent intent = new Intent(this, ProductActivity.class);
        Intent intent = new Intent(this, MainActivity2.class);
        //intent.putExtra("id", "15437");
        this.startActivity(intent);
    }

    void load_settings() {
        databaseRepository.get_settings().observe((LifecycleOwner) context, new Observer<MySettings>() {
            @Override
            public void onChanged(MySettings mySettings) {
                if (mySettings == null) {
                    startSplash();
                } else {
                    step_1_get_products();
                }
            }
        });
    }

    void startSplash() {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
        finish();
    }

    int trials = 0;
    private void step_1_get_products() {
        Toast.makeText(context, "Juste une minute...", Toast.LENGTH_SHORT).show();
        Call<ResponseBasicData> basic_data_call = shopInterface.get_basic_data();
        /*basic_data_call.enqueue(new retrofit2.Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Succs prods " + response.body().length(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: =======> " + response.body());
                } else {
                    Toast.makeText(context, "Failed to load products because " + response.errorBody(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "Failed to parse products because " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: ==== " + t.getMessage());
            }
        });*/



        basic_data_call.enqueue(new retrofit2.Callback<ResponseBasicData>() {


            @Override
            public void onResponse(retrofit2.Call<ResponseBasicData> call, final retrofit2.Response<ResponseBasicData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        try {
                            if (response.body().searchResults.size() > 1) {
                                databaseRepository.save_search_list(response.body().searchResults);

                            }

                        } catch (Exception e) {

                        }

                        try {
                            if (response.body().ads.size() > 1) {
                                databaseRepository.save_ads_list(response.body().ads);
                            }

                        } catch (Exception e) {

                        }

                        try {
                            if (response.body().categories.size() > 1) {
                                databaseRepository.save_categories_list(response.body().categories);
                            } else {
                            }

                        } catch (Exception e) {
                            Toast.makeText(context, "no CATS ==> ERROR", Toast.LENGTH_SHORT).show();
                        }

                        try {

                            if (response.body().products.size() > 0) {
                                databaseRepository.save_product_list(response.body().products, true);
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Failed to save products because " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: ");
                        }

                        startMain();
                        return;
                    } else {
                        Toast.makeText(context, "Poor network. " + response.errorBody(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onFailure: FAILED BACAUSE 11 ===> " + response.errorBody());
                        startMain();
                        return;
                    }
                } else {
                    trials++;
                    if(trials<=3){
                        step_1_get_products();
                        Toast.makeText(context, "Error "+response.code()+", trying again "+trials, Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        Toast.makeText(context, "Error "+response.code()+", failed  "+trials, Toast.LENGTH_SHORT).show();
                        startMain();

                    }
                    return;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBasicData> call, Throwable t) {
                Toast.makeText(context, "Poor network", Toast.LENGTH_SHORT).show();
                startMain();
                return;
            }
        });


    }

    void startMain() {
        Intent intent = new Intent(context, MainActivity2.class);
        context.startActivity(intent);
        finish();
    }


}