package gonevas.com.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import gonevas.com.MainActivity2;
import gonevas.com.R;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.fragment.FragmentConfirmation;
import gonevas.com.fragment.FragmentPayment;
import gonevas.com.fragment.FragmentShipping;
import gonevas.com.model.CartModel;
import gonevas.com.model.CartOrder;
import gonevas.com.model.CartOrderLineItems;
import gonevas.com.model.MyResponse;
import gonevas.com.model.OrderFirebase;
import gonevas.com.model.Product;
import gonevas.com.model.UserModel;
import gonevas.com.utils.MyFunctions;
import gonevas.com.utils.Tools;
import gonevas.com.web.WebInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static gonevas.com.R.string.login_before_you_proceed;
import static gonevas.com.StartActivity.BASE_URL;
import static gonevas.com.StartActivity.ORDERS_COLLECTION;
import static gonevas.com.StartActivity.PRODUCTS_COLLECTION;

public class SubmitOrderActivity extends AppCompatActivity {
    private enum State {
        SHIPPING,
        PAYMENT,
        CONFIRMATION,
        NONE
    }

    MyFunctions functions;
    DatabaseRepository databaseRepository;
    State[] array_state = new State[]{State.SHIPPING, State.PAYMENT, State.CONFIRMATION};
    private View line_first, line_second;
    private ImageView image_shipping, image_payment, image_confirm;
    private TextView tv_shipping, tv_payment, tv_confirm;
    SubmitOrderActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_order);
        context = this;
        functions = new MyFunctions(context);
        databaseRepository = new DatabaseRepository(context);

        initToolbar();
        initComponent();
        login_process();
        get_cart_data();
    }


    CartOrder orderModel = new CartOrder();
    CartOrder orderFromWeb = new CartOrder();
    String extra_message_val = "";
    String order_data = "";
    ProgressDialog progressDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void do_submit_order() {


        orderModel.billing = logged_in_user.billing;
        orderModel.shipping = logged_in_user.shipping;

        if (logged_in_user.user_id.length() > 0) {
            orderModel.customer_id = logged_in_user.phone;
        } else if (logged_in_user.id.length() > 0) {
            orderModel.customer_id = logged_in_user.phone;
        }


        orderModel.customer_note = extra_message_val + "";
        orderModel.payment_method = "bacs";
        orderModel.payment_method_title = "Direct Bank Transfer";

        if (logged_in_user.email.length() < 3) {
            Toast.makeText(context, "Wrong email", Toast.LENGTH_SHORT).show();
            return;
        }

        orderModel.status = "processing";
        orderModel.set_paid = false;


        progressDialog.setMessage("S'il vous plaît, attendez");
        progressDialog.setCancelable(false);
        progressDialog.show();

        orderModel.currency = "XOF";
        orderModel.parent_id = "0";
        orderModel.customer_id = logged_in_user.phone;
        orderModel.id = db.collection(ORDERS_COLLECTION).document().getId() + "";

        Gson g = new Gson();
        try {
            order_data = g.toJson(orderModel, CartOrder.class);
            Log.d("MUBS", "do_submit_order: ===> " + order_data);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to parse new order because " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("MUBS", "do_submit_order: ==> " + e.getMessage());
            progressDialog.hide();
            progressDialog.dismiss();
            return;
        }


        Log.d("MUBAHOOOOOODS", "do_submit_order: \n\n\n\n\n" + order_data + "\n\n\n\n\n");
        Toast.makeText(context, "Soumission...", Toast.LENGTH_SHORT).show();


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
        Call<MyResponse> user_call = webInterface.create_order(order_data);

        user_call.enqueue(new retrofit2.Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                progressDialog.hide();
                progressDialog.dismiss();
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Failed to create order", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    if (response.body().code.equals("1")) {

                        Gson g = new Gson();
                        try {
                            Log.d(TAG, "onResponse: " + response.message());
                            databaseRepository.clear_cart();
                        } catch (Exception e) {

                        }

                        AlertDialog alert = new AlertDialog.Builder(context).create();
                        alert.setCancelable(false);
                        alert.setTitle("SUCCESS");
                        alert.setMessage("Votre commande a été soumise avec succès!\n" +
                                "\n" +
                                "Ouvrez la section Compte pour voir les détails de votre commande.");
                        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(context, MainActivity2.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            }
                        });
                        alert.show();
                        return;

                    } else {


                        AlertDialog alert = new AlertDialog.Builder(context).create();
                        alert.setCancelable(false);
                        alert.setTitle("FAILED");
                        alert.setMessage(response.body().message.toString());
                        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        alert.show();
                        return;
                    }

                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

                progressDialog.setMessage("Poor network. " + t.getMessage());

                progressDialog.hide();
                progressDialog.dismiss();

            }
        });

        /*end of order submidion*/


    }

    OrderFirebase orderFirebase = new OrderFirebase();

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


    private int idx_state = 0;


    private void initComponent() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        line_first = (View) findViewById(R.id.line_first);
        line_second = (View) findViewById(R.id.line_second);
        image_shipping = (ImageView) findViewById(R.id.image_shipping);
        image_payment = (ImageView) findViewById(R.id.image_payment);
        image_confirm = (ImageView) findViewById(R.id.image_confirm);

        tv_shipping = (TextView) findViewById(R.id.tv_shipping);
        tv_payment = (TextView) findViewById(R.id.tv_payment);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);

        image_payment.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);
        image_confirm.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);

        (findViewById(R.id.lyt_next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stack.isEmpty()) {
                    displayPayment();
                    //Toast.makeText(context, "==> stack is empty done", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (stack.size() == 1) {
                    displayConfirm();
                    return;
                }
                if (stack.size() == 2) {
                    do_submit_order();
                    displayConfirm();
                    return;
                }


            }
        });

        (findViewById(R.id.lyt_previous)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stack.size() == 0) {
                    onBackPressed();
                    return;
                }

                if (stack.size() == 1) {
                    displayShipping();
                    return;
                }

                if (stack.size() == 2) {
                    displayPayment();
                    return;
                }

            }
        });
    }


    void login_process() {
        databaseRepository.get_logged_in_user().observe((LifecycleOwner) context, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                Log.d(TAG, "onChanged: ==> found => " + userModels.size());
                if (userModels.isEmpty()) {
                    isLoggedIn = false;
                    Toast.makeText(context, login_before_you_proceed, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    databaseRepository.get_logged_in_user().removeObservers(context);
                    isLoggedIn = true;
                    logged_in_user = userModels.get(0);
                    logged_in_user.prepare_objects();
                    displayShipping();
                }
            }
        });


    }

    UserModel logged_in_user = new UserModel();
    boolean isLoggedIn = false;
    private static final String TAG = "SubmitOrderActivity";

    boolean state_shipping_initialized = false;

    Fragment fragment;
    FragmentShipping fragmentShipping;
    ArrayList<String> stack = new ArrayList<>();


    private void displayShipping() {
        refreshStepTitle();
        if (!state_shipping_initialized) {
            state_shipping_initialized = true;
            fragmentShipping = new FragmentShipping(logged_in_user);
            tv_shipping.setTextColor(getResources().getColor(R.color.grey_90));
            image_shipping.clearColorFilter();

            stack.clear();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, fragmentShipping);
            fragmentTransaction.commit();

        } else {
            tv_shipping.setTextColor(getResources().getColor(R.color.grey_90));
            image_shipping.clearColorFilter();

            stack.clear();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, fragmentShipping);
            fragmentTransaction.commit();
        }


    }

    int price = 0;

    private void displayConfirm() {


        line_first.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        line_second.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        image_payment.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        image_confirm.clearColorFilter();
        tv_confirm.setTextColor(getResources().getColor(R.color.grey_90));

        stack.clear();
        stack.add(state_payment);
        stack.add(state_confirm);


        if (loadedProducts.isEmpty()) {


            databaseRepository.getCart().observe((LifecycleOwner) context, new Observer<List<CartModel>>() {

                @Override
                public void onChanged(final List<CartModel> cartModels) {
                    if (cartModels.isEmpty()) {
                        Toast.makeText(context, "Your cart is still empty.", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    for (CartModel c : cartModels) {
                        CartOrderLineItems cartOrderLineItem = new CartOrderLineItems();
                        c.product.init_product();
                        cartOrderLineItem.name = c.product_id;
                        cartOrderLineItem.product_id = c.product_id + "";
                        cartOrderLineItem.quantity = c.quantity + "";
                        if (c.is_variable) {
                            if (c.selected_variable_id != null) {
                                if (c.selected_variable_id.length() > 0) {
                                    try {
                                        cartOrderLineItem.variation_id = c.selected_variable_id;
                                        Log.d(TAG, "onChanged: variation_thing aDDED VARIATION  =====> " + c.selected_variable_id);
                                    } catch (Exception e) {
                                        Log.d(TAG, "onChanged: variation_thing  =====> failed to add because  " + e.getMessage());
                                    }
                                } else {
                                    Log.d(TAG, "onChanged: variation_thing  =====> is less than zero ==> " + c.selected_variable_id);
                                }
                            } else {
                                Log.d(TAG, "onChanged: variation_thing  =====> ID IS NULLE");
                            }
                        } else {
                            Log.d(TAG, "onChanged: variation_thing  =====> is not variable");
                        }

                        Log.d(TAG, "MUBAHOOOD: ADDEDD===> " + c.product_id);

                        try {
                            price += ((c.quantity) * (Integer.valueOf(c.product.price + "")));
                        } catch (Exception e) {

                        }

                        orderModel.line_items.add(cartOrderLineItem);
                    }

                    orderFirebase.total = price + "";
                    databaseRepository.getCart().removeObservers((LifecycleOwner) context);

                    db.collection(PRODUCTS_COLLECTION)
                            .get(Source.CACHE)
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.isEmpty()) {

                                        return;
                                    } else {
                                        for (QueryDocumentSnapshot p : queryDocumentSnapshots) {
                                            Product product = p.toObject(Product.class);
                                            for (CartModel _p : cartModels) {
                                                if (_p.product_id.equals(product.id)) {
                                                    product.init_product();
                                                    _p.product = product;
                                                    _p.product.init_product();
                                                    loadedProducts.add(_p);
                                                    Log.d(TAG, "onChanged: MUBAHOOOOOD ===> " + _p.product.name + " ==> " + _p.product.image_full);
                                                }
                                            }
                                        }

                                        refreshStepTitle();
                                        Fragment fragment = new FragmentConfirmation(logged_in_user, cartModels, functions);
                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.frame_content, fragment);
                                        fragmentTransaction.commit();
                                    }

                                    //Toast.makeText(context, " ===." + current_product.name, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {

                                    } else {

                                    }

                                }
                            });

                    temp_loadedProducts = new ArrayList<>();
                    temp_loadedProducts.clear();

                }
            });


        } else {
            refreshStepTitle();
            Fragment fragment = new FragmentConfirmation(logged_in_user, loadedProducts, functions);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, fragment);
            fragmentTransaction.commit();
        }


    }

    void submit_oder_process() {

    }


    String state_shipping = "SHIPPING";
    String state_payment = "PAYMENT";
    String state_confirm = "CONFIRM";
    String state_none = "NONE";
    State state;
    String current_fragment = "";
    String prev_fragment = "";
    String next_fragment = "";

    private void displayFragment(State state) {


        if (state.name().equalsIgnoreCase(State.SHIPPING.name())) {

        } else if (state.name().equalsIgnoreCase(State.PAYMENT.name())) {


        } else if (state.name().equalsIgnoreCase(State.CONFIRMATION.name())) {

        }


    }

    private boolean displayPayment() {

        logged_in_user = fragmentShipping.get_user();
        if (logged_in_user.shipping.first_name.length() < 2) {
            Toast.makeText(context, R.string.first_name_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (logged_in_user.shipping.last_name.length() < 2) {
            Toast.makeText(context, R.string.last_name_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (logged_in_user.shipping.address_1.length() < 2) {
            Toast.makeText(context, R.string.address_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (logged_in_user.shipping.state.length() < 2) {
            Toast.makeText(context, R.string.center_correct_state, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (logged_in_user.shipping.address_2.length() > 2) {
            orderModel.customer_note = logged_in_user.shipping.address_2;
            logged_in_user.shipping.address_2 = "";
        }


        databaseRepository.save_user(logged_in_user);

        refreshStepTitle();


        fragment = null;
        fragmentPayment = new FragmentPayment();
        line_first.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        image_shipping.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        image_payment.clearColorFilter();
        tv_payment.setTextColor(getResources().getColor(R.color.grey_90));
        line_second.setBackgroundColor(getResources().getColor(R.color.grey_10));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, fragmentPayment);
        fragmentTransaction.commit();


        stack.clear();
        stack.add(state_payment);
        return true;
    }

    FragmentPayment fragmentPayment;

    private void refreshStepTitle() {
        tv_shipping.setTextColor(getResources().getColor(R.color.grey_20));
        tv_payment.setTextColor(getResources().getColor(R.color.grey_20));
        tv_confirm.setTextColor(getResources().getColor(R.color.grey_20));
        line_first.setBackgroundColor(getResources().getColor(R.color.grey_20));
        line_second.setBackgroundColor(getResources().getColor(R.color.grey_20));
    }

    List<CartModel> loadedProducts = new ArrayList<>();
    List<CartModel> temp_loadedProducts = new ArrayList<>();


    private void get_cart_data() {


        databaseRepository.getCart().observe((LifecycleOwner) context, new Observer<List<CartModel>>() {

            @Override
            public void onChanged(List<CartModel> cartModels) {
                if (cartModels.isEmpty()) {
                    Toast.makeText(context, "Your cart is still empty.", Toast.LENGTH_SHORT).show();

                    return;
                }
                databaseRepository.getCart().removeObservers(context);

                temp_loadedProducts = new ArrayList<>();
                temp_loadedProducts.clear();
                temp_loadedProducts = cartModels;

            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.menu_setting, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        ;*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == android.R.id.home) {

        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }*/
        return super.onOptionsItemSelected(item);
    }
}