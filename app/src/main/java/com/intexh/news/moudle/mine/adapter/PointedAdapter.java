package com.intexh.news.moudle.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.moudle.main.adapter.easyadapter.BaseViewHolder;
import com.intexh.news.moudle.main.adapter.easyadapter.RecyclerArrayAdapter;
import com.intexh.news.moudle.mine.bean.PointedBean;
import com.intexh.news.moudle.mine.bean.StoreBean;
import com.intexh.news.utils.DataUtil;
import com.zjw.base.utils.ValidateUtils;

/**
 * Created by AndroidIntexh1 on 2017/11/21.
 */

public class PointedAdapter extends RecyclerArrayAdapter<PointedBean> {


    private Context mContext;
    private LayoutInflater inflater;

    public PointedAdapter(Context context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_message_universal, parent, false));
    }

    class ViewHolder extends BaseViewHolder<PointedBean> {
        TextView timeTv;
        TextView optionTv;
        TextView contentTv;

        public ViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.time_tv);
            optionTv = itemView.findViewById(R.id.option_tv);
            contentTv = itemView.findViewById(R.id.content_tv);

        }

        @Override
        public void setData(PointedBean data) {
            super.setData(data);
            timeTv.setText(ValidateUtils.isValidate(data.getNew_publictime()) ? DataUtil.getStrTime(data.getNew_publictime()) : "");
            contentTv.setText(data.getNew_title());
            optionTv.setVisibility(View.GONE);
//            optionTv.setOnClickListener(view -> {
//                if (onDeleteComment != null) {
//                    onDeleteComment.onDelete(getDataPosition());
//                }
//            });
        }

    }

    public interface OnDeleteComment {
        void onDelete(int position);
    }


    public CommentAdapter.OnDeleteComment onDeleteComment;

    public void setOnDeleteComment(CommentAdapter.OnDeleteComment onDeleteComment) {
        this.onDeleteComment = onDeleteComment;
    }
}