package gonevas.com.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.model.Ad;
import gonevas.com.utils.MyFunctions;
import gonevas.com.utils.Tools;

public class AdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int style = 0;
    MyFunctions functions;
    int count = 0;
    private List<Ad> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    Tools tools;

    public AdsAdapter(Context context, List<Ad> items, int style) {
        this.items = items;
        this.ctx = context;
        this.style = style;
        functions = new MyFunctions(ctx);
        tools = new Tools();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;
        if (style == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_2, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_2, parent, false);

        }


        count++;
        vh = new OriginalViewHolder(v);


        return vh;
    }

    String img_link = "";

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Ad p = items.get(position);
            view.ad_title.setText(p.title);
            img_link = p.photo;

            Log.d("NOT MUHINDOOO", "init_category_section: ==> " + p.position);

            if (p.photo.length() > 2) {
                tools.displayImageRemote(ctx, view.ad_photo, img_link, R.drawable.gonevas_category);
            }

            view.ad_parent_view.setOnClickListener(new View.OnClickListener() {
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
        void onItemClick(View view, Ad obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView ad_photo;
        public TextView ad_title;
        public TextView ad_detail;
        public LinearLayout ad_parent_view;

        public OriginalViewHolder(View v) {
            super(v);
            ad_photo = v.findViewById(R.id.ad_photo);
            ad_title = v.findViewById(R.id.ad_title);
            ad_detail = v.findViewById(R.id.ad_detail);
            ad_parent_view = v.findViewById(R.id.ad_parent_view);
        }
    }

}