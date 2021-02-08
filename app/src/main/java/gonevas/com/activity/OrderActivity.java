package gonevas.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.adapter.OrderItemsAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.Order;
import gonevas.com.model.OrderLineItems;
import gonevas.com.utils.MyFunctions;
import gonevas.com.utils.Tools;

public class OrderActivity extends AppCompatActivity {

    OrderActivity context;
    String id = "";
    Order order = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        context = OrderActivity.this;
        Intent intent = getIntent();
        try {
            id = intent.getStringExtra("id");
        } catch (Exception e) {
            finish();
            return;
        }
        databaseRepository = new DatabaseRepository(context);
        get_product_from_db();
    }


    DatabaseRepository databaseRepository;

    private void get_product_from_db() {

        databaseRepository.getOrdersById(id).observe(context, new Observer<Order>() {
            @Override
            public void onChanged(Order o) {
                if (o == null) {
                    Toast.makeText(context, "Order not found.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                order = o;
                feed_data();
                initToolbar("ORDER #" + o.order_id);
            }
        });


    }

    RecyclerView recyc_orders;
    OrderItemsAdapter ordersAdapter;
    MyFunctions functions;

    List<OrderLineItems> orderLineItems = new ArrayList<>();

    private void feed_data() {
        order.from_json();


        ordersAdapter = new OrderItemsAdapter(context, order.line_items, 1);
        context.recyc_orders = context.findViewById(R.id.recyc_orders);
        recyc_orders.setHasFixedSize(true);
        recyc_orders.setLayoutManager(new LinearLayoutManager(context));
        recyc_orders.setAdapter(ordersAdapter);

        context.functions = new MyFunctions(context);
        context.shipping_total = context.findViewById(R.id.shipping_total);
        context.order_total = context.findViewById(R.id.order_total);

        context.shipping_total.setText(order.shipping_total + "");
        try {
            context.order_total.setText(functions.toMoneyFormat(order.total) + " CFA");
        } catch (Exception e) {

        }


    }

    private static final String TAG = "MUHINDO";
    TextView shipping_total;
    TextView order_total;

    private void initToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}