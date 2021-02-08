package gonevas.com.activity.section;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.activity.CategoryActivity;
import gonevas.com.activity.ProductActivity;
import gonevas.com.activity.SearchResultsActivity;
import gonevas.com.adapter.AdsAdapter;
import gonevas.com.adapter.ProductAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.Ad;
import gonevas.com.model.Product;

public class HomeSection {

    Context context;
    View root;
    DatabaseRepository databaseRepository;
    List<Product> products;
    RecyclerView just_for_you_view_RecyclerView;
    RecyclerView ads_recyclerView;
    List<Ad> category_ads = new ArrayList<>();
    List<Ad> ads = new ArrayList<>();
    ImageView ad_15;
    ImageView ad_16;
    ImageView ad_17;
    ImageView ad_18;
    ImageView ad_19;
    ImageView ad_20;
    ImageView ad_21;
    ImageView ad_22;
    ImageView ad_23;
    ImageView ad_24;
    TextView ad_text_17;
    TextView ad_text_16;
    TextView ad_text_15;
    TextView temp_ad_text;
    private ProductAdapter justForYouAdapter;
    private AdsAdapter adsAdapter;

    public HomeSection(Context context, View root, DatabaseRepository databaseRepository, List<Product> ps, List<Ad> ads) {
        this.context = context;
        this.products = ps;
        this.root = root;
        this.databaseRepository = databaseRepository;
        this.ads = ads;
        bind_views();
    }

    private void bind_views() {
        just_for_you_view_RecyclerView = root.findViewById(R.id.just_for_you_view_RecyclerView);
        just_for_you_view_RecyclerView.setNestedScrollingEnabled(false);
        ads_recyclerView = root.findViewById(R.id.ads_recyclerView);
        ad_text_17 = root.findViewById(R.id.ad_text_17);
        ad_16 = root.findViewById(R.id.ad_16);
        temp_ad_text = root.findViewById(R.id.temp_ad_text);
        ad_text_16 = root.findViewById(R.id.ad_text_16);
        ad_15 = root.findViewById(R.id.ad_15);
        ad_16 = root.findViewById(R.id.ad_16);
        ad_24 = root.findViewById(R.id.ad_24);
        ad_21 = root.findViewById(R.id.ad_21);
        ad_22 = root.findViewById(R.id.ad_22);
        ad_23 = root.findViewById(R.id.ad_23);
        ad_17 = root.findViewById(R.id.ad_17);
        ad_18 = root.findViewById(R.id.ad_18);
        ad_19 = root.findViewById(R.id.ad_19);
        ad_20 = root.findViewById(R.id.ad_20);
        init_category_section();
        init_just_for_you();
    }


    public void init() {

    }

    private void load_ad(final Ad ad, ImageView imageView, TextView textView) {
        Glide.with(context)
                .load(ad.photo)
                .fitCenter()
                .placeholder(R.drawable.doodle_horizontal)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("ad_id", ad.ad_id);
                context.startActivity(intent);
            }
        });
    }

    private void init_category_section() {

        for (Ad ad : ads) {
            if (
                    ad.position == 6 ||
                            ad.position == 7 ||
                            ad.position == 8 ||
                            ad.position == 9 ||
                            ad.position == 10 ||
                            ad.position == 11 ||
                            ad.position == 12 ||
                            ad.position == 13
            ) {
                category_ads.add(ad);
            } else if (ad.position == 14) {
                load_ad(ad, ad_15, ad_text_15);
            } else if (ad.position == 15) {
                load_ad(ad, ad_16, ad_text_16);
            } else if (ad.position == 16) {
                load_ad(ad, ad_17, ad_text_17);
            } else if (ad.position == 17) {
                load_ad(ad, ad_18, temp_ad_text);
            } else if (ad.position == 18) {
                load_ad(ad, ad_19, temp_ad_text);
            } else if (ad.position == 19) {
                load_ad(ad, ad_20, temp_ad_text);
            } else if (ad.position == 20) {
                load_ad(ad, ad_21, temp_ad_text);
            } else if (ad.position == 21) {
                load_ad(ad, ad_22, temp_ad_text);
            } else if (ad.position == 22) {
                load_ad(ad, ad_23, temp_ad_text);
            } else if (ad.position == 23) {
                load_ad(ad, ad_24, temp_ad_text);
            }

        }

        if (category_ads.size() > 1) {
            adsAdapter = new AdsAdapter(context, category_ads, 1);
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
        } else {
            ads_recyclerView.setVisibility(View.GONE);
        }


    }

    private void init_just_for_you() {

        justForYouAdapter = new ProductAdapter(context, products, 1);

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


    }
}

