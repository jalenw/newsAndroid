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
import com.intexh.news.moudle.news.bean.ChannelBean;
import com.zjw.base.utils.ValidateUtils;

import butterknife.BindView;

/**
 * Created by AndroidIntexh1 on 2017/11/15.
 */


public class ChannelListAdapter extends RecyclerArrayAdapter<ChannelBean> {


    private Context mContext;
    private LayoutInflater inflater;

    public ChannelListAdapter(Context context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_channel, parent, false));

    }



    class ViewHolder extends BaseViewHolder<ChannelBean> {
        TextView contentTv;
        ImageView coverIv;

        public ViewHolder(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.content_tv);
            coverIv = itemView.findViewById(R.id.cover_iv);
        }

        @Override
        public void setData(ChannelBean data) {
            super.setData(data);
            contentTv.setText(data.getChannel_content());
            if (ValidateUtils.isValidate(data.isEditing())){
                 if (data.isEditing()){
                     coverIv.setVisibility(View.VISIBLE);
                 }else {
                     coverIv.setVisibility(View.GONE);
                 }
            }

        }

    }
}