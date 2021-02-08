package gonevas.com.activity.section;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.activity.CategoryActivity;
import gonevas.com.adapter.ProductAdapter;
import gonevas.com.adapter.ProductCategoryAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.ProductCategory;

public class CategorySection {

    Context context;
    View root;
    DatabaseRepository databaseRepository;

    RecyclerView categories_rec_view;
    List<ProductCategory> productCategories = new ArrayList<>();
    private ProductAdapter justForYouAdapter;
    private ProductCategoryAdapter productCategoryAdapter;


    public CategorySection(Context context, View root, DatabaseRepository databaseRepository) {
        this.context = context;
        this.root = root;
        this.databaseRepository = databaseRepository;
        bind_views();
    }


    private void bind_views() {
        categories_rec_view = root.findViewById(R.id.categories_rec_view);
        databaseRepository.dbInterface.get_categories().observe((LifecycleOwner) context, new Observer<List<ProductCategory>>() {
            @Override
            public void onChanged(List<ProductCategory> categories) {
                if (categories == null) {
                    Toast.makeText(context, "Categories are null.", Toast.LENGTH_SHORT).show();
                    return;
                }
                productCategories = categories;
                feed_data();
            }
        });


    }


    public void feed_data() {

        if (productCategories.size() > 1) {
            productCategoryAdapter = new ProductCategoryAdapter(context, productCategories, 0);
            categories_rec_view.setHasFixedSize(true);
            categories_rec_view.setLayoutManager(new LinearLayoutManager(context));
            //categories_rec_view.setLayoutManager(new GridLayoutManager(context, 4));
            categories_rec_view.setAdapter(productCategoryAdapter);

            productCategoryAdapter.setOnItemClickListener(new ProductCategoryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, ProductCategory obj, int position) {

                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("cat_id", obj.id + "");
                    context.startActivity(intent);

                    /*Intent intent = new Intent(context, SearchResultsActivity.class);
                    intent.putExtra("category", obj.ad_id);
                    context.startActivity(intent);*/

                }
            });
        } else {
            Toast.makeText(context, "Product categories are empty", Toast.LENGTH_SHORT).show();
        }

    }


}

