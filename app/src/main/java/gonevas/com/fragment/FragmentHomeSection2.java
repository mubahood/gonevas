package gonevas.com.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gonevas.com.R;
import gonevas.com.activity.ProductActivity;
import gonevas.com.activity.SearchFormActivity;
import gonevas.com.activity.SearchResultsActivity;
import gonevas.com.adapter.AdsAdapter;
import gonevas.com.adapter.ProductAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.Ad;
import gonevas.com.model.Product;
import gonevas.com.model.ProductCategory;
import gonevas.com.model.ProductSectionModel;
import gonevas.com.utils.MyFunctions;
import gonevas.com.utils.Tools;

public class FragmentHomeSection2 extends Fragment {


    Context context;
    MyFunctions functions;

    static String fragment_1 = null;
    static String fragment_2 = null;
    static String fragment_3 = null;
    static String fragment_4 = null;

    boolean fragment_1_done = false;
    boolean fragment_2_done = false;
    boolean fragment_3_done = false;
    boolean fragment_4_done = false;


    public static Fragment newInstance(String name) {
        if (fragment_1 == null) {
            fragment_1 = name;
        } else if (fragment_2 == null) {
            fragment_2 = name;
        } else if (fragment_3 == null) {
            fragment_3 = name;
        } else if (fragment_4 == null) {
            fragment_4 = name;
        }
        FragmentHomeSection2 fragment = new FragmentHomeSection2();
        return fragment;
    }

    private static final String TAG = "FragmentVideos";
    ProductSectionModel testProductSectionModel = new ProductSectionModel();


    Tools tools;

    void start_ad_activity(Ad obj) {
        Intent intent = new Intent(context, SearchResultsActivity.class);
        intent.putExtra("category", obj.ad_id);
        context.startActivity(intent);

    }

    void start_product_activity(String obj) {
        Intent intent = new Intent(context, ProductActivity.class);
        intent.putExtra("id", obj);
        context.startActivity(intent);

    }

    void load_main_banner_0(final Ad ad, ImageView main_banner_0, TextView title) {
        tools.displayImageRemote(context, main_banner_0, ad.photo, R.drawable.loader_horizonal);
        if (title != null) {
            try {
                title.setText(ad.title.toString());
            } catch (Exception e) {

            }
        }
        main_banner_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_ad_activity(ad);
            }
        });

    }

    void load_main_banner_1(final Ad ad, ImageView main_banner_0, TextView title, TextView price, View view) {
        Log.d(TAG, "onClick: CLICKED ON SOME");
        tools.displayImageRemote(context, main_banner_0, ad.photo, R.drawable.lodaer_vertical);
        if (title != null) {
            try {
                title.setText(ad.title.toString());
            } catch (Exception e) {

            }
        }

        if (price != null) {
            try {
                if (ad.details.length() > 0) {
                    price.setText(functions.toMoneyFormat(ad.details) + " CFA");
                }
            } catch (Exception e) {
                price.setText("");
            }
        }

        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start_product_activity(ad.category);
                }
            });

        } else {
            main_banner_0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    start_product_activity(ad.category);
                }
            });
        }

    }

    View root;
    Ad temp_ad;
    List<Ad> ads = new ArrayList<>();
    List<Ad> temp_ads = new ArrayList<>();
    List<Ad> ads_categories = new ArrayList<>();
    List<Product> ready_products = new ArrayList<>();
    List<Product> products = new ArrayList<>();

    LinearLayout loader, _container;

    boolean prepared = false;
    DatabaseRepository databaseRepository;

    void prepare_data() {

        if (prepared) {
            return;
        }

        databaseRepository = new DatabaseRepository(context);
        databaseRepository.getAds().observe((LifecycleOwner) context, new Observer<List<Ad>>() {
            @Override
            public void onChanged(List<Ad> _ads) {
                databaseRepository.getAds().removeObservers((LifecycleOwner) context);
                ads = _ads;

                databaseRepository.getProducts().observe((LifecycleOwner) context, new Observer<List<Product>>() {
                    @Override
                    public void onChanged(List<Product> _products) {
                        if (_products == null) {
                            Toast.makeText(context, "No products found.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Collections.shuffle(_products);
                        Collections.shuffle(_products);

                        if (_products.size() > 300) {
                            products = _products.subList(0, 300);
                        } else {
                            products = _products;
                        }

                        databaseRepository.getProducts().removeObservers((LifecycleOwner) context);

                        temp_ads.clear();
                        for (Ad d : ads) {
                            temp_ad = null;
                            temp_ad = new Ad();


                            fragment_3_done = true;
                            if ((d.position >= 30) && (d.position <= 44)) {

                                temp_ad = null;
                                temp_ad = new Ad();
                                temp_ad = d;
                                temp_ad.position -= 30;
                                temp_ads.add(temp_ad);
                            }

                        }
                        init_content(temp_ads);
                    }
                });

            }
        });
        prepared = true;
    }


    String main_category = "";
    Button bt_search;

    void init_content(List<Ad> my__ads) {
        bt_search = root.findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchFormActivity.class);
                context.startActivity(intent);
            }
        });

        for (Ad d : my__ads) {
            if (d.position == 0) {
                main_category = d.category;
                load_main_banner_0(d, (ImageView) (root.findViewById(R.id.main_banner_0)), null);
            }
            if (d.position == 9) {
                load_main_banner_0(d, (ImageView) (root.findViewById(R.id.main_banner_1)), null);
            }

            if (d.position == 14) {
                load_main_banner_0(d, (ImageView) (root.findViewById(R.id.banner_7_parent)), null);
            }

            if (d.position == 11) {
                load_main_banner_1(
                        d,
                        (ImageView) (root.findViewById(R.id.banner_4_image)),
                        (TextView) (root.findViewById(R.id.banner_4_title)),
                        (TextView) (root.findViewById(R.id.banner_4_price)),
                        (View) (root.findViewById(R.id.banner_4_parent))
                );
                Log.d(TAG, "init_content: " + d.details);
            }
            if (d.position == 12) {
                load_main_banner_1(
                        d,
                        (ImageView) (root.findViewById(R.id.banner_5_image)),
                        (TextView) (root.findViewById(R.id.banner_5_title)),
                        (TextView) (root.findViewById(R.id.banner_5_price)),
                        (View) (root.findViewById(R.id.banner_5_parent))
                );
                Log.d(TAG, "init_content: " + d.details);
            }

            if (d.position == 13) {
                load_main_banner_1(
                        d,
                        (ImageView) (root.findViewById(R.id.banner_6_image)),
                        (TextView) (root.findViewById(R.id.banner_6_title)),
                        (TextView) (root.findViewById(R.id.banner_6_price)),
                        (View) (root.findViewById(R.id.banner_6_parent))
                );
            }

            if (d.position == 10) {
                load_main_banner_1(
                        d,
                        (ImageView) (root.findViewById(R.id.banner_3_image)),
                        (TextView) (root.findViewById(R.id.banner_3_title)),
                        (TextView) (root.findViewById(R.id.banner_3_price)),
                        (View) (root.findViewById(R.id.banner_3_parent))
                );
            }

            if (
                    d.position == 1 ||
                            d.position == 2 ||
                            d.position == 3 ||
                            d.position == 4 ||
                            d.position == 5 ||
                            d.position == 6 ||
                            d.position == 7 ||
                            d.position == 8
            ) {
                ads_categories.add(d);
            }
        }

        load_categories_section();
        init_just_for_you();

    }

    private ProductAdapter justForYouAdapter;
    RecyclerView just_for_you_view_RecyclerView;

    private void init_just_for_you() {

        if (main_category.length() < 0) {
            Toast.makeText(context, "Main category not set!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Product p : products) {
            p.init_product();

            if (p.categories == null) {
                Log.d(TAG, "init_just_for_you: cates are NULL ==> " + p.categories_json);
            } else {
                for (ProductCategory c : p.categories) {
                    if (c.id != null) {
                        if (c.id.toString().trim().equals(main_category.trim())) {
                            Log.d(TAG, "init_just_for_you: cates are ==> " + c.id);
                            ready_products.add(p);
                        }
                    }
                }
            }
        }

        if (ready_products.isEmpty()) {
            Toast.makeText(context, "No default products found. => " + products.size(), Toast.LENGTH_SHORT).show();
        }


        just_for_you_view_RecyclerView = root.findViewById(R.id.just_for_you_view_RecyclerView);
        justForYouAdapter = new ProductAdapter(context, ready_products, 4);


        just_for_you_view_RecyclerView.setHasFixedSize(true);
        just_for_you_view_RecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        just_for_you_view_RecyclerView.setNestedScrollingEnabled(false);
        just_for_you_view_RecyclerView.setAdapter(justForYouAdapter);

        justForYouAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Product obj, int position) {
                Intent i = new Intent(context, ProductActivity.class);
                i.putExtra("id", obj.product_id);
                context.startActivity(i);
                return;
            }
        });

        _container.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.home_section_fragment, container, false);

        loader = root.findViewById(R.id.loader);
        _container = root.findViewById(R.id.container);
        _container.setVisibility(View.GONE);

        context = getContext();
        tools = new Tools();
        functions = new MyFunctions(context);

        prepare_data();
        return root;
    }


    private AdsAdapter adsAdapter;

    private void load_categories_section() {


        if (ads_categories.size() > 1) {

            _container.setVisibility(View.VISIBLE);
            loader.setVisibility(View.GONE);

            adsAdapter = new AdsAdapter(context, ads_categories, 1);
            RecyclerView ads_recyclerView = root.findViewById(R.id.ads_recyclerView);
            ads_recyclerView.setHasFixedSize(true);
            ads_recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
            ads_recyclerView.setNestedScrollingEnabled(false);
            ads_recyclerView.setAdapter(adsAdapter);


            adsAdapter.setOnItemClickListener(new AdsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Ad obj, int position) {

                    Intent intent = new Intent(context, SearchResultsActivity.class);
                    intent.putExtra("category", obj.ad_id);
                    context.startActivity(intent);

                }
            });
        }
    }


}