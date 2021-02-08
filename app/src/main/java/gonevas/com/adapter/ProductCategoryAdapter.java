package gonevas.com.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.model.ProductCategory;
import gonevas.com.utils.MyFunctions;

public class ProductCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int style = 0;
    MyFunctions functions;
    int count = 0;
    private List<ProductCategory> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public ProductCategoryAdapter(Context context, List<ProductCategory> items, int style) {
        this.items = items;
        this.ctx = context;
        this.style = style;
        functions = new MyFunctions(ctx);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;
        if (style == 0) {
            if (count == 0) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_category1, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_category1, parent, false);
            }
        } else if (style == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_category1, parent, false);


        } else if (style == 2) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_category1, parent, false);


        } else if (style == 3) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_category1, parent, false);


        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_category1, parent, false);
        }


        count++;
        vh = new OriginalViewHolder(v);


        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            ProductCategory p = items.get(position);
            view.category_name.setText(Html.fromHtml(p.name).toString());
            view.category_count.setText("No Items");
            if (p.count != null) {
                try {
                    view.category_count.setText(functions.toMoneyFormat(Double.valueOf(p.count + "")) + " CFA");
                } catch (Exception e) {

                }
            }

            if (p.photo.length() > 2) {
                try {

                    Glide.with(ctx)
                            .load(p.photo)
                            .fitCenter()
                            .placeholder(R.drawable.doodle_1)
                            .into(view.category_image);
                } catch (Exception e) {

                }
            }


            //Tools.displayImageRound(ctx, view.image, p.image);

            view.category_parent_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ProductCategory obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView category_image;
        public TextView category_name;
        public TextView category_count;
        public MaterialRippleLayout category_parent_view;

        public OriginalViewHolder(View v) {
            super(v);
            category_image = v.findViewById(R.id.category_image);
            category_name = v.findViewById(R.id.category_name);
            category_count = v.findViewById(R.id.category_count);
            category_parent_view = v.findViewById(R.id.category_parent_view);
        }
    }

}