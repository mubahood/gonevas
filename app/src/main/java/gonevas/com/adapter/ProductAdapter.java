package gonevas.com.adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.model.Product;
import gonevas.com.utils.MyFunctions;
import gonevas.com.utils.Tools;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int style = 0;
    MyFunctions functions;
    int count = 0;
    private List<Product> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public ProductAdapter(Context context, List<Product> items, int style) {
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
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_1, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_1, parent, false);
            }
        } else if (style == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_1, parent, false);


        } else if (style == 2) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_2, parent, false);


        } else if (style == 3) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_3, parent, false);


        } else if (style == 4) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_5, parent, false);


        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_1, parent, false);
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

            Product p = items.get(position);
            p.init_product();

            view.product_name.setText(Html.fromHtml(p.name).toString());
            if (p.price.length() > 0) {
                try {
                    view.product_price.setText(functions.toMoneyFormat(Double.valueOf(p.price + "")) + " CFA");
                } catch (Exception e) {

                }
            }


            Log.d("BINDING_", "onBindViewHolder: ===> " + p.image_full);
            if (p.image_full != null)
                if (p.image_full.length() > 2) {
                    Tools.displayImageRemote(ctx, view.product_image, p.image_full, R.drawable.lodaer_vertical);
                }

            //Tools.displayImageRound(ctx, view.image, p.image);


            view.product_parent_view.setOnClickListener(new View.OnClickListener() {
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
        void onItemClick(View view, Product obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView product_image;
        public TextView product_name;
        public TextView product_price;
        public CardView product_parent_view;

        public OriginalViewHolder(View v) {
            super(v);
            product_image = v.findViewById(R.id.product_image);
            product_name = v.findViewById(R.id.product_name);
            product_price = v.findViewById(R.id.product_price);
            product_parent_view = v.findViewById(R.id.product_parent_view);
        }
    }

}