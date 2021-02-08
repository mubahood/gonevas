package gonevas.com.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.adapter.CartAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.CartModel;
import gonevas.com.model.Product;
import gonevas.com.model.UserModel;
import gonevas.com.utils.MyFunctions;
import gonevas.com.utils.Tools;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "MUBAHOOOOO";
    Context context;
    MyFunctions functions;
    RecyclerView recyclerProducts;
    List<CartModel> loadedProducts = new ArrayList<>();
    List<CartModel> temp_loadedProducts = new ArrayList<>();
    int cart_count_val = 0;
    int total_cart_amount = 0;
    TextView total_cart_view;
    AppCompatButton submit_button;
    boolean isLoggedIn = false;
    DatabaseRepository databaseRepository;
    Boolean is_fed = false;
    UserModel logged_in_user = new UserModel();
    private ActionBar actionBar;
    private Toolbar toolbar;
    private CartAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        context = this;
        databaseRepository = new DatabaseRepository(context);
        functions = new MyFunctions(context);
        login_process();

        bind_view();
        get_db_data();
    }

    private void get_db_data() {
        if (is_fed) {
            update_cart_ui();
            return;
        }


        databaseRepository.getCart().observe((LifecycleOwner) context, new Observer<List<CartModel>>() {

            @Override
            public void onChanged(final List<CartModel> cartModels) {
                if (cartModels.isEmpty()) {
                    Toast.makeText(context, "Your cart is still empty.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                databaseRepository.getCart().removeObservers((LifecycleOwner) context);

                databaseRepository.getProducts().observe((LifecycleOwner) context, new Observer<List<Product>>() {
                    @Override
                    public void onChanged(List<Product> products) {
                        for (Product product : products) {
                            for (CartModel _p : cartModels) {
                                if (_p.product_id.equals(product.id)) {
                                    product.init_product();
                                    _p.product = product;
                                    loadedProducts.add(_p);
                                    Log.d(TAG, "onChanged: MUBAHOOOOOD ===> " + _p.product.name + " ==> " + _p.product.image_full);
                                }
                            }
                        }

                        feed_data();
                    }
                });


                temp_loadedProducts = new ArrayList<>();
                temp_loadedProducts.clear();

            }
        });

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setHomeButtonEnabled(false);
        Tools.setSystemBarColor(this);


        //getSupportActionBar().setElevation(0);
    }

    private void bind_view() {
        recyclerProducts = findViewById(R.id.recyclerProducts);


        submit_button = findViewById(R.id.submit_button);
        total_cart_view = findViewById(R.id.total_cart_view);
        submit_button = findViewById(R.id.submit_button);
        initToolbar();
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isLoggedIn) {
                    Intent i = new Intent(context, SubmitOrderActivity.class);
                    context.startActivity(i);
                    finish();

                } else {
                    Toast.makeText(context, "Login to proceed.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(context, PhoneVerificationActivity.class);
                    context.startActivity(i);
                    //finish();
                }
            }
        });
        initToolbar();
    }

    void login_process() {
        databaseRepository.get_logged_in_user().observe((LifecycleOwner) context, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                Log.d(TAG, "onChanged: ==> found => " + userModels.size());
                if (userModels.isEmpty()) {
                    isLoggedIn = false;
                    return;
                } else {
                    isLoggedIn = true;
                    logged_in_user = userModels.get(0);
                }
            }
        });
    }

    private void feed_data() {
        update_cart_ui();
        is_fed = true;
        recyclerProducts.setLayoutManager(new LinearLayoutManager(context));
        recyclerProducts.setHasFixedSize(true);
        mAdapter = new CartAdapter(context, loadedProducts, databaseRepository, 1);
        recyclerProducts.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final CartModel obj, final int position, boolean isRemoveProduct) {
                if (isRemoveProduct) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Êtes-vous sûr de vouloir supprimer  " + obj.product.name + " produit du panier?");
                    builder.setPositiveButton("RETIRER", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            databaseRepository.remove_item_from_cart(loadedProducts.get(position));
                            loadedProducts.remove(position);
                            mAdapter.notifyDataSetChanged();
                            get_db_data();
                            update_cart_ui();
                        }
                    });
                    builder.setNegativeButton("ANNULER", null);
                    builder.show();


                } else {
                    get_db_data();
                    update_cart_ui();
                }

            }
        });

    }

    private void update_cart_ui() {
        total_cart_amount = 0;
        temp_loadedProducts = new ArrayList<>();
        temp_loadedProducts.clear();

        cart_count_val = loadedProducts.size();


        for (CartModel c : loadedProducts) {
            try {
                total_cart_amount += ((c.quantity) * (Integer.valueOf(c.product.cart_price + "")));
            } catch (Exception e) {

            }
        }
        total_cart_view.setText(functions.toMoneyFormat(total_cart_amount) + " CFA");


    }

}
