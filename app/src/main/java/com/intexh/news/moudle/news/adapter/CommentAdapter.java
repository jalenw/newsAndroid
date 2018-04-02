package com.intexh.news.moudle.news.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.moudle.main.adapter.easyadapter.BaseViewHolder;
import com.intexh.news.moudle.main.adapter.easyadapter.RecyclerArrayAdapter;
import com.intexh.news.moudle.news.bean.AllCommentBean;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.utils.ValidateUtils;

import butterknife.BindView;

/**
 * Created by AndroidIntexh1 on 2017/11/21.
 */
public class CommentAdapter extends RecyclerArrayAdapter<AllCommentBean> {


    private Context mContext;
    private LayoutInflater inflater;

    public CommentAdapter(Context context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_comment, parent, false));
    }

    class ViewHolder extends BaseViewHolder<AllCommentBean> {
        ImageView avatarTv;
        TextView nameTv;
        TextView pointTv;
        TextView commentTv;
        TextView contentTv;
        RecyclerView subRecycleView;
        int likesum = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarTv = itemView.findViewById(R.id.avatar_tv);
            nameTv = itemView.findViewById(R.id.name_tv);
            pointTv = itemView.findViewById(R.id.point_tv);
            commentTv = itemView.findViewById(R.id.comment_tv);
            contentTv = itemView.findViewById(R.id.content_tv);
            subRecycleView = itemView.findViewById(R.id.sub_recycleView);

        }

        @Override
        public void setData(AllCommentBean data) {
            super.setData(data);
            if (ValidateUtils.isValidate(data.getReply_uavatar())) {
                GlideHelper.INSTANCE.loadCircleImage(avatarTv, data.getReply_uavatar());
            }
            nameTv.setText(data.getReply_unickname());
            contentTv.setText(data.getReply_content());
            if (ValidateUtils.isValidate(data.getReply_likenum())) {
                pointTv.setText(data.getReply_likenum());
            }
            likesum = Integer.parseInt(data.getReply_likenum());
            Log.e("wilson ", likesum + "");
            commentTv.setOnClickListener(view -> {
                if (ValidateUtils.isValidate(commentOnclick)) {
                    commentOnclick.onCommentclick(getDataPosition());
                }
            });
            if (ValidateUtils.isValidate(data.getSonlist())) {
                SubCommentAdapter subCommentAdapter = new SubCommentAdapter(mContext);
                subRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
                subRecycleView.setAdapter(subCommentAdapter);
                subCommentAdapter.addAll(data.getSonlist());
            }
            pointTv.setOnClickListener(view -> {
                if (pointedClick != null) {
                    likesum++;
                    pointedClick.onPointed(getDataPosition());
                }
            });

        }

    }

    public interface CommentOnclick {
        void onCommentclick(int position);
    }

    public CommentOnclick commentOnclick;

    public void setCommentOnclick(CommentOnclick commentOnclick) {
        this.commentOnclick = commentOnclick;
    }

    public interface PointedClick {
        void onPointed(int position);
    }

    public PointedClick pointedClick;

    public void setPointedClick(PointedClick pointedClick) {
        this.pointedClick = pointedClick;
    }
}