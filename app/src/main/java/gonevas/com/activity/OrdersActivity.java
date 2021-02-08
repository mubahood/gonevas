package gonevas.com.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import gonevas.com.R;
import gonevas.com.adapter.OrdersAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.MyResponse;
import gonevas.com.model.Order;
import gonevas.com.model.OrderFirebase;
import gonevas.com.model.UserModel;
import gonevas.com.utils.Tools;
import gonevas.com.web.WebInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static gonevas.com.StartActivity.BASE_URL;
import static gonevas.com.StartActivity.ORDERS_COLLECTION;

public class OrdersActivity extends AppCompatActivity {

    OrdersActivity context;
    UserModel logged_in_user = new UserModel();
    RecyclerView recyc_orders;
    RelativeLayout no_order_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        context = OrdersActivity.this;
        bind_views();
        initToolbar();
        progressDialog = new ProgressDialog(this);
        databaseRepository = new DatabaseRepository(context);
        login_process();
    }

    private void bind_views() {
        context.recyc_orders = context.findViewById(R.id.recyc_orders);
        context.no_order_container = context.findViewById(R.id.no_order_container);
    }


    private OrdersAdapter ordersAdapter;

    private void feed_data() {

        if (orders.isEmpty()) {
            context.recyc_orders.setVisibility(View.GONE);
            context.no_order_container.setVisibility(View.VISIBLE);
        } else {
            context.recyc_orders.setVisibility(View.VISIBLE);
            context.no_order_container.setVisibility(View.GONE);

            ordersAdapter = new OrdersAdapter(context, orders, 1);

            context.recyc_orders.setHasFixedSize(true);
            context.recyc_orders.setLayoutManager(new LinearLayoutManager(context));
            context.recyc_orders.setAdapter(ordersAdapter);


            ordersAdapter.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Order obj, int position) {
                    Intent i = new Intent(context, OrderActivity.class);
                    i.putExtra("id", obj.id);
                    context.startActivity(i);
                    //Toast.makeText(context, "Order ===> " + obj.id, Toast.LENGTH_SHORT).show();
                    return;
                }
            });

        }

    }


    DatabaseRepository databaseRepository;
    Boolean isLoggedIn = false;

    private static final String TAG = "MUHINDO";

    void login_process() {
        databaseRepository.get_logged_in_user().observe((LifecycleOwner) context, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                if (userModels.isEmpty()) {
                    isLoggedIn = false;
                    Toast.makeText(context, "Connectez-vous avant de continuer", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                } else {
                    databaseRepository.get_logged_in_user().removeObservers(context);
                    isLoggedIn = true;
                    logged_in_user = userModels.get(0);
                    logged_in_user.prepare_objects();
                    get_orders();
                }
            }
        });


    }

    ProgressDialog progressDialog;
    List<Order> orders = new ArrayList<>();
    List<Order> temp_orders = new ArrayList<>();
    OrderFirebase ordersFirebase = new OrderFirebase();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void get_orders() {
        progressDialog.setMessage("S'il vous plaît, attendez");
        progressDialog.setCancelable(false);
        progressDialog.show();


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

        WebInterface webInterface = shop_retrofit.create(WebInterface.class);
        Call<MyResponse> user_call = webInterface.customer_orders(logged_in_user.id);

        user_call.enqueue(new retrofit2.Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                progressDialog.hide();
                progressDialog.dismiss();
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Failed to fetch orders because " + response.errorBody(), Toast.LENGTH_LONG).show();
                    feed_data();
                    return;
                } else {

                    if (response.body().code.equals("1")) {

                        Gson g = new Gson();
                        try {
                            orders = Arrays.asList(g.fromJson(response.body().message, Order[].class));
                            databaseRepository.save_orders_list(orders);

                            for (Order o : orders) {
                                Log.d(TAG, "\n\n\nonResponse:  ============================== ");
                                Log.d(TAG, "\n\n\nonResponse: ORDER ===> " + o.id);
                                Log.d(TAG, "onResponse: ===> " + o.line_items.size());
                                if (o.line_items.size() > 0) {
                                    Log.d(TAG, "onResponse: ===> " + o.line_items.get(0).name + "\n");
                                    Log.d(TAG, "onResponse: ===> " + o.line_items.get(0).price + "\n");
                                    Log.d(TAG, "onResponse: ===> " + o.line_items.get(0).product_id + "\n");
                                }
                                Log.d(TAG, "nonResponse:  ============================== \n\n\n");
                            }
                            Log.d("MHINDOOOOZ ==> " + orders.size(), " " + logged_in_user.id);
                            Log.d("MHINDOOOOZ", "onResponse: " + response.body().message);
                        } catch (Exception e) {
                            Log.d("MHINDOOOOZ", "FAILED BECAUSE: " + e.getMessage());

                        }

                        feed_data();
                        return;

                    } else {


                        Toast.makeText(context, "Failed because " + response.message(), Toast.LENGTH_LONG).show();
                        feed_data();
                        return;
                    }

                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

                Toast.makeText(context, "Poor network. " + t.getMessage(), Toast.LENGTH_SHORT).show();
                feed_data();
                progressDialog.hide();
                progressDialog.dismiss();

            }
        });


        if (true)
            return;
        db.collection(ORDERS_COLLECTION).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        progressDialog.hide();
                        progressDialog.dismiss();

                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(context, "No orders found", Toast.LENGTH_SHORT).show();

                        } else {

                            for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                                ordersFirebase = q.toObject(OrderFirebase.class);
                                orders.add(new Gson().fromJson(ordersFirebase.order_json, Order.class));
                                databaseRepository.save_orders_list(orders);
                            }

                        }

                        feed_data();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed because " + e.getMessage() + ". " + e.getCause(), Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        progressDialog.dismiss();
                    }
                });


    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.my_orders);
        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void show_message(String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //verify_number();;

            }
        });
        alert.show();
        return;
    }


}