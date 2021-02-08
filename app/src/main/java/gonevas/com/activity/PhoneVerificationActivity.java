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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import gonevas.com.R;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.PhoneNumberModel;
import gonevas.com.utils.MyFunctions;
import gonevas.com.utils.Tools;

public class PhoneVerificationActivity extends AppCompatActivity {

    private Context context;
    private String code = "";
    private MyFunctions functions;
    DatabaseRepository db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        context = this;
        functions = new MyFunctions(context);
        db = new DatabaseRepository(context);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        bind_views();
        initToolbar();
    }

    LinearLayout enter_code_container, enter_phone_number_container;
    EditText country_view, phone_number_view, phone_number;
    Button bt_submit, bt_submit_code;

    @Override
    protected void onStart() {
        //phoneNumberModels =
        super.onStart();
    }

    void get_db_data() {

        db.get_phone_number().observe((LifecycleOwner) context, new Observer<PhoneNumberModel>() {

            @Override
            public void onChanged(PhoneNumberModel phone) {
                if (phone == null) {
                    show_phone();
                    Log.d(TAG, "onChanged: PHONE isss NULL");
                } else {
                    phoneNumberModel = phone;
                    if (!phoneNumberModel.is_verified) {
                        show_verify_code();
                    } else {
                        Intent i = new Intent(context, RegisterActivity.class);
                        context.startActivity(i);
                        finish();
                    }
                    Log.d(TAG, "onChanged: PHONE NOT NULL");
                }
            }
        });
    }


    EditText code_field;

    TextView bt_request_another_code;

    private void bind_views() {
        country_view = (EditText) findViewById(R.id.country);
        phone_number_view = (EditText) findViewById(R.id.phone_number);
        bt_submit = findViewById(R.id.bt_submit);
        code_field = findViewById(R.id.code_field);
        bt_request_another_code = findViewById(R.id.bt_request_another_code);
        bt_submit_code = findViewById(R.id.bt_submit_code);
        enter_code_container = findViewById(R.id.enter_code_container);
        enter_phone_number_container = findViewById(R.id.enter_phone_number_container);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetch_form_data();
                return;
            }
        });
        country_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStateDialog(v);
                return;
            }
        });
        bt_request_another_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_another_code();
                return;
            }
        });

        bt_submit_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(context, RegisterActivity.class);
                //context.startActivity(i);

                do_code_verification_process();
                return;
            }
        });
        show_phone();
        get_db_data();
    }

    String time_now = "";
    String time_ago = "";
    Long time_dif;

    private void request_another_code() {
        if (phoneNumberModel != null) {
            if (phoneNumberModel.code_request_time != null) {
                if (phoneNumberModel.code_request_time.length() > 3) {
                    time_ago = phoneNumberModel.code_request_time;
                    time_now = functions.getTimeStamp();
                    time_dif = Long.valueOf(time_now) - Long.valueOf(time_ago);
                    time_dif = Long.valueOf((time_dif / 60));
                    if (time_dif > 2) {
                        AlertDialog alert = new AlertDialog.Builder(context).create();

                        alert.setMessage(getString(R.string.are_you_sure_request_another_code));
                        alert.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.request_another_code_), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.delete_all_phone_numbers();
                                Intent i = new Intent(context, PhoneVerificationActivity.class);
                                context.startActivity(i);
                                finish();
                            }
                        });
                        alert.show();
                    } else {
                        AlertDialog alert = new AlertDialog.Builder(context).create();
                        alert.setMessage(getString(R.string.wait_for_two_minutes));
                        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alert.show();
                    }
                }
            }
        }

    }

    String code_field_val = "";

    private void do_code_verification_process() {
        code_field_val = code_field.getText().toString();

        if (code_field_val == null) {
            Toast.makeText(context, "Enter correct code.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code_field_val.length() < 3) {
            Toast.makeText(context, "Code too short.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumberModel == null) {
            Toast.makeText(context, "Phone number not set.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumberModel.verification_code == null) {
            Toast.makeText(context, "Phone number not set.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumberModel.verification_code.length() < 3) {
            Toast.makeText(context, phoneNumberModel.verification_code + " <<== Verification code too short.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumberModel.verification_code.equals(code_field_val)) {
            phoneNumberModel.code_verification_time = functions.getTimeStamp();
            phoneNumberModel.is_verified = true;
            db.save_phone_number(phoneNumberModel);
            Toast.makeText(context, R.string.success_phone_verification, Toast.LENGTH_LONG).show();
            Intent i = new Intent(context, RegisterActivity.class);
            context.startActivity(i);
            finish();
            return;
        } else {
            Toast.makeText(context, R.string.wrong_code, Toast.LENGTH_LONG).show();
            return;
        }
    }

    void test_verification_code() {
        code = "123456";
        phoneNumberModel = new PhoneNumberModel();

        phoneNumberModel.phone_number = new_phone_number;
        if (phoneNumberModel.phone_number.length() < 5) {
            Toast.makeText(context, "Phone number too short", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.hide();
        progressDialog.dismiss();
        if (code == null) {
            Log.d(TAG, "CODE IS NULL: ===> " + code);
            return;
        }
        if (code.length() < 2) {
            Log.d(TAG, "CODE IS SHORT: ===> " + code);
            return;
        }

        phoneNumberModel.id = "1";
        phoneNumberModel.code_verification_time = "0";
        phoneNumberModel.is_verified = true;
        phoneNumberModel.verification_code = code;
        phoneNumberModel.code_request_time = functions.getTimeStamp();

        try {
            db.save_phone_number(phoneNumberModel);
            Log.d(TAG, "onCodeSent: SVED 111===> " + code + " phone ==> " + phoneNumberModel.phone_number);
            Intent i = new Intent(context, RegisterActivity.class);
            context.startActivity(i);
            finish();
        } catch (Exception e) {
            Log.d(TAG, "FAILED TO SAVED: 2222 ===> " + e.getMessage());
        }

    }

    ProgressDialog progressDialog;
    private static final String TAG = "MUBAHOOOOO";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

            Log.d(TAG, "onCodeSent:  CODE SENT  ==> " + s);
            super.onCodeSent(s, forceResendingToken);

        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {

            Toast.makeText(context, "CODE TIME OUT  ==> " + s, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCodeSent:  CODE TIME OUT  ==> " + s);
            super.onCodeAutoRetrievalTimeOut(s);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            code = phoneAuthCredential.getSmsCode();

            phoneNumberModel = new PhoneNumberModel();

            phoneNumberModel.phone_number = new_phone_number;
            if (phoneNumberModel.phone_number.length() < 5) {
                Toast.makeText(context, "Phone number too short", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.hide();
            progressDialog.dismiss();
            if (code == null) {
                Log.d(TAG, "CODE IS NULL: ===> " + code);
                return;
            }
            if (code.length() < 2) {
                Log.d(TAG, "CODE IS SHORT: ===> " + code);
                return;
            }

            phoneNumberModel.id = "1";
            phoneNumberModel.code_verification_time = "0";
            phoneNumberModel.is_verified = false;
            phoneNumberModel.verification_code = code;
            phoneNumberModel.code_request_time = functions.getTimeStamp();

            try {
                db.save_phone_number(phoneNumberModel);
                show_verify_code();
                Log.d(TAG, "onCodeSent: SVED 111===> " + code + " phone ==> " + phoneNumberModel.phone_number);
            } catch (Exception e) {
                Log.d(TAG, "FAILED TO SAVED: 2222 ===> " + e.getMessage());
            }

        }


        @Override

        public void onVerificationFailed(FirebaseException e) {
            Log.d(TAG, "onVerificationFailed: Verification failed ==> " + e.getMessage());
            progressDialog.hide();
            AlertDialog alert = new AlertDialog.Builder(context).create();
            alert.setTitle("Failed");
            alert.setMessage(e.getMessage());
            alert.setButton(Dialog.BUTTON_POSITIVE, "TRY AGAIN", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //verify_number();;
                    Toast.makeText(context, "Trying....", Toast.LENGTH_SHORT).show();
                }
            });
            alert.show();
        }
    };

    private void show_verify_code() {
        enter_code_container.setVisibility(View.VISIBLE);
        enter_phone_number_container.setVisibility(View.GONE);
        Toast.makeText(context, "Enter code", Toast.LENGTH_SHORT).show();
    }

    private void show_phone() {
        enter_code_container.setVisibility(View.GONE);
        enter_phone_number_container.setVisibility(View.VISIBLE);
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

                phoneNumberModel.phone_number = phone_number_view.getText().toString() + "";
                if (phoneNumberModel.phone_number.length() < 6) {
                    country_view.setText(array[i] + "");
                    phone_number_view.setText("");
                    phone_number_view.append(array_phone_codes[i]);
                    phone_number_view.requestFocus();
                    return;
                }


                temp_country = array[i];
                temp_country_1 = phoneNumberModel.phone_number.substring(0, array_phone_codes[i].length());

                phoneNumberModel.phone_number = phoneNumberModel.phone_number.replace("+", "");

                if (!temp_country_1.equals(array_phone_codes[i])) {
                    country_view.setText(array[i]);
                    phone_number_view.setText("");
                    phone_number_view.append(array_phone_codes[i]);
                    phone_number_view.requestFocus();
                    return;
                }
                phoneNumberModel.phone_number = "+" + phoneNumberModel.phone_number;
                new_phone_number = phoneNumberModel.phone_number;
                country_view.setText(array[i] + "");
                phoneNumberModel.country = array_country_codes[i];

                phone_code_is_set = true;

                //((EditText) v).setText();

            }
        });
        builder.show();
    }

    String new_phone_number = "";
    boolean phone_code_is_set = false;

    public void request_code() {


        phoneNumberModel.id = "1";
        phoneNumberModel.code_verification_time = "1";
        phoneNumberModel.is_verified = true;
        phoneNumberModel.verification_code = "0000";
        phoneNumberModel.code_request_time = functions.getTimeStamp();



        /*Intent i = new Intent(context, RegisterActivity.class);
        context.startActivity(i);
        finish();

        if (true)
            return;*/
        if (phoneNumberModel.phone_number.length() < 6) {
            Toast.makeText(context, "Phone number too short", Toast.LENGTH_SHORT).show();
            return;
        }

        /*progressDialog.setCancelable(false);
        progressDialog.setMessage("S'il vous plaît, attendez...");
        progressDialog.show();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumberModel.phone_number + "",
                100,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );*/

        try {
            db.save_phone_number(phoneNumberModel);
            Log.d(TAG, "onCodeSent: SVED 111===> " + code + " phone ==> " + phoneNumberModel.phone_number);
            Intent i = new Intent(context, RegisterActivity.class);
            context.startActivity(i);
            finish();
        } catch (Exception e) {
            Log.d(TAG, "FAILED TO SAVED: 2222 ===> " + e.getMessage());
        }

    }

    String temp_country = "";
    String temp_country_1 = "";
    private FirebaseAuth mAuth;


    private void fetch_form_data() {

        if (!phone_code_is_set) {
            showStateDialog(country_view);
            return;
        }

        if (phoneNumberModel.phone_number.length() < 4) {
            Toast.makeText(context, R.string.phone_numer_too_short, Toast.LENGTH_SHORT).show();
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.confirm_this_is_your_number);
        builder.setTitle(phoneNumberModel.phone_number + "");
        builder.setPositiveButton(R.string.confirm_number, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //test_verification_code();
                request_code();
            }
        });
        builder.show();
    }

    PhoneNumberModel phoneNumberModel = new PhoneNumberModel();

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vérification du numéro de téléphone");
        Tools.setSystemBarColor(this);
    }


}