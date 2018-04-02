package com.intexh.news.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.intexh.news.R;
import com.zjw.base.utils.UIUtil;
import com.zjw.base.utils.ValidateUtils;

import butterknife.BindView;

/**
 * Created by Frank on 2017/6/23.
 */

public class DialogUtil {
    static int pay_type = 0;


    public static void showDefaultDialog(Context context, String title, String content, final DialogImpli listener) {
        showDefaultDialog(context, title, content, "确定", "取消", listener);
    }

    public static void showDefaultDialog(Context context, String title, String content, String ok, String cancel, final DialogImpli listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setCancelable(false);
        builder.setNegativeButton(cancel, (dialog, which) -> listener.onCancel());
        builder.setPositiveButton(ok, (dialog, which) -> listener.onOk());
        builder.show();
    }

    /**
     * 显示全屏的dialog dialog本身是透明，颜色再view中定义
     *
     * @param context
     * @param content
     * @param isCancelable             是否可以点击返回键取消
     * @param isCanceledOnTouchOutside 点击dialog意外其他区域是否可以取消
     * @return
     */
    public static Dialog showFullScreenViewDialog(Context context, View content,
                                                  boolean isCancelable, boolean isCanceledOnTouchOutside) {
        Dialog dialog = new Dialog(context, R.style.transparent_dialog_theme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(isCancelable);         //是否可以取消
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside); //点击弹窗外是否消失
        dialog.setContentView(content);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = UIUtil.getWindowWidth(context); // 宽度
        lp.height = UIUtil.getWindowHeight(context); // 高度
        dialogWindow.setAttributes(lp);
        dialog.show();
        return dialog;

    }

    public static Dialog getViewDialog(Context context, View content,
                                       boolean isCancelable, boolean isCanceledOnTouchOutside) {
        Dialog dialog = new Dialog(context, R.style.transparent_dialog_theme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(isCancelable);         //是否可以取消
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside); //点击弹窗外是否消失
        dialog.setContentView(content);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = UIUtil.getWindowWidth(context); // 宽度
        lp.height = UIUtil.getWindowHeight(context); // 高度
        dialogWindow.setAttributes(lp);
        return dialog;

    }

    public static void showConfirmHitDialog(Context context, String s, String message, String leftStr, String rightStr, DialogImpl impl) {
        View view = View.inflate(context, R.layout.dialog_confirm_hint, null);
        TextView messageTv = (TextView) view.findViewById(R.id.message_tv);
        messageTv.setText(message);
        TextView leftBtn = (TextView) view.findViewById(R.id.left_btn);
        TextView rightBtn = (TextView) view.findViewById(R.id.right_btn);
        rightBtn.setText(rightStr);
        Dialog dialog = showFullScreenViewDialog(context, view, false, false);
        if (!ValidateUtils.isValidate(leftStr)) {
            leftBtn.setVisibility(View.GONE);
        } else {
            leftBtn.setText(leftStr);
            leftBtn.setOnClickListener(v -> {
                onDismiss(view, () -> {
                    dialog.dismiss();
                    impl.onLeft();
                });

            });
        }

        rightBtn.setOnClickListener(v -> {
            onDismiss(view, () -> {
                dialog.dismiss();
                impl.onRight();
            });
        });

    }
    /**
     * 显示打赏对话框
     *
     * @param context
     * @param
     */
    public static void showRewardDialog(Context context, RewardImpl rewradIpl) {
        View view = View.inflate(context, R.layout.dialog_reward, null);
        ImageView closeIv = view.findViewById(R.id.close_iv);
        RadioButton lowPriceRb = view.findViewById(R.id.low_price_rb);
        RadioButton midPriceRb = view.findViewById(R.id.mid_price_rb);
        RadioButton highPriceRb = view.findViewById(R.id.high_price_rb);
        RadioGroup priceRg = view.findViewById(R.id.price_rg);
        RadioButton wechatPayRb = view.findViewById(R.id.wechat_pay_rb);
        RadioButton aliPayRb = view.findViewById(R.id.ali_pay_rb);
        RadioGroup typeRg = view.findViewById(R.id.type_rg);
        TextView rewardBtn = view.findViewById(R.id.reward_btn);
        Dialog dialog = showFullScreenViewDialog(context, view, false, false);
        closeIv.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        rewardBtn.setOnClickListener(view1 -> {
            String price = "";
            String type = "";
            switch (priceRg.getCheckedRadioButtonId()) {
                case R.id.low_price_rb:
                    price = "low";
                    break;
                case R.id.mid_price_rb:
                    price = "mid";
                    break;
                case R.id.high_price_rb:
                    price = "high";
                    break;
            }
            switch (typeRg.getCheckedRadioButtonId()) {
                case R.id.wechat_pay_rb:
                    type = "wechat";

                    break;
                case R.id.ali_pay_rb:
                    type = "alipay";
                    break;
            }
            rewradIpl.onReward(price, type);
        });


    }

    private static void onDismiss(View view, OnAnimationEndListener listener) {
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(1, 0);
        view.startAnimation(alphaAnimation1);//开始动画
        alphaAnimation1.setFillAfter(true);//动画结束后保持状态
        alphaAnimation1.setDuration(300);//动画持续时间，单位为毫秒
        alphaAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onEnd();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public interface DialogImpli {
        void onOk();

        void onCancel();
    }


    public interface BottomMenuDialogImpl {
        void onMenu1();

        void onMenu2();

        void onMenuTitle();
    }

    public interface StyleDialogImpl {
        void onOk(Dialog dialog, String nickName);

        void onCancel(Dialog dialog);
    }

    public interface StyleUnifiedDialogImpl {
        void onOk(Dialog dialog);

        void onCancel(Dialog dialog);
    }

    public interface StyleHintDialogImpl {
        void onOk();
    }

    private interface OnAnimationEndListener {
        void onEnd();
    }

    public static class DialogImpl {
        public void onLeft() {
        }

        public void onRight() {
        }
    }

    public static class RewardImpl {
        public void onReward(String price, String type) {

        }
    }

    /**
     * 分享底部对话框
     */
    public static Dialog showShareBottomDialog(Context context, final ShareBottomDialogImpl impl) {
        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        View view = View.inflate(context, R.layout.dialog_share_bottom_layout, null);

        ImageView closeIv = view.findViewById(R.id.close_iv);
        TextView wechatTv = view.findViewById(R.id.wechat_tv);
        TextView friendTv = view.findViewById(R.id.friend_tv);
        TextView qqTv = view.findViewById(R.id.qq_tv);
        TextView weiboTv = view.findViewById(R.id.weibo_tv);
        TextView zoneTv = view.findViewById(R.id.zone_tv);
        TextView dingdingTv = view.findViewById(R.id.dingding_tv);

        closeIv.setOnClickListener(view1 -> {
            mBottomSheetDialog.dismiss();
        });
        wechatTv.setOnClickListener(view1 -> {
            mBottomSheetDialog.dismiss();
            impl.onShare(mBottomSheetDialog, "wechatTv");
        });
        friendTv.setOnClickListener(view1 -> {
            mBottomSheetDialog.dismiss();
            impl.onShare(mBottomSheetDialog, "friendTv");
        });
        qqTv.setOnClickListener(view1 -> {
            mBottomSheetDialog.dismiss();
            impl.onShare(mBottomSheetDialog, "qqTv");
        });
        weiboTv.setOnClickListener(view1 -> {
            mBottomSheetDialog.dismiss();
            impl.onShare(mBottomSheetDialog, "weiboTv");
        });
        zoneTv.setOnClickListener(view1 -> {
            mBottomSheetDialog.dismiss();
            impl.onShare(mBottomSheetDialog, "zoneTv");
        });
        dingdingTv.setOnClickListener(view1 -> {
            mBottomSheetDialog.dismiss();
            impl.onShare(mBottomSheetDialog, "dingdingTv");
        });
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        return mBottomSheetDialog;
    }

    /**
     * 登录底部对话框
     */
    public static Dialog showLoginBottomDialog(Context context, final ShareBottomDialogImpl impl) {
        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        View view = View.inflate(context, R.layout.dialog_login_bottom_layout, null);

        ImageView closeIv = view.findViewById(R.id.close_iv);
        TextView wechatTv = view.findViewById(R.id.wechat_tv);
        TextView phoneTv = view.findViewById(R.id.phone_tv);
        TextView qqTv = view.findViewById(R.id.qq_tv);

        closeIv.setOnClickListener(view1 -> {
            mBottomSheetDialog.dismiss();
        });
        wechatTv.setOnClickListener(view1 -> {
            impl.onShare(mBottomSheetDialog, "wechatTv");
        });
        phoneTv.setOnClickListener(view1 -> {
            impl.onShare(mBottomSheetDialog, "phoneTv");
        });
        qqTv.setOnClickListener(view1 -> {
            impl.onShare(mBottomSheetDialog, "qqTv");
        });

        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        return mBottomSheetDialog;
    }

    public interface ShareBottomDialogImpl {
        void onShare(Dialog dialog, String type);
    }


    /**
     * 底部菜单选择
     *
     * @param context
     * @param menu1_str
     * @param menu2_str
     * @param impl
     */
    public static void showBottomMenuDialog(Context context, String title, String menu1_str, String menu2_str, BottomMenuDialogImpl impl) {
        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        View view = View.inflate(context, R.layout.dialog_bottom_tow_menu_sheet, null);
        TextView title_tv = (TextView) view.findViewById(R.id.title_tv);
        title_tv.setText(title);
        TextView menu1 = (TextView) view.findViewById(R.id.menu1);
        TextView menu2 = (TextView) view.findViewById(R.id.menu2);
        menu1.setText(menu1_str);
        menu2.setText(menu2_str);

        Button cancel = (Button) view.findViewById(R.id.cancel);

        menu1.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
            impl.onMenu1();
        });
        menu2.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
            impl.onMenu2();
        });
        title_tv.setOnClickListener(view1 -> {
            mBottomSheetDialog.dismiss();
            impl.onMenuTitle();
        });

        cancel.setOnClickListener(v -> mBottomSheetDialog.dismiss());

        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
    }
}
