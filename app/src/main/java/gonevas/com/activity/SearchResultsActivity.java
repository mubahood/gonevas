package gonevas.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import gonevas.com.R;
import gonevas.com.adapter.ProductAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.Product;
import gonevas.com.model.SearchResults;
import gonevas.com.utils.MyFunctions;
import gonevas.com.utils.Tools;
import gonevas.com.web.WebInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static gonevas.com.StartActivity.BASE_URL;

public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";
    public List<Product> tempProducts = new ArrayList<>();
    public List<Product> categoryProducts = new ArrayList<>();
    DatabaseRepository databaseRepository;
    SearchResultsActivity context;
    String search_word = null;

    RecyclerView category_RecyclerView;
    private ProductAdapter categoryAdapter;
    String category = "";
    MyFunctions functions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        context = this;
        functions = new MyFunctions(context);
        bind_data();

        Intent intent = getIntent();


        try {
            search_word = intent.getStringExtra("search");
            Log.d(TAG, "onCreate: ==> " + search_word);
        } catch (Exception e) {
            return;
        }

        try {
            category = intent.getStringExtra("category");
            if (search_word == null) {
                search_word = "";
            }
            Log.d(TAG, "onCreate: ==> " + search_word);
        } catch (Exception e) {
            Toast.makeText(context, "Category Not Selected. " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        this.databaseRepository = new DatabaseRepository(context);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        if (search_word.length() > 1) {
            get_search_data(search_word);
            initToolbar();
        } else {
            get_category_data(category);
            initToolbar();
        }
    }


    private void get_category_data(String a) {
        show_loader();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit shop_retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        WebInterface shopInterface = shop_retrofit.create(WebInterface.class);
        Call<List<Product>> basic_data_call = shopInterface.get_products_by_category(a + "");


        basic_data_call.enqueue(new retrofit2.Callback<List<Product>>() {


            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, final retrofit2.Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {


                        try {

                        } catch (Exception e) {
                            //data_view.setText("Failed ==> " + e.getMessage());
                        }
                        hide_loader();
                        categoryProducts = response.body();
                        feed_data();
                        return;
                    }
                } else {
                    finish();
                    return;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                Toast.makeText(context, "Poor network", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });

    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> searchArray = new ArrayList<>();


    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();

    Retrofit shop_retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    WebInterface shopInterface = shop_retrofit.create(WebInterface.class);

    private void get_search_data(final String a) {
        show_loader();
        //search_word


        Map<String, String> search_query = new HashMap<String, String>();
        search_query.put("s", search_word);
        Call<List<Product>> basic_data_call = shopInterface.get_products(search_query);

        basic_data_call.enqueue(new retrofit2.Callback<List<Product>>() {

            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, final retrofit2.Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        try {
                            if (response.body().size() > 0) {
                                databaseRepository.save_product_list(response.body(), false);
                                categoryProducts = response.body();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "No product found." + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: ");
                        }

                        feed_data();
                        return;
                    } else {
                        Toast.makeText(context, "Poor network. " + response.errorBody(), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                } else {

                    Toast.makeText(context, "Failed to get data." + response.errorBody(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: 11 ===> " + response.errorBody());
                    finish();
                    return;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                Toast.makeText(context, "Poor network. " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: ===> " + t.getMessage());
                finish();
                return;
            }
        });


    }


    private void show_loader() {
        main_container.setVisibility(View.GONE);
        main_loader.setVisibility(View.VISIBLE);
    }

    private void hide_loader() {
        main_container.setVisibility(View.VISIBLE);
        main_loader.setVisibility(View.GONE);
    }

    private void show_no_results() {
        no_results_container.setVisibility(View.VISIBLE);
        main_container.setVisibility(View.GONE);
        main_loader.setVisibility(View.GONE);
    }


    LinearLayout main_container, no_results_container;
    ProgressBar main_loader;

    private void bind_data() {
        main_container = findViewById(R.id.main_container);
        no_results_container = findViewById(R.id.no_results_container);
        main_loader = findViewById(R.id.main_loader);
        main_loader.setVisibility(View.GONE);
        main_container.setVisibility(View.GONE);
        category_RecyclerView = findViewById(R.id.category_RecyclerView);
    }

    TextView title_text;
    AppBarLayout app_bar_layout;

    private void initToolbar() {
        title_text = findViewById(R.id.title_text);
        app_bar_layout = findViewById(R.id.app_bar_layout);
        title_text.setText(search_word + "");
        Tools.setSystemBarColor(this, R.color.colorPrimary);
        app_bar_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    SearchResults searchResults = new SearchResults();

    void update_search_results() {
        Call<List<SearchResults>> basic_data_call = shopInterface.get_search_keywords();

        basic_data_call.enqueue(new retrofit2.Callback<List<SearchResults>>() {

            @Override
            public void onResponse(retrofit2.Call<List<SearchResults>> call, final retrofit2.Response<List<SearchResults>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        try {
                            if (response.body().size() > 0) {
                                databaseRepository.save_search_list(response.body());
                            }
                        } catch (Exception e) {

                        }

                    } else {

                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<SearchResults>> call, Throwable t) {

                return;
            }
        });
    }

    private void feed_data() {

        update_search_results();

        if (categoryProducts.size() < 1) {
            hide_loader();
            show_no_results();
            return;
        }
        categoryAdapter = new ProductAdapter(context, categoryProducts, 3);

        category_RecyclerView.setHasFixedSize(true);
        category_RecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        category_RecyclerView.setAdapter(categoryAdapter);

        categoryAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Product obj, int position) {
                Intent i = new Intent(context, ProductActivity.class);
                i.putExtra("id", obj.product_id);
                context.startActivity(i);
                return;
            }
        });

        hide_loader();


    }
}