package gonevas.com.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gonevas.com.R;
import gonevas.com.adapter.AttributePropertyAdapter;
import gonevas.com.adapter.ProductAdapter;
import gonevas.com.adapter.SliderAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.AttributeProperty;
import gonevas.com.model.CartModel;
import gonevas.com.model.Image;
import gonevas.com.model.Product;
import gonevas.com.model.ProductAttribute;
import gonevas.com.model.SliderModel;
import gonevas.com.model.Variable;
import gonevas.com.utils.MyFunctions;

public class ProductActivity extends AppCompatActivity {
    private static final String TAG = "ANJANEAA";
    private final String full_image = "full_image";
    private final ArrayList<String> stack = new ArrayList<>();
    private final String[] VARIABLES_IDS = new String[]{};
    public List<Product> relatedProducts = new ArrayList<>();
    public List<CartModel> cart = new ArrayList<>();
    Context context;
    String current_product_id = "";
    Product current_product = null;
    DatabaseRepository databaseRepository;
    MyFunctions functions;
    FrameLayout full_image_container;
    LinearLayout add_to_cart_container, add_to_cart_inner_container, show_product_description_view, check_out_container;
    Button checkout_button_container;
    NestedScrollView details_container;
    ImageButton close_full_image;
    TextView discount_view, single_product_name;
    TextView single_product_price, percentage_off;
    RecyclerView related_products_recyclerView;
    List<AttributeProperty> properties = new ArrayList<AttributeProperty>();
    String option = "";
    int counter = 0;
    RecyclerView attribute_properties_recyclerView;
    String last_stack_item = "";
    SliderModel sliderModel = new SliderModel();
    CartModel cartModel = new CartModel();
    int my_counter = 0;
    String temp_string = "";
    Button button_add_to_cart;
    boolean pre_pared = false;
    int VARIABLES_COUNTER = 0;
    List<String> options = new ArrayList<>();
    List<String> option_ids = new ArrayList<>();
    int single_choice_selected = -1;
    boolean is_variable = false;
    private ProductAdapter relatedProductsAdapter;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private AttributePropertyAdapter attributePropertyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        context = this;
        functions = new MyFunctions(context);

        Intent intent = getIntent();


        try {
            current_product_id = intent.getStringExtra("id");
            Log.d(TAG, "onCreate: ==> " + current_product_id);
        } catch (Exception e) {
            Toast.makeText(context, "Product Not Selected. " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (current_product_id == null) {
            Toast.makeText(context, "Product not selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (current_product_id.length() < 1) {
            Toast.makeText(context, "Product not selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        databaseRepository = new DatabaseRepository(context);


    }

    @Override
    protected void onStart() {
        if (current_product_id == null) {
            Toast.makeText(context, "Product not set", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (current_product_id.length() < 1) {
            Toast.makeText(context, "Product not set", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        current_product = new Product();

        databaseRepository.dbInterface.get_product_by_id(current_product_id).observe((LifecycleOwner) context, new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                current_product = product;
                if (current_product == null) {
                    Toast.makeText(context, "Local product not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                current_product.init_product();

                bind_views();
                show_product_full_details();
                prepare_properties();
                feed_related_products();
            }
        });


        //bind_views();
        //show_product_full_details();
        super.onStart();
    }

    Button call_seller;

    private void bind_views() {


        single_product_name = findViewById(R.id.single_product_name);
        single_product_price = findViewById(R.id.single_product_price);
        check_out_container = findViewById(R.id.check_out_container);
        call_seller = findViewById(R.id.call_seller);

        percentage_off = findViewById(R.id.percentage_off);
        button_add_to_cart = findViewById(R.id.button_add_to_cart);

        full_image_container = findViewById(R.id.full_image_container);
        show_product_description_view = findViewById(R.id.show_product_description_view);
        add_to_cart_container = findViewById(R.id.add_to_cart_container);
        add_to_cart_inner_container = findViewById(R.id.add_to_cart_inner_container);
        details_container = findViewById(R.id.details_container);
        close_full_image = findViewById(R.id.close_full_image);
        discount_view = findViewById(R.id.discount_view);
        checkout_button_container = findViewById(R.id.checkout_button_container);

        checkout_button_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_cart_activity();
            }
        });

        button_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_to_cart_button(v, false);
            }
        });

        call_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_to_cart_button(v, true);
            }
        });

        related_products_recyclerView = findViewById(R.id.related_products_recyclerView);
        related_products_recyclerView.setVisibility(View.GONE);
        discount_view.setPaintFlags(discount_view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);


        close_full_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_product_full_details();
            }
        });
        show_product_description_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        get_product_from_db();

    }

    private void feed_related_products() {

        databaseRepository.getProducts().observe((LifecycleOwner) context, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {

                Collections.shuffle(products);
                Collections.shuffle(products);
                Collections.shuffle(products);

                for (Product p : products) {
                    if (my_counter > 25) {
                        break;
                    }
                    my_counter++;
                    if (!p.id.equals(current_product_id)) {
                        relatedProducts.add(p);
                    }
                }


                relatedProductsAdapter = new ProductAdapter(context, relatedProducts, 2);

                related_products_recyclerView.setHasFixedSize(true);
                related_products_recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                related_products_recyclerView.setAdapter(relatedProductsAdapter);

                related_products_recyclerView.setVisibility(View.VISIBLE);
                relatedProductsAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Product obj, int position) {
                        Intent i = new Intent(context, ProductActivity.class);
                        i.putExtra("id", obj.product_id);
                        context.startActivity(i);
                        //finish();
                        return;
                    }
                });


            }
        });


    }

    private void feed_current_product_data() {

        databaseRepository.getCart().observe((LifecycleOwner) context, new Observer<List<CartModel>>() {

            @Override
            public void onChanged(List<CartModel> cartModels) {
                cart = cartModels;
                for (CartModel c : cartModels) {
                    if (c.product_id.equals(current_product_id + "")) {
                        Log.d(TAG, "onChanged: CHECKTOUT_BTN 1");
                        View v = new View(context);
                        show_checkout_button(v);
                        break;
                    }
                }
            }
        });


        init_thumbnail_slider();
        single_product_name.setText(Html.fromHtml(current_product.name) + "");

        if (current_product.price.length() > 0) {
            single_product_price.setText(functions.toMoneyFormat(current_product.price) + " CFA");
        } else {
            single_product_price.setText("0.00 CFA");
        }

        discount_view.setText(functions.toMoneyFormat(current_product.price) + " CFA");
        percentage_off.setText(functions.get_percentage(current_product.price, current_product.regular_price) + "%OFF");


    }

    private void get_product_from_db() {
        feed_current_product_data();
    }

    private void prepare_properties() {
        if (pre_pared)
            return;
        pre_pared = true;
        final View sheet_list_product_view = getLayoutInflater().inflate(R.layout.sheet_list_product, null);
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(sheet_list_product_view);

        properties.clear();
        attribute_properties_recyclerView = mBottomSheetDialog.findViewById(R.id.attribute_properties_recyclerView);
        properties.add(new AttributeProperty("ID", current_product.product_id + ""));
        if (current_product.weight.length() > 0) {
            properties.add(new AttributeProperty("Weight", current_product.weight + " Kg"));
        }
        for (ProductAttribute attribute : current_product.attributes) {
            if (true) {
                if (!attribute.options.isEmpty()) {
                    counter = 0;
                    option = "";
                    for (String d : attribute.options) {
                        if (counter == 0) {
                            option += d;
                        } else {
                            option += ", " + d;
                        }
                        counter++;
                    }

                    properties.add(new AttributeProperty(attribute.name, option + ""));
                }
            }
        }

        if (current_product.type.equals("variable")) {
            if (!current_product.variations.isEmpty()) {
                for (Variable variable : current_product.variations) {
                    temp_string = "";
                    for (AttributeProperty attributeProperty : variable.attributes) {
                        if (temp_string.length() < 1) {
                            temp_string = attributeProperty.name + ": " + attributeProperty.value;
                        } else {
                            temp_string = ", " + attributeProperty.name + ": " + attributeProperty.value;
                        }
                    }

                    if (variable.selling_price.length() > 0) {
                        try {
                            properties.add(new AttributeProperty(temp_string, functions.toMoneyFormat(variable.selling_price) + " CFA"));
                            is_variable = true;

                            options.add(temp_string + ": " + functions.toMoneyFormat(variable.selling_price) + " CFA");
                            option_ids.add(variable.variation_id);
                            Log.d(TAG, "prepare_properties: =========> " + options.size());
                            VARIABLES_COUNTER++;
                        } catch (Exception e) {
                            Log.d(TAG, "prepare_properties: FAILED TO ADD BECAUSE " + e.getMessage());
                        }
                    }


                }
            } else {
                Log.d(TAG, "prepare_properties: thisngs are empty!");
            }
        }
        //properties.add(new AttributeProperty("\n\nDescription", current_product.short_description));
        if (current_product.description.length() > 2) {
            properties.add(new AttributeProperty("\n\nDETAILS", current_product.description));
        }
        attributePropertyAdapter = new AttributePropertyAdapter(context, properties, 1);
        attribute_properties_recyclerView.setHasFixedSize(true);
        attribute_properties_recyclerView.setLayoutManager(new LinearLayoutManager(context));
        attribute_properties_recyclerView.setAdapter(attributePropertyAdapter);

    }

    private void showBottomSheetDialog() {


        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //mBottomSheetDialog = null;
            }
        });
    }

    private void show_product_full_details() {
        add_to_cart_container.setVisibility(View.VISIBLE);
        details_container.setVisibility(View.VISIBLE);
        full_image_container.setVisibility(View.GONE);
        stack.clear();
    }

    private void show_full_image(int position) {
        add_to_cart_container.setVisibility(View.GONE);
        details_container.setVisibility(View.GONE);
        full_image_container.setVisibility(View.VISIBLE);

        stack.add(full_image);
        init_full_image(position);
    }

    @Override
    public void onBackPressed() {
        last_stack_item = "";
        if (!stack.isEmpty()) {
            last_stack_item = stack.get((stack.size() - 1));

            if (last_stack_item.equals(full_image)) {
                Log.d(TAG, "onBackPressed: ==> " + (stack.size() - 1));
                stack.remove((stack.size() - 1));
                show_product_full_details();
                return;
            }

        }

        super.onBackPressed();
    }

    private void init_full_image(int start_position) {

        SliderView sliderView;
        sliderView = findViewById(R.id.full_image_slider);
        SliderAdapter adapter;
        adapter = new SliderAdapter(context, 3);

        for (Image image : current_product.images) {
            sliderModel = new SliderModel();
            sliderModel.image_url = image.full;
            adapter.addItem(sliderModel);
        }


        sliderView.setIndicatorEnabled(false);
        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setInfiniteAdapterEnabled(false);
        sliderView.setAutoCycle(false);

        if (start_position > 0 && start_position < current_product.images.size()) {
            sliderView.setCurrentPagePosition(start_position);
        }


        adapter.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SliderModel SliderModel, int position) {
                //Toast.makeText(context, "Clicked on ==> " + sliderModel.image_url, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void init_thumbnail_slider() {

        SliderView sliderView;
        sliderView = findViewById(R.id.imageSlider);
        SliderAdapter adapter;
        adapter = new SliderAdapter(context, 2);

        for (Image image : current_product.images) {
            sliderModel = new SliderModel();
            sliderModel.image_url = image.full;

            adapter.addItem(sliderModel);
        }


        sliderView.setIndicatorEnabled(false);
        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setInfiniteAdapterEnabled(false);
        adapter.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SliderModel sliderModel, int position) {
                show_full_image(position);
                //Toast.makeText(context, "Clicked on ==> " + sliderModel.image_url, Toast.LENGTH_SHORT).show();
            }

        });


    }


    public void open_cart_activity() {
        Intent i = new Intent(context, CartActivity.class);
        context.startActivity(i);
    }

    void openWhatsappContact(String number, String msg) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://wa.me/" + number + "?text=" + msg
        ));
        startActivity(browserIntent);

    }

    public void open_whatsapp(View view) {


        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        String text = "Bonjour, je vous ai contacté depuis l'application " +
                "GENEVA'S à propos de ce produit" +
                "\n*NOM: * " + current_product.name +
                "\n*ID:* " + current_product.product_id +
                "\n*PRIX:* " + functions.toMoneyFormat(current_product.price) +
                "\n\n.";

        openWhatsappContact("+22549555445", text);

    }

    private void showSingleChoiceDialog(final View _view, final boolean is_buy_now) {
        final String[] VARIABLES = options.toArray(new String[0]);

        if (VARIABLES == null) {
            Toast.makeText(context, "VARIABLES is null", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "showSingleChoiceDialog: ==> " + VARIABLES.length);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionner");
        builder.setSingleChoiceItems(VARIABLES, single_choice_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                single_choice_selected = which;
            }
        });

        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (single_choice_selected < 0 || single_choice_selected >= option_ids.size()) {
                    Toast.makeText(context, "Vous n'avez sélectionné aucun choix.", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    cartModel = new CartModel();
                    cartModel.product_id = current_product_id;
                    cartModel.product = current_product;
                    cartModel.quantity = 1;
                    cartModel.is_variable = true;
                    cartModel.selected_variable_id = option_ids.get(single_choice_selected);
                    cartModel.variable_text = options.get(single_choice_selected);

                    current_product.cart_price = current_product.variations.get(single_choice_selected).selling_price + "";

                    try {
                        databaseRepository.update_product(current_product, false);
                    } catch (Exception e) {

                    }

                    databaseRepository.add_to_cart(cartModel);
                    show_checkout_button(_view);

                    if (is_buy_now) {
                        open_cart_activity();
                        return;
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onClick__: FAILED BCAUSE ===> " + e.getMessage());
                    Log.d(TAG, "onClick__: FAILED BCAUSE ===> " + e.getCause());
                    Toast.makeText(context, "Failed because " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }

                // Toast.makeText(context, "Selected " + option_ids.get(i), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }

    public void show_checkout_button(View view) {
        Log.d(TAG, "show_checkout_button: SHOWINT CART BTM ==< >> ");

        cartModel = new CartModel();
        cartModel.product_id = current_product_id;
        cartModel.quantity = 1;

        check_out_container.setVisibility(View.VISIBLE);
        checkout_button_container.setVisibility(View.VISIBLE);
        add_to_cart_inner_container.setVisibility(View.GONE);
    }

    public void add_to_cart_button(View view, boolean is_buy_now) {
        if (!pre_pared) {
            Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
            return;
        }
        if (is_variable) {
            showSingleChoiceDialog(view, is_buy_now);
        } else {
            try {
                cartModel = new CartModel();
                cartModel.product_id = current_product_id;
                cartModel.product = current_product;
                cartModel.quantity = 1;
                databaseRepository.add_to_cart(cartModel);
                show_checkout_button(view);
                if (is_buy_now) {
                    open_cart_activity();
                }

            } catch (Exception e) {

            }
        }

    }
}