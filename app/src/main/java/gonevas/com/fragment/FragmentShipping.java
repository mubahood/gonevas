package gonevas.com.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import gonevas.com.R;
import gonevas.com.model.UserModel;

public class FragmentShipping extends Fragment {

    UserModel userModel;

    public FragmentShipping(UserModel userModel) {
        this.userModel = userModel;
    }

    View root;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_shipping, container, false);
        bind_views();
        return root;
    }

    EditText ship_name,extra_message, ship_city, ship_address, ship_state, cont_phone, cont_email, ship_last_name, cont_name;

    void bind_views() {
        cont_name = root.findViewById(R.id.cont_name);
        cont_email = root.findViewById(R.id.cont_email);
        ship_last_name = root.findViewById(R.id.ship_last_name);
        ship_name = root.findViewById(R.id.ship_name);
        cont_phone = root.findViewById(R.id.cont_phone);
        ship_address = root.findViewById(R.id.ship_address);
        extra_message = root.findViewById(R.id.extra_message);
        ship_city = root.findViewById(R.id.ship_city);
        ship_state = root.findViewById(R.id.ship_state);
        feed_data();
    }

    void feed_data() {
        cont_name.setText(userModel.first_name + " " + userModel.last_name);
        cont_email.setText(userModel.email);
        cont_phone.setText(userModel.billing.phone);

        ship_name.setText(userModel.first_name);
        ship_last_name.setText(userModel.last_name);
        ship_state.setText(userModel.shipping.state);
        ship_city.setText(userModel.shipping.city);
        ship_address.setText(userModel.shipping.address_1);
    }

    public UserModel get_user() {

        userModel.shipping.first_name = ship_name.getText().toString();
        userModel.shipping.last_name = ship_last_name.getText().toString();
        userModel.shipping.state = ship_state.getText().toString();
        userModel.shipping.city = ship_city.getText().toString();
        userModel.shipping.address_1 = ship_address.getText().toString();
        userModel.shipping.address_2 = extra_message.getText().toString();

        return userModel;
    }

}