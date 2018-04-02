package com.intexh.news.moudle.disclose.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.moudle.disclose.bean.DraftBean;
import com.intexh.news.moudle.main.adapter.easyadapter.BaseViewHolder;
import com.intexh.news.moudle.main.adapter.easyadapter.RecyclerArrayAdapter;
import com.intexh.news.utils.DataUtil;
import com.zjw.base.utils.ValidateUtils;

import butterknife.BindView;

/**
 * Created by AndroidIntexh1 on 2017/11/11.
 */

public class DraftAdapter extends RecyclerArrayAdapter<DraftBean> {


    private Context mContext;
    private LayoutInflater inflater;

    public DraftAdapter(Context context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_draft, parent, false));
    }

    class ViewHolder extends BaseViewHolder<DraftBean> {
        TextView timeTv;
        TextView deleteTv;
        TextView contentTv;
        TextView typeTv;
        TextView authorTvTv;

        public ViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.time_tv);
            deleteTv = itemView.findViewById(R.id.delete_tv);
            contentTv = itemView.findViewById(R.id.content_tv);
            typeTv = itemView.findViewById(R.id.type_tv);
            authorTvTv = itemView.findViewById(R.id.author_tv_tv);
        }

        @Override
        public void setData(DraftBean data) {
            super.setData(data);
            if (ValidateUtils.isValidate(data.getNew_publictime())) {
                timeTv.setText(DataUtil.getStrTime(data.getNew_publictime()));
            }
            contentTv.setText(data.getNew_title());
            deleteTv.setOnClickListener(view -> {
                if (onDeleteItem != null) {
                    onDeleteItem.onDelete(getDataPosition());
                }

            });
            if (ValidateUtils.isValidate(data.getNew_type())){
                if (data.getNew_type().equals("0")){
                    typeTv.setText("爆");
                }
            }
           authorTvTv.setText(ValidateUtils.isValidate(data.getNew_uname())?data.getNew_uname():"");
        }

    }

    public OnDeleteItem onDeleteItem;

    public interface OnDeleteItem {
        void onDelete(int position);
    }

    public void setOnDeleteItem(OnDeleteItem onDeleteItem) {
        this.onDeleteItem = onDeleteItem;
    }
}