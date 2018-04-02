package com.intexh.news.moudle.news.bean;

import java.util.List;

/**
 * Created by AndroidIntexh1 on 2017/11/21.
 */

public class AllCommentBean {

    /**
     * reply_id : 1
     * reply_nid : 1
     * reply_uid : 186
     * reply_parentid : 0
     * reply_likenum : 0
     * reply_content : 1111111
     * reply_createtime : null
     * reply_status : 0
     * reply_uavatar : http://news.intexh.com/data/upload/mobile/mobile_img__05645728080736789_240.jpg
     * reply_unickname : 简报用户1879897
     * sonlist : [{"reply_id":"9","reply_nid":"1","reply_uid":null,"reply_parentid":"1","reply_likenum":"0","reply_content":"Greater","reply_createtime":"1511147024","reply_status":"0","reply_uavatar":"http://news.intexh.com/data/upload/shop/common/default_user_portrait.gif","reply_unickname":"匿名用户100607","sonlist":[]},{"reply_id":"12","reply_nid":"1","reply_uid":null,"reply_parentid":"1","reply_likenum":"0","reply_content":"we were","reply_createtime":"1511258208","reply_status":"0","reply_uavatar":"http://news.intexh.com/data/upload/shop/common/default_user_portrait.gif","reply_unickname":"匿名用户100255","sonlist":[]}]
     */

    private String reply_id;
    private String reply_nid;
    private String reply_uid;
    private String reply_parentid;
    private String reply_likenum;
    private String reply_content;
    private Object reply_createtime;
    private String reply_status;
    private String reply_uavatar;
    private String reply_unickname;
    private List<SonlistBean> sonlist;

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getReply_nid() {
        return reply_nid;
    }

    public void setReply_nid(String reply_nid) {
        this.reply_nid = reply_nid;
    }

    public String getReply_uid() {
        return reply_uid;
    }

    public void setReply_uid(String reply_uid) {
        this.reply_uid = reply_uid;
    }

    public String getReply_parentid() {
        return reply_parentid;
    }

    public void setReply_parentid(String reply_parentid) {
        this.reply_parentid = reply_parentid;
    }

    public String getReply_likenum() {
        return reply_likenum;
    }

    public void setReply_likenum(String reply_likenum) {
        this.reply_likenum = reply_likenum;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public Object getReply_createtime() {
        return reply_createtime;
    }

    public void setReply_createtime(Object reply_createtime) {
        this.reply_createtime = reply_createtime;
    }

    public String getReply_status() {
        return reply_status;
    }

    public void setReply_status(String reply_status) {
        this.reply_status = reply_status;
    }

    public String getReply_uavatar() {
        return reply_uavatar;
    }

    public void setReply_uavatar(String reply_uavatar) {
        this.reply_uavatar = reply_uavatar;
    }

    public String getReply_unickname() {
        return reply_unickname;
    }

    public void setReply_unickname(String reply_unickname) {
        this.reply_unickname = reply_unickname;
    }

    public List<SonlistBean> getSonlist() {
        return sonlist;
    }

    public void setSonlist(List<SonlistBean> sonlist) {
        this.sonlist = sonlist;
    }

    public static class SonlistBean {
        /**
         * reply_id : 9
         * reply_nid : 1
         * reply_uid : null
         * reply_parentid : 1
         * reply_likenum : 0
         * reply_content : Greater
         * reply_createtime : 1511147024
         * reply_status : 0
         * reply_uavatar : http://news.intexh.com/data/upload/shop/common/default_user_portrait.gif
         * reply_unickname : 匿名用户100607
         * sonlist : []
         */

        private String reply_id;
        private String reply_nid;
        private Object reply_uid;
        private String reply_parentid;
        private String reply_likenum;
        private String reply_content;
        private String reply_createtime;
        private String reply_status;
        private String reply_uavatar;
        private String reply_unickname;
        private List<?> sonlist;

        public String getReply_id() {
            return reply_id;
        }

        public void setReply_id(String reply_id) {
            this.reply_id = reply_id;
        }

        public String getReply_nid() {
            return reply_nid;
        }

        public void setReply_nid(String reply_nid) {
            this.reply_nid = reply_nid;
        }

        public Object getReply_uid() {
            return reply_uid;
        }

        public void setReply_uid(Object reply_uid) {
            this.reply_uid = reply_uid;
        }

        public String getReply_parentid() {
            return reply_parentid;
        }

        public void setReply_parentid(String reply_parentid) {
            this.reply_parentid = reply_parentid;
        }

        public String getReply_likenum() {
            return reply_likenum;
        }

        public void setReply_likenum(String reply_likenum) {
            this.reply_likenum = reply_likenum;
        }

        public String getReply_content() {
            return reply_content;
        }

        public void setReply_content(String reply_content) {
            this.reply_content = reply_content;
        }

        public String getReply_createtime() {
            return reply_createtime;
        }

        public void setReply_createtime(String reply_createtime) {
            this.reply_createtime = reply_createtime;
        }

        public String getReply_status() {
            return reply_status;
        }

        public void setReply_status(String reply_status) {
            this.reply_status = reply_status;
        }

        public String getReply_uavatar() {
            return reply_uavatar;
        }

        public void setReply_uavatar(String reply_uavatar) {
            this.reply_uavatar = reply_uavatar;
        }

        public String getReply_unickname() {
            return reply_unickname;
        }

        public void setReply_unickname(String reply_unickname) {
            this.reply_unickname = reply_unickname;
        }

        public List<?> getSonlist() {
            return sonlist;
        }

        public void setSonlist(List<?> sonlist) {
            this.sonlist = sonlist;
        }
    }
}
