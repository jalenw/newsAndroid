package com.intexh.news.net;


public interface Apis {
    //
//注册
    String getRegister = "?act=connect&op=sms_register";
    String getRegisterMessage = "?act=connect&op=get_sms_captcha";
    String getLogin = "?act=login&op=captchaLogin";
    String getReservePsw = "Customer/ChangePassword/";
    String getQQLogin = "?act=connect&op=loginByQq";
    String getWechatLogin = "?act=connect&op=loginByWeixin";

    String getMailCode = "?act=connect&op=sendEmailCode";
    String veriMailCode = "?act=connect&op=identityEmailCode";
    String veriPhoneCode = "?act=connect&op=check_sms_captcha";
    String checkMObile="?act=connect&op=checkMobile";
    //新闻
    String getChannel = "?act=channel";
    String getNewsList = "?act=news";
    String getNewsDetail = "?act=news&op=detail";
    String getNewsAllComment = "?act=news&op=getAllComment";
    String commentNews = "?act=news&op=addComment";
    String deleteNews = "?act=news&op=deleteNews";
    String pointNews = "?act=newsuser&op=likeNews";//新闻点赞
    String storeNews = "?act=newsuser&op=colloctNews";//收藏新闻
    String pointComment = "?act=news&op=likeComment";//评论点赞
    String getNewsAD = "?act=advertise&op=getAd";//信息广告
    String deleteDraft = "?act=newsUser&op=deleteDraft";
    String reverseDraft = "?act=news&op=editNews";
    String reportNews = "?act=news&op=addAccusation";
    String clickNews="?act=advertise&op=adclick";
    String hasNewAdd = "?act=news&op=checkNews1";

    //搜索
    String getSearch = "?act=search&op=addSearch";
    String getDeleteSearch = "?act=search&op=deleteSearch";
    String getHotSearch = "?act=search";
    String getSearchHistory = "?act=search&op=userSearch";
    //资讯管理搜索
    String getNewsManager = "?act=newsuser&op=searchNews";
    //资讯审核搜素
    String getNewsCheck = "?act=newsuser&op=searchNews1";

    //频道
    String addChannel = "?act=channel&op=addChannel";
    String changeChannel = "?act=channel&op=changeSort";
    String editChannel = "?act=channel&op=channeledit";
    //添加资讯
    String doDisclose = "?act=news&op=addNews";
    String getDraft = "?act=newsuser&op=mydraft";
    String managerInfo = "?act=news&op=identifyNew";
    String checkInfo = "?act=news&op=adminIdentify";
    //图片上传
    String upLoadPic = "?act=upload&op=index";
    //收藏
    String getUserStore = "?act=newsuser&op=colloctNewsList";
    String getUserLike = "?act=newsuser&op=likeNewsList";
    String getUserCommented = "?act=newsuser&op=commentNewsList";
    String deleteComments = "?act=news&op=deleteComment";
    //个人
    String reverseUserInfo = "?act=newsuser&op=editUser";
    String getUserInfo = "?act=newsuser";
    String applyToAuthor = "?act=news&op=identifyUser";
    //反馈
    String feedBack = "?act=news&op=addFeedback";
    //关于我们
    String aboutUs = "?act=news&op=base";
    String inverstAds = "?act=news&op=contactNew";
    //广告流
    String getAdsList = "?act=advertise&op=getAd";
    String getNotice = "?act=news&op=message";
    String getCover = "?act=advertise";
    //支付
    String wechatPay = "?act=newsuser&op=begin_wxpay";
    String aliPay = "?act=newsuser&op=begin_alipay";
    String checkVersion="";
    //  ---------------------------------H5-----------------------------------
    String mBaseUrl = "http://app.24xuanbao.com/"; //测试环境:"http://news.intexh.com/"  正式环境:"http://app.24xuanbao.com/"
    String h5Download = mBaseUrl + "wap/download/download.html";
    //wap/news_detail.html?new_id=10
    String shareNews = mBaseUrl + "wap/news_detail.html?";
    String authorWeb="http://www.24xuanbao.com";

}
