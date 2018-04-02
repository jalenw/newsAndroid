package com.intexh.news.helper;

import android.content.Context;
import android.widget.Toast;

import com.mob.MobSDK;
import com.intexh.news.moudle.mine.bean.ShareBean;
import com.intexh.news.utils.DemoUtils;
import com.intexh.news.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.utils.LogCatUtil;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;

import cn.sharesdk.dingding.friends.Dingding;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;

public enum ShareHelper {
    INSTANCE;
    private Context context;

    //微信好友分享
    public void shareToWeChat(Context context, ShareBean shareBean) {
        this.context = context;
        if (shareBean == null) {
            return;
        }
        LogCatUtil.e("T1aaa ", "share -- bean:" + shareBean);
        Wechat.ShareParams spWeChat = new Wechat.ShareParams();
        spWeChat.setShareType(Platform.SHARE_WEBPAGE);
        spWeChat.setText(shareBean.getText());
        spWeChat.setUrl(shareBean.getUrl());
        spWeChat.setTitle(shareBean.getTitle());
        spWeChat.setComment(shareBean.getText());
        if (ValidateUtils.isValidate(shareBean.getImgPath())) {
            spWeChat.setImagePath(shareBean.getImgPath());
        }else {
            spWeChat.setImageUrl(shareBean.getPic());
        }
//        spWeChat.setImageData(shareBean.getImgLogo());
        Platform weChat = ShareSDK.getPlatform(Wechat.NAME);
        weChat.setPlatformActionListener(new ShareListener()); // 设置分享事件回调
        // 执行图文分享
        weChat.share(spWeChat);
    }


    //朋友圈分享
    public void shareToWeChatFriend(Context context, ShareBean shareBean) {
        this.context = context;

        if (shareBean == null) {
            return;
        }
        WechatMoments.ShareParams spWeChatMoments = new WechatMoments.ShareParams();
        spWeChatMoments.setShareType(Platform.SHARE_WEBPAGE);
        spWeChatMoments.setText(shareBean.getText());
        spWeChatMoments.setUrl(shareBean.getUrl());

        spWeChatMoments.setTitle(shareBean.getTitle());
        spWeChatMoments.setComment(shareBean.getText());
        if (ValidateUtils.isValidate(shareBean.getImgPath())) {
            spWeChatMoments.setImagePath(shareBean.getImgPath());
        }else {
            spWeChatMoments.setImageUrl(shareBean.getPic());
        }
        Platform weChatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
        weChatMoments.setPlatformActionListener(new ShareListener()); // 设置分享事件回调
        // 执行图文分享
        weChatMoments.share(spWeChatMoments);
    }

    //QQ分享
    public void shareToQQ(Context context, ShareBean shareBean) {
        this.context = context;
        if (shareBean == null) {
            return;
        }
        QQ.ShareParams qqShare = new QQ.ShareParams();
        qqShare.setShareType(Platform.SHARE_WEBPAGE);
        qqShare.setTitleUrl(shareBean.getUrl());
        if (shareBean.isHome()) {
            qqShare.setTitle(shareBean.getText());
        } else {
            qqShare.setTitle(shareBean.getTitle());
        }
        qqShare.setText(shareBean.getText());
        if (ValidateUtils.isValidate(shareBean.getImgPath())) {
            qqShare.setImagePath(shareBean.getImgPath());
        }else {
            qqShare.setImageUrl(shareBean.getPic());
        }
        Platform qqSharePlatFrom = ShareSDK.getPlatform(QQ.NAME);
        qqSharePlatFrom.setPlatformActionListener(new ShareListener()); // 设置分享事件回调
        // 执行图文分享
        qqSharePlatFrom.share(qqShare);
    }

    //QQ分享--空间
    public void shareToQQFriend(Context context, ShareBean shareBean) {
        this.context = context;
        if (shareBean == null) {
            return;
        }
        QZone.ShareParams qqShare = new QZone.ShareParams();
        qqShare.setShareType(Platform.SHARE_WEBPAGE);
        qqShare.setTitleUrl(shareBean.getUrl());
        if (shareBean.isHome()) {
            qqShare.setTitle(shareBean.getText());
        } else {
            qqShare.setTitle(shareBean.getTitle());
        }
        qqShare.setText(shareBean.getText());

        if (ValidateUtils.isValidate(shareBean.getImgPath())) {
            qqShare.setImagePath(shareBean.getImgPath());
        }else {
            qqShare.setImageUrl(shareBean.getPic());
        }
        Platform qqSharePlatFrom = ShareSDK.getPlatform(QZone.NAME);
        qqSharePlatFrom.setPlatformActionListener(new ShareListener()); // 设置分享事件回调
        // 执行图文分享
        qqSharePlatFrom.share(qqShare);
    }

    //新浪微博分享
    public void shareToSinaWeibo(ShareBean shareBean) {
        if (shareBean == null) {
            return;
        }
        SinaWeibo.ShareParams sina = new SinaWeibo.ShareParams();
        sina.setShareType(Platform.SHARE_WEBPAGE);
        sina.setText(shareBean.getText());
        sina.setUrl(shareBean.getUrl());
        sina.setTitle(shareBean.getTitle());
        if (ValidateUtils.isValidate(shareBean.getImgPath())) {
            sina.setImagePath(shareBean.getImgPath());
        }else {
            sina.setImageUrl(shareBean.getPic());
        }
        Platform sinaShare = ShareSDK.getPlatform(SinaWeibo.NAME);
        if(sinaShare.isClientValid()){
            sinaShare.setPlatformActionListener(null); // 设置分享事件回调
            // 执行图文分享
            sinaShare.share(sina);
        }else {
            ToastUtil.showToast(mContext, "没有安装新浪微博");
        }
    }

    public void shareToDingding(Context context, ShareBean shareBean) {
        this.context = context;
        DemoUtils.isValidClient("com.alibaba.android.rimet");
        Platform platform = ShareSDK.getPlatform(Dingding.NAME);
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setText(shareBean.getText());
        shareParams.setTitle(shareBean.getTitle());
        shareParams.setUrl(shareBean.getUrl());
        if (ValidateUtils.isValidate(shareBean.getImgPath())) {
            shareParams.setImagePath(shareBean.getImgPath());
        }else {
            shareParams.setImageUrl(shareBean.getPic());
        }
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        shareParams.setScence(0);

        platform.setPlatformActionListener(new ShareListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                super.onError(arg0, arg1, arg2);

            }
        });
        platform.share(shareParams);
    }

    class ShareListener implements PlatformActionListener {
        int mShareType;

        public ShareListener() {
        }

        public ShareListener(int shareType) {
            this.mShareType = shareType;
        }

        @Override
        public void onCancel(Platform arg0, int arg1) {
            ToastUtil.showToast(context, "分享取消");
        }

        @Override
        public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
            ToastUtil.showToast(context, "分享成功");
        }

        @Override
        public void onError(Platform arg0, int arg1, final Throwable arg2) {
            ToastUtil.showToast(context, "分享失败,请稍后重试");
        }
    }
}
