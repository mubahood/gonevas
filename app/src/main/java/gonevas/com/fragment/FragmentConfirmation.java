package gonevas.com.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.model.CartModel;
import gonevas.com.model.UserModel;
import gonevas.com.utils.MyFunctions;

public class FragmentConfirmation extends Fragment {

    UserModel userModel = new UserModel();
    MyFunctions functions;
    List<CartModel> loadedProducts = new ArrayList<>();

    public FragmentConfirmation(UserModel userModel, List<CartModel> loadedProducts, MyFunctions functions) {
        this.userModel = userModel;
        this.loadedProducts = loadedProducts;
        this.functions = functions;
    }

    TextView shipping_to, order_total_view, shipping_price_view, product_cost_view;

    void bind_views() {
        shipping_to = root.findViewById(R.id.shipping_to);
        shipping_price_view = root.findViewById(R.id.shipping_price_view);
        order_total_view = root.findViewById(R.id.order_total_view);
        product_cost_view = root.findViewById(R.id.product_cost_view);
    }

    private static final String TAG = "mubahooooooooooooood";
    Integer products_cost = 0;

    void feed_data() {
        shipping_to.setText(userModel.shipping.first_name + " " + userModel.shipping.last_name + ", " +
                userModel.shipping.state + ",\n" + userModel.shipping.city + ", " + userModel.shipping.address_1 + ".\n" + userModel.billing.phone);
        products_cost = 0;
        for (CartModel c : loadedProducts) {

            if (c.product.cart_price == null) {
                Log.d(TAG, "feed_data: NULL ==> " + c.product.sale_price);
                sale_price = 0;
            } else if (c.product.sale_price.length() > 0) {
                Log.d(TAG, "feed_data: NOT NULL ==> " + c.product.sale_price);
                try {
                    sale_price = Integer.valueOf(c.product.cart_price);
                } catch (Exception e) {
                    sale_price = 0;
                }
            } else {
                sale_price = 0;
                Log.d(TAG, "feed_data: NOTING... ==> " + c.product.sale_price);
            }

            if (sale_price == 0) {
                if (c.product.cart_price == null) {
                    sale_price = 0;
                } else if (c.product.cart_price.length() > 0) {
                    Log.d(TAG, "feed_data: NOT NULL ==> " + c.product.regular_price);
                    try {
                        sale_price = Integer.valueOf(c.product.cart_price);
                    } catch (Exception e) {
                        sale_price = 0;
                    }
                } else {
                    sale_price = 0;
                    Log.d(TAG, "feed_data: NOTING... ==> " + c.product.cart_price);
                }

            }

            Log.d(TAG, "feed_data: ==> " + c.quantity + " ==> " + sale_price + "  ==>\n\n NAME: " + c.product.name + "" +
                    "\nregular_price: " + c.product.regular_price + "" +
                    "\n" + "price_html: " + c.product.price_html +
                    "\n" + "price: " + c.product.cart_price + "" +
                    "\n\n\n\n");
            products_cost += ((c.quantity) * (sale_price));
        }

        shipping_price_view.setText(shipping_price + " CFA");
        product_cost_view.setText(functions.toMoneyFormat(products_cost) + " CFA");
        order_total_view.setText(functions.toMoneyFormat(products_cost + shipping_price) + " CFA");
    }

    int sale_price = 0;
    int shipping_price = 120;


    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_confirmation, container, false);
        bind_views();
        feed_data();
        return root;
    }
}