package com.intexh.news.moudle.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.moudle.main.adapter.easyadapter.BaseViewHolder;
import com.intexh.news.moudle.main.adapter.easyadapter.RecyclerArrayAdapter;

import butterknife.BindView;

/**
 * Created by AndroidIntexh1 on 2017/11/20.
 */
public class SearchAdapter extends RecyclerArrayAdapter<String> {


    private Context mContext;
    private LayoutInflater inflater;
    private boolean mDelete;

    public SearchAdapter(Context context, boolean hasDelete) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
        mDelete = hasDelete;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_history, parent, false));
    }

    class ViewHolder extends BaseViewHolder<String> {
        TextView contentTv;
        ImageView deleteIv;

        public ViewHolder(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.content_tv);
            deleteIv = itemView.findViewById(R.id.close_iv);
            if (mDelete) {
                deleteIv.setVisibility(View.VISIBLE);
                deleteIv.setOnClickListener(view -> {
                    if (ondeleteItem != null) {
                        ondeleteItem.onDeleteitem(getDataPosition());
                    }
                });
            } else {
                deleteIv.setVisibility(View.GONE);
            }

        }

        @Override
        public void setData(String data) {
            super.setData(data);
            contentTv.setText(data);

        }

    }

    public interface OndeleteItem {
        void onDeleteitem(int position);
    }

    public OndeleteItem ondeleteItem;

    public void setOndeleteItem(OndeleteItem ondeleteItem) {
        this.ondeleteItem = ondeleteItem;
    }
}