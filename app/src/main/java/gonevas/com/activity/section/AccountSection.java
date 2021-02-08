package gonevas.com.activity.section;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.MainActivity2;
import gonevas.com.R;
import gonevas.com.activity.OrdersActivity;
import gonevas.com.activity.PhoneVerificationActivity;
import gonevas.com.adapter.ProductAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.Product;
import gonevas.com.model.UserModel;

public class AccountSection {

    Context context;
    View root;
    DatabaseRepository databaseRepository;
    boolean isLoggedIn = false;
    UserModel logged_in_user = new UserModel();

    public AccountSection(Context context, View root, DatabaseRepository databaseRepository) {
        this.context = context;
        this.root = root;
        this.databaseRepository = databaseRepository;

        databaseRepository.get_logged_in_user().observe((LifecycleOwner) context, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                if (userModels.isEmpty()) {
                    isLoggedIn = false;
                    bind_views();
                    return;
                } else {
                    isLoggedIn = true;
                    logged_in_user = userModels.get(0);
                    logged_in_user.init();
                    bind_views();
                    feed_loggedin_data();
                    return;
                }

            }
        });

    }

    private void feed_loggedin_data() {
        if (logged_in_user.avatar_url.length() > 2) {
            Glide.with(context)
                    .load(logged_in_user.avatar_url)
                    .fitCenter()
                    .placeholder(R.drawable.doodle_1)
                    .into(profile_photo);
        }
        account_name.setText(logged_in_user.first_name + " " + logged_in_user.last_name);
        account_phone.setText(logged_in_user.phone);


    }


    LinearLayout not_logged_in_container, logged_in_container, my_orders, my_profile;

    Button bt_create_account, bt_login;
    TextView account_name, account_phone;
    CircularImageView profile_photo;

    private void bind_views() {
        not_logged_in_container = root.findViewById(R.id.not_logged_in_container);
        bt_create_account = root.findViewById(R.id.bt_create_account);
        logged_in_container = root.findViewById(R.id.logged_in_container);
        profile_photo = root.findViewById(R.id.profile_photo);
        account_phone = root.findViewById(R.id.account_phone);
        my_profile = root.findViewById(R.id.my_profile);
        account_name = root.findViewById(R.id.account_name);
        my_orders = root.findViewById(R.id.my_orders);
        bt_login = root.findViewById(R.id.bt_login);
        logged_in_container.setVisibility(View.GONE);
        not_logged_in_container.setVisibility(View.GONE);
        if (isLoggedIn) {
            logged_in_container.setVisibility(View.VISIBLE);
        } else {
            not_logged_in_container.setVisibility(View.VISIBLE);
        }

        my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OrdersActivity.class);
                context.startActivity(i);
            }
        });

        my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to logout your account?  ");
                builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        databaseRepository.delete_all_user();
                        databaseRepository.clear_cart();
                        Toast.makeText(context, "Déconnecté avec succès!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, MainActivity2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);


                    }
                });
                builder.setNegativeButton("ANNULER", null);
                builder.show();
            }
        });

        bt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PhoneVerificationActivity.class);
                context.startActivity(i);
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PhoneVerificationActivity.class);
                context.startActivity(i);
            }
        });
    }


    private ProductAdapter justForYouAdapter;
    public List<Product> productsJustForYou = new ArrayList<>();

}

