package com.intexh.news.moudle.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.moudle.main.adapter.easyadapter.BaseViewHolder;
import com.intexh.news.moudle.main.adapter.easyadapter.RecyclerArrayAdapter;
import com.intexh.news.moudle.news.bean.AllCommentBean;

/**
 * Created by AndroidIntexh1 on 2017/11/21.
 */

public class SubCommentAdapter extends RecyclerArrayAdapter<AllCommentBean.SonlistBean> {


    private Context mContext;
    private LayoutInflater inflater;

    public SubCommentAdapter(Context context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_sub_comment, parent, false));
    }

    class ViewHolder extends BaseViewHolder<AllCommentBean.SonlistBean> {
        TextView contentTv;

        public ViewHolder(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.content_tv);

        }

        @Override
        public void setData(AllCommentBean.SonlistBean data) {
            super.setData(data);
            contentTv.setText(data.getReply_unickname() + "：" + data.getReply_content());
        }
    }
}