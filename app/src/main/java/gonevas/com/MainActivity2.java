package gonevas.com;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.activity.CartActivity;
import gonevas.com.activity.ProductActivity;
import gonevas.com.activity.SearchFormActivity;
import gonevas.com.activity.SearchResultsActivity;
import gonevas.com.activity.section.AccountSection;
import gonevas.com.activity.section.CategorySection;
import gonevas.com.adapter.ProductCategoryAdapter;
import gonevas.com.adapter.SearchResultsAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.fragment.FragmentHomeSection;
import gonevas.com.fragment.FragmentHomeSection1;
import gonevas.com.fragment.FragmentHomeSection2;
import gonevas.com.fragment.FragmentHomeSection3;
import gonevas.com.model.ProductCategory;
import gonevas.com.model.SearchResults;
import gonevas.com.model.SliderModel;

public class MainActivity2 extends AppCompatActivity {


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    boolean isHome_created = false;

    FragmentHomeSection home_fragment;
    FragmentHomeSection1 women_fragment;
    FragmentHomeSection2 men_fragment;
    FragmentHomeSection3 more_fragment;
    LinearLayout data_policy;

    private void prepare_home_fragments() {
        SectionsPagerAdapter adapter;
        ViewPager view_pager;
        TabLayout top_tabs;
        top_tabs = findViewById(R.id.top_tabs);
        view_pager = findViewById(R.id.view_pager);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(men_fragment.newInstance("Maison"), "Maison");
        adapter.addFragment(women_fragment.newInstance("Femme"), "Femme");
        adapter.addFragment(home_fragment.newInstance("Homme"), "Homme");
        adapter.addFragment(more_fragment.newInstance("Deal"), "Deal");


/*        adapter.addFragment(FragmentHomeSection.newInstance(), "WOMEN");
        adapter.addFragment(FragmentHomeSection.newInstance(), "MEN");
        adapter.addFragment(FragmentHomeSection.newInstance(), "ELECTRONICS");*/
        view_pager.setOffscreenPageLimit(10);
        view_pager.setAdapter(adapter);
        top_tabs.setupWithViewPager(view_pager);
    }


    BottomNavigationView navView;
    MainActivity2 context;
    TextView search_box;

    TextView cart_count_view;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProductCategoryAdapter productCategoryAdapter;
    List<ProductCategory> productCategories = new ArrayList<>();
    CoordinatorLayout section_home;
    LinearLayout section_categories, section_cart, section_search, section_account;
    SliderModel sliderModel = new SliderModel();
    CardView cart_container;
    String search_word = "";
    RecyclerView categories_rec_view;
    LinearLayout main_loader;
    RecyclerView search_suggestions;
    boolean home_created = false;
    AccountSection accountSection = null;
    boolean search_initialized = false;
    SearchResultsAdapter searchResultsAdapter;
    List<String> searchResults = new ArrayList<>();
    private ActionBar actionBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        context = MainActivity2.this;
        databaseRepository = new DatabaseRepository(context);
        bind_views();


    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Voulez-vous vraiment quitter l'application GONEVA'S?");

        builder.setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });

        builder.setPositiveButton("QUITTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, "À plus tard.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        builder.show();

    }

    public void open_product_activity(View v) {
        Intent i = new Intent(context, ProductActivity.class);
        context.startActivity(i);
    }

    LinearLayout open_website, open_dialer;

    private void bind_views() {


        main_loader = findViewById(R.id.main_loader);
        main_loader.setVisibility(View.VISIBLE);
        navView = findViewById(R.id.nav_view);
        cart_container = findViewById(R.id.cart_container);
        cart_count_view = findViewById(R.id.cart_count_view);
        section_home = findViewById(R.id.section_home);
        section_categories = findViewById(R.id.section_categories);
        categories_rec_view = findViewById(R.id.categories_rec_view);
        section_search = findViewById(R.id.section_search);
        section_cart = findViewById(R.id.section_cart);
        section_account = findViewById(R.id.section_account);
        search_box = findViewById(R.id.search_box);
        search_suggestions = findViewById(R.id.search_suggestions);
        data_policy = findViewById(R.id.data_policy);
        open_website = findViewById(R.id.open_website);
        open_dialer = findViewById(R.id.open_dialer);
        show_home();


        open_dialer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:+22549555445"));
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });
        data_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.gonevas.com"));
                    startActivity(browserIntent);
                } catch (Exception e) {

                }
            }
        });

        open_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.gonevas.com"));
                    startActivity(browserIntent);
                } catch (Exception e) {

                }
            }
        });

        search_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_search();
            }
        });

        initToolbar();


        cart_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CartActivity.class);
                context.startActivity(i);
            }
        });


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Toast.makeText(context, "Clicked on ==> " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_account:
                        show_account();
                        break;
                    case R.id.navigation_home:
                        show_home();
                        break;
                    case R.id.navigation_categories:
                        show_categories();
                        break;
                    case R.id.navigation_search:
                        show_search();
                        break;
                    case R.id.navigation_cart:
                        Intent i = new Intent(context, CartActivity.class);
                        context.startActivity(i);
                        return false;
                    //show_Cart();

                }
                return true;
            }
        });

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setElevation(0);

        //Tools.setSystemBarColor(this);

        //getSupportActionBar().setElevation(0);

    }


    private void show_home() {

        if (!isHome_created) {
            isHome_created = true;
            prepare_home_fragments();
        }

        search_box.clearFocus();
        section_home.setVisibility(View.VISIBLE);
        section_categories.setVisibility(View.GONE);
        main_loader.setVisibility(View.GONE);
        section_search.setVisibility(View.GONE);
        section_cart.setVisibility(View.GONE);
        section_account.setVisibility(View.GONE);
    }

    CategorySection categorySection = null;
    DatabaseRepository databaseRepository;

    private void show_categories() {

        if (categorySection == null) {
            categorySection = new CategorySection(context, section_categories, databaseRepository);
        }
        search_box.clearFocus();
        section_home.setVisibility(View.GONE);
        section_categories.setVisibility(View.VISIBLE);
        section_search.setVisibility(View.GONE);
        section_cart.setVisibility(View.GONE);
        section_account.setVisibility(View.GONE);
    }


    private void show_search() {

        Intent intent = new Intent(context, SearchFormActivity.class);
        context.startActivity(intent);
        if (true)
            return;


        section_home.setVisibility(View.GONE);
        section_categories.setVisibility(View.GONE);
        section_search.setVisibility(View.VISIBLE);
        section_cart.setVisibility(View.GONE);
        section_account.setVisibility(View.GONE);
        search_box.requestFocus();


        if (!search_initialized) {

            databaseRepository.get_search_results().observe(context, new Observer<List<SearchResults>>() {
                @Override
                public void onChanged(List<SearchResults> dbSearchResults) {
                    if (dbSearchResults.size() > 0) {


                        if (!dbSearchResults.isEmpty()) {
                            for (SearchResults search : dbSearchResults) {
                                if (!searchResults.contains(search.keyword.toLowerCase())) {
                                    searchResults.add(search.keyword.toLowerCase());
                                }
                            }

                            searchResultsAdapter = new SearchResultsAdapter(context, searchResults, 1);
                            search_suggestions.setHasFixedSize(true);
                            search_suggestions.setLayoutManager(new LinearLayoutManager(context));
                            search_suggestions.setAdapter(searchResultsAdapter);

                            searchResultsAdapter.setOnItemClickListener(new SearchResultsAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, String obj, int position) {
                                    Intent intent = new Intent(context, SearchResultsActivity.class);
                                    intent.putExtra("search", obj + "");
                                    context.startActivity(intent);
                                }
                            });


                        }


                    } else {

                    }
                }
            });


        }


        //navView.setSelectedItemId(3);
    }

    private void show_Cart() {
        search_box.clearFocus();
        section_home.setVisibility(View.GONE);
        section_categories.setVisibility(View.GONE);
        section_search.setVisibility(View.GONE);
        section_cart.setVisibility(View.VISIBLE);
        section_account.setVisibility(View.GONE);
    }

    private void show_account() {
        //bt_create_account
        if (accountSection == null) {
            accountSection = new AccountSection(context, section_account, databaseRepository);
        }

        //navView.setSelectedItemId(R.id.navigation_search);
        section_home.setVisibility(View.GONE);
        section_categories.setVisibility(View.GONE);
        section_search.setVisibility(View.GONE);
        section_cart.setVisibility(View.GONE);
        section_account.setVisibility(View.VISIBLE);
    }

}