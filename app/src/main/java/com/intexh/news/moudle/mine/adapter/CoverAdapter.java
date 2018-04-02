package com.intexh.news.moudle.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.intexh.news.R;
import com.intexh.news.html.ui.WebViewActivity;
import com.intexh.news.moudle.main.adapter.easyadapter.BaseViewHolder;
import com.intexh.news.moudle.main.adapter.easyadapter.RecyclerArrayAdapter;
import com.intexh.news.moudle.mine.bean.CoverBean;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.utils.ValidateUtils;

import butterknife.BindView;

/**
 * Created by AndroidIntexh1 on 2017/11/25.
 */

public class CoverAdapter extends RecyclerArrayAdapter<CoverBean> {


    private Context mContext;
    private LayoutInflater inflater;

    public CoverAdapter(Context context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_cover, parent, false));
    }

    class ViewHolder extends BaseViewHolder<CoverBean> {
        ImageView imageTv;

        public ViewHolder(View itemView) {
            super(itemView);
            imageTv = itemView.findViewById(R.id.image_tv);

        }

        @Override
        public void setData(CoverBean data) {
            super.setData(data);
            if (ValidateUtils.isValidate(data.getAdvertise_address())) {
                GlideHelper.INSTANCE.loadImage(imageTv, data.getAdvertise_address());
            }
            imageTv.setOnClickListener(view -> {
                if (ValidateUtils.isValidate(data.getAdvertise_url())) {
                    WebViewActivity.startActivity(mContext, data.getAdvertise_url());
                }
            });
        }
    }

}