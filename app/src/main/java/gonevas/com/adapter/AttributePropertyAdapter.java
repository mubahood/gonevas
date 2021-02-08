package gonevas.com.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.model.AttributeProperty;
import gonevas.com.utils.MyFunctions;

public class AttributePropertyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int style = 0;
    MyFunctions functions;
    int count = 0;
    private List<AttributeProperty> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public AttributePropertyAdapter(Context context, List<AttributeProperty> items, int style) {
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
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attribute_property, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attribute_property, parent, false);
            }
        } else if (style == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attribute_property, parent, false);


        } else if (style == 2) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attribute_property, parent, false);


        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attribute_property, parent, false);
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

            AttributeProperty p = items.get(position);
            view.attribute_name.setText(Html.fromHtml(p.name).toString());
            view.attribute_value.setHtml(p.value, new HtmlHttpImageGetter(view.attribute_value));


           /* view.attribute_value.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
*/

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, AttributeProperty obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView attribute_name;
        public HtmlTextView attribute_value;

        public OriginalViewHolder(View v) {
            super(v);
            attribute_name = v.findViewById(R.id.attribute_name);
            attribute_value = v.findViewById(R.id.attribute_value);
        }
    }

}