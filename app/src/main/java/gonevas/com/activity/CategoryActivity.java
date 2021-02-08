package gonevas.com.activity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import gonevas.com.R;
import gonevas.com.adapter.ProductAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.Product;
import gonevas.com.model.ProductCategory;
import gonevas.com.utils.Tools;
import gonevas.com.web.WebInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static gonevas.com.StartActivity.BASE_URL;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";
    public List<Product> categoryProducts = new ArrayList<>();
    DatabaseRepository databaseRepository;
    CategoryActivity context;
    String cat_id = null;

    RecyclerView category_RecyclerView;
    private ProductAdapter categoryAdapter;
    ProductCategory productCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        context = this;


        Intent intent = getIntent();

        try {
            title_data = intent.getStringExtra("title");
        } catch (Exception e) {
            title_data = "No title";
        }

        try {
            cat_id = intent.getStringExtra("cat_id");
        } catch (Exception e) {

        }


        this.databaseRepository = new DatabaseRepository(context);
        bind_data();

        if (cat_id != null) {


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
            Call<List<Product>> basic_data_call = shopInterface.get_products_by_category(cat_id);


            basic_data_call.enqueue(new retrofit2.Callback<List<Product>>() {


                @Override
                public void onResponse(retrofit2.Call<List<Product>> call, final retrofit2.Response<List<Product>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {


                            try {

                            } catch (Exception e) {
                                //data_view.setText("Failed ==> " + e.getMessage());
                            }


                            categoryProducts = response.body();
                            databaseRepository.save_product_list(categoryProducts, false);
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



            /*======OLD======*/

           /* db.collection(CATEGORIES_COLLECTION)
                    .whereEqualTo("id", Integer.valueOf(cat_id))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(context, "No data to display.", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }

                            productCategory = queryDocumentSnapshots.toObjects(ProductCategory.class).get(0);

                            if (productCategory == null) {
                                Toast.makeText(context, "Category is null.", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }

                            ad = new Ad();
                            ad.position = 0;
                            ad.title = productCategory.name;
                            ad.photo = productCategory.photo;
                            ad.category = String.valueOf(productCategory.id + "");

                            db.collection(PRODUCTS_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.isEmpty()) {
                                        Toast.makeText(context, "No products to display.", Toast.LENGTH_SHORT).show();
                                        finish();
                                        return;
                                    }

                                    for (Product p : queryDocumentSnapshots.toObjects(Product.class)) {
                                        if (p.category_ids.contains(Integer.valueOf(ad.category + ""))) {
                                            categoryProducts.add(p);
                                        }
                                    }


                                }
                            });


                        }
                    });
        */


        }

    }


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void show_loader() {
        main_container.setVisibility(View.GONE);
        main_loader.setVisibility(View.VISIBLE);
    }

    private void hide_loader() {
        main_container.setVisibility(View.VISIBLE);
        main_loader.setVisibility(View.GONE);
    }


    LinearLayout main_container;
    ProgressBar main_loader;

    private void bind_data() {
        main_container = findViewById(R.id.main_container);
        main_loader = findViewById(R.id.main_loader);
        main_loader.setVisibility(View.GONE);
        main_container.setVisibility(View.GONE);
        category_RecyclerView = findViewById(R.id.category_RecyclerView);
        show_loader();
    }

    TextView title_text;
    AppBarLayout app_bar_layout;
    String title_data = "Gonevas Mall";

    private void initToolbar() {
        title_text = findViewById(R.id.title_text);
        app_bar_layout = findViewById(R.id.app_bar_layout);

        if (title_data != null) {
            if (title_data.length() < 2) {
                title_text.setText("Gonevas Mall");

            } else {
                title_text.setText(title_data);

            }
        } else {
            title_text.setText("Gonevas Mall");
        }
        Tools.setSystemBarColor(this, R.color.colorPrimary);
        app_bar_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void feed_data() {

        if (categoryProducts.size() < 1) {
            Toast.makeText(context, "No product found ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        hide_loader();
        initToolbar();
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


    }
}