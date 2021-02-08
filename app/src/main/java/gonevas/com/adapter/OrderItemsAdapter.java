package gonevas.com.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.model.OrderLineItems;
import gonevas.com.utils.MyFunctions;

public class OrderItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int style = 0;
    MyFunctions functions;
    int count = 0;
    private List<OrderLineItems> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public OrderItemsAdapter(Context context, List<OrderLineItems> items, int style) {
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
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);
            }
        } else if (style == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);


        } else if (style == 2) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);


        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);
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

            OrderLineItems o = items.get(position);
            view.order_title.setText("" + o.name + " (" + o.quantity + ")");
            try {
                view.order_total.setText(Html.fromHtml(functions.toMoneyFormat(Double.parseDouble(o.total)) + " CFA").toString());
            } catch (Exception e) {

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
        void onItemClick(View view, OrderLineItems obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView order_title;
        public TextView order_description;
        public TextView order_total;
        public MaterialRippleLayout product_parent_view;

        public OriginalViewHolder(View v) {
            super(v);
            order_title = v.findViewById(R.id.order_title);
            order_description = v.findViewById(R.id.order_description);
            product_parent_view = v.findViewById(R.id.product_parent_view);
            order_total = v.findViewById(R.id.order_total);
        }
    }

}