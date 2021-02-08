package gonevas.com.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import gonevas.com.MainActivity2;
import gonevas.com.R;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.BillingModel;
import gonevas.com.model.MyResponse;
import gonevas.com.model.PhoneNumberModel;
import gonevas.com.model.UserModel;
import gonevas.com.utils.Tools;
import gonevas.com.web.WebInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static gonevas.com.StartActivity.BASE_URL;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "MUBAHOOD";
    UserModel new_customer = new UserModel();
    Button bt_submit;
    EditText first_name_view, last_name_view, email_view, phone_number_view, country_view, city_view, address_view;
    Context context;
    DatabaseRepository databaseRepository;
    PhoneNumberModel phoneNumberModel = new PhoneNumberModel();
    String temp_country = "";
    String temp_country_1 = "";
    ProgressDialog progressDialog;
    String new_user_json = "";
    Gson gson = new Gson();
    UserModel logged_in_user = new UserModel();
    BillingModel billing = new BillingModel();
    TextView bt_change_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = RegisterActivity.this;
        databaseRepository = new DatabaseRepository(context);
        progressDialog = new ProgressDialog(this);
        initToolbar();
        bind_views();
        get_db_data();
    }

    private void bind_views() {
        first_name_view = findViewById(R.id.first_name);
        bt_change_phone = findViewById(R.id.bt_change_phone);
        last_name_view = findViewById(R.id.last_name);
        bt_submit = findViewById(R.id.bt_submit);
        email_view = findViewById(R.id.email);
        address_view = findViewById(R.id.address);
        city_view = findViewById(R.id.city);
        country_view = findViewById(R.id.country);
        phone_number_view = findViewById(R.id.phone_number);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetch_form_data();
            }
        });
        country_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStateDialog(v);
            }
        });
        bt_change_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_change_number();
            }
        });
    }

    private void request_change_number() {
        AlertDialog alert = new AlertDialog.Builder(context).create();

        alert.setMessage(getString(R.string.change_number_asasuarance));
        alert.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseRepository.delete_all_phone_numbers();
                Intent i = new Intent(context, PhoneVerificationActivity.class);
                context.startActivity(i);
                finish();
            }
        });
        alert.show();

    }

    void get_db_data() {
        databaseRepository.get_phone_number().observe((LifecycleOwner) context, new Observer<PhoneNumberModel>() {

            @Override
            public void onChanged(PhoneNumberModel phone) {
                if (phone == null) {
                    Intent i = new Intent(context, PhoneVerificationActivity.class);
                    context.startActivity(i);
                    Toast.makeText(context, R.string.verify_phone_number_first, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    phoneNumberModel = phone;
                    if ((!phoneNumberModel.is_verified) || (phoneNumberModel.phone_number == null)) {
                        Intent i = new Intent(context, PhoneVerificationActivity.class);
                        context.startActivity(i);
                        Toast.makeText(context, R.string.verify_phone_number_first, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        phone_number_view.setText(phoneNumberModel.phone_number);
                        get_user_by_phone(phoneNumberModel.phone_number);

                        Log.d(TAG, "onChanged: ====> " + phoneNumberModel.phone_number);
                    }
                    Log.d(TAG, "onChanged: PHONE NOT NULL");
                }
            }
        });
    }

    private void showStateDialog(final View v) {

        final String[] array_country_codes = new String[]{
                "CIV", "ML", "LR", "GIN", "BFA", "GHA", "BD"
        };

        final String[] array_phone_codes = new String[]{
                "225", "223", "231", "224", "226", "233", "880"
        };


        final String[] array = new String[]{
                "Côte d'Ivoire", "Mali", "Liberia", "Guinea", "Burkina Faso", "Ghana", "Bangladesh"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select country");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                new_customer.phone = phoneNumberModel.phone_number;


                temp_country = array[i];
                temp_country_1 = new_customer.phone.substring(0, array_phone_codes[i].length());

                new_customer.phone = new_customer.phone.replace("+", "");

                country_view.setText(array[i] + "");
                billing.phone = new_customer.phone;
                billing.country = array_country_codes[i];
                new_customer.billing.country = array_country_codes[i];
                new_customer.shipping.country = array_country_codes[i];

                //((EditText) v).setText();

            }
        });
        builder.show();
    }

    private void fetch_form_data() {
        new_customer.first_name = first_name_view.getText().toString() + "";
        if (new_customer.first_name.length() < 3) {
            first_name_view.requestFocus();
            Toast.makeText(this, R.string.first_name_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        new_customer.password = ((TextView) findViewById(R.id.password)).getText().toString() + "";
        if (new_customer.password.length() < 3) {
            ((TextView) findViewById(R.id.password)).append("");
            Toast.makeText(this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        new_customer.last_name = last_name_view.getText().toString() + "";
        if (new_customer.last_name.length() < 3) {
            last_name_view.requestFocus();
            Toast.makeText(this, R.string.last_name_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        new_customer.email = email_view.getText().toString() + "";
        if (new_customer.email.length() < 6) {
            email_view.requestFocus();
            Toast.makeText(this, R.string.email_too_short, Toast.LENGTH_SHORT).show();
            return;
        }


        billing.phone = new_customer.phone;


        if (billing.country.length() < 2) {
            showStateDialog(city_view);
            Toast.makeText(this, R.string.country_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        billing.city = city_view.getText().toString() + "";
        if (billing.city.length() < 2) {
            city_view.requestFocus();
            Toast.makeText(this, R.string.city_too_name, Toast.LENGTH_SHORT).show();
            return;
        }

        billing.address_1 = address_view.getText().toString() + "";
        if (billing.address_1.length() < 2) {
            address_view.requestFocus();
            Toast.makeText(this, R.string.address_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        if (new_customer.phone.length() < 6) {
            Toast.makeText(this, R.string.phone_numer_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        new_customer.phone_verified = false;
        new_customer.username = new_customer.email;
        new_customer.billing.phone = new_customer.phone;
        new_customer.billing.state = billing.city;

        new_customer.billing.first_name = new_customer.first_name;
        new_customer.billing.last_name = new_customer.last_name;
        new_customer.billing.company = "Not mentioned";
        new_customer.billing.address_1 = billing.address_1;
        new_customer.billing.city = billing.city;
        new_customer.billing.state = billing.state;
        new_customer.billing.country = billing.country;
        new_customer.billing.email = new_customer.email;
        new_customer.billing.phone = new_customer.phone;
        new_customer.billing.postcode = "0000";


        new_customer.shipping.city = billing.city;
        new_customer.shipping.state = billing.state;
        new_customer.shipping.country = billing.country;
        new_customer.shipping.first_name = new_customer.first_name;
        new_customer.shipping.last_name = new_customer.last_name;
        new_customer.shipping.address_1 = billing.address_1;
        new_customer.shipping.company = new_customer.billing.company;
        new_customer.shipping.city = billing.city;
        new_customer.shipping.state = billing.state;
        new_customer.shipping.postcode = new_customer.billing.postcode;


        submit_data_process();
    }

    int trials = 0;

    private void get_user_by_phone(final String phone_number) {
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Gson gson = new Gson();


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
        Call<List<UserModel>> user_call = webInterface.get_customer_by_phone(phone_number.replace("+", ""));

        user_call.enqueue(new retrofit2.Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {

                if (!response.isSuccessful()) {
                    trials++;
                    if (trials <= 3) {
                        get_user_by_phone(phone_number);
                        Toast.makeText(context, "Error " + response.code() + ", trying again " + trials, Toast.LENGTH_SHORT).show();
                        return;

                    }
                }

                if (response.body() == null) {
                    Toast.makeText(context, "Create account", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.hide();
                //progressDialog.dismiss();
                if (response.body() == null) {
                    Toast.makeText(context, "Create account", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body().isEmpty()) {
                    Toast.makeText(context, "Create account", Toast.LENGTH_SHORT).show();
                    return;
                } else {


                    try {


                        logged_in_user = response.body().get(0);

                        logged_in_user.init();


                        try {
                            databaseRepository.save_user(logged_in_user);
                            Log.d(TAG, "onResponse: LOGGED IN ===> " + logged_in_user.first_name);
                            Toast.makeText(context, "Logged in successfull!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(context, MainActivity2.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                            Toast.makeText(context, "Déconnecté avec succès. ", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: FAILED BECAUSE ===> " + e.getMessage());
                        }

                    } catch (Exception e) {

                    }


                }
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {

                progressDialog.setMessage("Poor network. " + t.getMessage());

                progressDialog.hide();

            }
        });


    }


    private void submit_data_process() {
        progressDialog.setMessage("Time to register...");
        progressDialog.setMessage("please wait...");
        progressDialog.show();

        final AlertDialog alert = new AlertDialog.Builder(context).create();

        final Gson gson = new Gson();
        new_user_json = gson.toJson(new_customer);


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
        Call<MyResponse> user_call = webInterface.create_customer(new_user_json);

        user_call.enqueue(new retrofit2.Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                progressDialog.hide();


                if (response.isSuccessful()) {


                    if (response.body().code.equals("1")) {

                        final UserModel user_from_web = new Gson().fromJson(response.body().message, UserModel.class);


                        if (user_from_web != null) {
                            alert.setTitle("Success");
                            alert.setCancelable(false);
                            alert.setMessage("Account created successfully. ===> " + user_from_web.first_name);
                            alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    user_from_web.init();

                                    try {
                                        databaseRepository.save_user(user_from_web);
                                        Toast.makeText(context, "Logged in successfull!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(context, MainActivity2.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    } catch (Exception e) {
                                        Toast.makeText(context, "Failed to log you in. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onResponse: FAILED BECAUSE ===> " + e.getMessage());
                                    }

                                }
                            });
                            alert.show();
                        } else {
                            alert.setTitle("Error");
                            alert.setMessage("Failed to purse user data. Please try again.");
                            alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //verify_number();;
                                    //finish();
                                }
                            });
                            alert.show();
                        }


                    } else {
                        alert.setTitle("Failed to create account");
                        alert.setMessage(response.body().message);
                        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //verify_number();;
                                //finish();
                            }
                        });
                        alert.show();
                    }

                } else {

                    alert.setTitle("Error");
                    alert.setMessage("Failed gto purse web object ");
                    alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //verify_number();;
                            //finish();
                        }
                    });
                    alert.show();
                }

            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                alert.setTitle("Error");
                alert.setMessage("" + t.getMessage());
                alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //verify_number();;
                        //finish();
                    }
                });
                alert.show();
            }
        });


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

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_account);
        Tools.setSystemBarColor(this);
    }
}