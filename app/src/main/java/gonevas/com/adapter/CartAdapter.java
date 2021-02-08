package gonevas.com.adapter;


import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.CartModel;
import gonevas.com.utils.MyFunctions;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ANJANE_CartAdapter";
    int style = 0;
    MyFunctions functions;
    DatabaseRepository databaseRepository;
    int count = 0;
    private List<CartModel> items = new ArrayList<>();
    private final Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public CartAdapter(Context context, List<CartModel> items, DatabaseRepository databaseRepository, int style) {
        this.items = items;
        this.ctx = context;
        this.databaseRepository = databaseRepository;
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
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_1, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_1, parent, false);
            }
        } else if (style == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_1, parent, false);


        } else if (style == 2) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_1, parent, false);


        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_1, parent, false);
        }


        count++;
        vh = new OriginalViewHolder(v);


        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;


            final CartModel p = items.get(position);

            if (p.is_variable) {
                Log.d(TAG, "onChanged: variation_thing  =====>  " + p.selected_variable_id);
                view.title.setText(
                        Html.fromHtml(p.product.name).toString() + " (" +
                                p.variable_text +
                                ")"
                );
            } else {
                view.title.setText(Html.fromHtml(p.product.name).toString());
            }

            view.cart_quantity.setText(p.quantity + "");


            if (p.product.cart_price.length() > 0) {
                try {
                    view.product_price.setText(functions.toMoneyFormat(Double.valueOf(p.product.cart_price + "")) + " GMD");
                } catch (Exception e) {

                }
            }


            DrawableCrossFadeFactory factory =
                    new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

            Glide.with(ctx)
                    .load(p.product.image_full)
                    .placeholder(R.drawable.doodle_1)
                    .transition(withCrossFade(factory))
                    .centerCrop()
                    .into(view.image);

            //Tools.displayImageRound(ctx, view.image, p.image);


            view.cart_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (p.quantity > 1) {
                        p.quantity--;
                    } else {
                        p.quantity = 1;
                    }

                    databaseRepository.update_to_cart(p);
                    Log.d(TAG, "ANJANE_CartAdapter => UPDATING ==> : " + p.quantity);

                    ((OriginalViewHolder) holder).cart_quantity.setText(p.quantity + "");

                    mOnItemClickListener.onItemClick(view, items.get(position), position, false);
                }
            });

            view.cart_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    p.quantity++;
                    databaseRepository.update_to_cart(p);
                    ((OriginalViewHolder) holder).cart_quantity.setText(p.quantity + "");
                    mOnItemClickListener.onItemClick(view, items.get(position), position, false);
                }
            });

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position, true);
                        //items.remove(items.get(position));
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
        void onItemClick(View view, CartModel obj, int position, boolean isRemoveProduct);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView cart_location;
        public TextView product_price;
        public TextView cart_quantity;
        public ImageButton lyt_parent;
        public ImageView cart_increase, cart_decrease;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.cart_image);
            cart_increase = v.findViewById(R.id.cart_increase);
            cart_decrease = v.findViewById(R.id.cart_decrease);
            cart_location = v.findViewById(R.id.cart_location);
            cart_quantity = v.findViewById(R.id.cart_quantity);
            title = v.findViewById(R.id.cart_title);
            product_price = v.findViewById(R.id.cart_price);
            lyt_parent = v.findViewById(R.id.cart_parent_view);
        }
    }

}