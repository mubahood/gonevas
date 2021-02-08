package gonevas.com.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.utils.MyFunctions;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int style = 0;
    MyFunctions functions;
    int count = 0;
    private List<String> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public SearchResultsAdapter(Context context, List<String> items, int style) {
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
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_results, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_results, parent, false);
            }
        } else if (style == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_results, parent, false);


        } else if (style == 2) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_results, parent, false);


        } else if (style == 3) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_results, parent, false);


        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_results, parent, false);
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

            String p = items.get(position);

            view.search_key_word.setText(Html.fromHtml(functions.stringCapitalize(p + "")).toString());


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
        void onItemClick(View view, String obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView search_key_word;
        public LinearLayout product_parent_view;

        public OriginalViewHolder(View v) {
            super(v);
            search_key_word = v.findViewById(R.id.search_key_word);
            product_parent_view = v.findViewById(R.id.product_parent_view);
        }
    }

}