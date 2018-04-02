package com.intexh.news.moudle.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.moudle.main.adapter.easyadapter.BaseViewHolder;
import com.intexh.news.moudle.main.adapter.easyadapter.RecyclerArrayAdapter;
import com.intexh.news.moudle.mine.bean.NoticeBean;
import com.intexh.news.moudle.mine.bean.UniversalMessageBean;
import com.intexh.news.utils.DataUtil;
import com.zjw.base.utils.ValidateUtils;

import butterknife.BindView;

/**
 * Created by AndroidIntexh1 on 2017/11/14.
 */

public class UniversalMessageAdapter extends RecyclerArrayAdapter<NoticeBean> {

    private Context mContext;
    private LayoutInflater inflater;

    public UniversalMessageAdapter(Context context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_message_universal, parent, false));
    }

    class ViewHolder extends BaseViewHolder<NoticeBean> {
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
        public void setData(NoticeBean data) {
            super.setData(data);
            timeTv.setText(ValidateUtils.isValidate(data.getMessage_time()) ? DataUtil.getStrTime(data.getMessage_time()) : "");
            contentTv.setText(data.getMessage_content());
            optionTv.setOnClickListener(view -> {
                if (onDeleteComment!=null){
                    onDeleteComment.onDelete(getDataPosition());
                }
            });

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