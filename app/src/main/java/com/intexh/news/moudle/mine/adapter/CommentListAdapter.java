package com.intexh.news.moudle.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.moudle.disclose.bean.DraftBean;
import com.intexh.news.moudle.main.adapter.easyadapter.BaseViewHolder;
import com.intexh.news.moudle.main.adapter.easyadapter.RecyclerArrayAdapter;
import com.intexh.news.moudle.mine.bean.CommentListBean;

/**
 * Created by AndroidIntexh1 on 2017/11/14.
 */


public class CommentListAdapter extends RecyclerArrayAdapter<CommentListBean> {


    private Context mContext;
    private LayoutInflater inflater;

    public CommentListAdapter(Context context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_comment, parent, false));
    }

    class ViewHolder extends BaseViewHolder<CommentListBean> {

        public ViewHolder(View itemView) {
            super(itemView);


        }

        @Override
        public void setData(CommentListBean data) {
            super.setData(data);


        }

    }
}