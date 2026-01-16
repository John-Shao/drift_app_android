package com.drift.util;

import android.app.Activity;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;

//import com.drift.dialog.ConfirmDialog2;
//import com.drift.dialog.ConfirmDialogFragment;
import com.drift.dialog.ForeamConfirmDialog;
//import com.foream.dialog.ForeamConnectCamFailedDialog;
//import com.foream.dialog.ForeamConnectCamFailedDialog.OnBtnClick;
//import com.foream.dialog.ForeamHintDialog;
//import com.foream.dialog.InputDialog;
//import com.foream.dialog.RetryUploadDialog;
import com.drift.camcontroldemo.R;
//import com.foream.uihelper.OnChangeTextListener;
//import com.foreamlib.cloud.model.ErrorCode;

public class AlertDialogHelper {
    private final static int DEFAULT_DISAPPEAR_TIME = 1000;

//    public static void showForeamHintDialog(Activity context, int resId) {
//        String resourceType = context.getResources().getResourceTypeName(resId);
//        if (resourceType.equals("string")) {
//            showForeamHintDialog(context, -1, context.getString(resId), DEFAULT_DISAPPEAR_TIME);
//        } else if (resourceType.equals("drawable")) {
//            showForeamHintDialog(context, resId, null, DEFAULT_DISAPPEAR_TIME);
//        }
//    }
//
//    public static void showForeamHintDialog(Activity context, String msg) {
//        showForeamHintDialog(context, -1, msg, DEFAULT_DISAPPEAR_TIME);
//    }
//
//    public static void showCloudError(Activity context, int errCode) {
//        switch (errCode) {
//            // 用户不存在
//            case ErrorCode.ERR_USR_NOT_EXTST_PHONE:
//            case ErrorCode.ERR_USR_NAME_NOT_EXIST:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.user_not_register);
//                break;
//            case ErrorCode.ERR_CAM_NOT_YOU:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.cam_not_you);
//                break;
//            case ErrorCode.ERR_PHONE_EXIST:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.phone_exist);
//                break;
//            case ErrorCode.ERR_USER_EXIST:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.user_exist);
//                break;
//            case ErrorCode.ERR_INVAILD_VERIFICATIONCODE:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.invaild_verificationcode);
//                break;
//            case ErrorCode.ERR_TOO_FREQUENTLY:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.get_pwd_back_too_frequently);
//
//                break;
//            // Email不存在
//            case ErrorCode.ERR_USR_EMAIL_NOT_EXIST:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.email_not_register);
//                break;
//            // 连接出错
//            case ErrorCode.ERR_NETWORK_CONNECT_FAIL:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.network_error);
//                break;
//            // 没有用户登录
//            case ErrorCode.ERR_USR_TOKEN_INVALID:
////                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.network_error);
////                break;break
//            case ErrorCode.ERR_USER_OFFLINE:
////                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.please_login);
//                ActivityUtil.loginInBackground();
////                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.please_login);
//                break;
//            case ErrorCode.ERR_NO_SESIONID:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.please_login);
//                break;
//            // 密码错误
//            case ErrorCode.ERR_USR_WRONG_PASSWORD:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.wrong_password);
//                break;
//            // 摄像头已经存在
//            case ErrorCode.ERR_CAM_EXIST:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.registered);
//                break;
//            // 初始化失败
//            case ErrorCode.ERR_INITAL_FAIL:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.init_fail);
//                break;
//            // 服务器验证未通过
//            case ErrorCode.ERR_USR_VALIDATE_FAIL:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.server_not_verification);
//                break;
//            // 摄像头验证未通过
//            case ErrorCode.ERR_CAM_VALIDATE_FAIL:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.camera_not_verification);
//                break;
//            // 网络出错
//            case ErrorCode.ERR_NETWORK_CONNECT_TIMEOUT:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.network_error);
//                break;
//            // 摄像机已离线
//            case ErrorCode.ERR_CAM_OFFLINE:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.cam_is_offline);
//                break;
//            case ErrorCode.ERR_HAD_LIKE:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.had_like);
//                break;
//            case ErrorCode.EXCEED_DAILY_PK_INVITATION_LIMIT:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.exceed_pk_count_limit);
//                break;
//            case ErrorCode.USER_REJECT_ALL_PK:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.user_set_reject_pk);
//                break;
//            case ErrorCode.USER_HAS_ON_GOING_PK_EVENT:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.user_is_pking);
//                break;
//            case ErrorCode.PK_EVENT_NOT_FOUND:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.connot_find_pk_event);
//                break;
//            case ErrorCode.PK_EVENT_HAS_EXPIRED:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.pk_event_expired);
//                break;
//            case ErrorCode.PK_EVENT_HAS_STARTED:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.pk_event_start);
//                break;
//            case ErrorCode.PK_EVENT_HAS_COMPLETED:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.pk_event_finish);
//                break;
//            case ErrorCode.PK_EVENT_HAS_BEEN_CANCELED:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.pk_cancel);
//                break;
//            case ErrorCode.PK_EVENT_HAS_NOT_END_YET:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.pk_hasnot_finish);
//                break;
//            case ErrorCode.CANNOT_INVITE_THE_SAME_TARGET_TWICE_IN_THE_SAME_TIME:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.connot_invite_twice_ontime);
//                break;
//            case ErrorCode.CANNOT_ACCEPT_OR_REJECT_PK_EVENT_AS_YOU_ARE_NOT_THE_TARGER:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.not_pk_target);
//                break;
//            case ErrorCode.CANNOT_INVITE_YOURSELF:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.cannot_invite_onself);
//                break;
//
//            case ErrorCode.INAPPROPRIATE_WORD_DETECTED:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, R.string.INAPPROPRIATE_WORD_DETECTED);
//                break;
//
//            // 未知错误
//            default:
//                AlertDialogHelper.showForeamHintDialog(context, R.drawable.p_icon_fail, "Error(" + errCode + ")");
//        }
//
//    }
//
//    public static void showForeamSuccessDialogWithConfirm(Activity context, int msgRes) {
//        showForeamSuccessDialogWithConfirm(context, msgRes, null);
//    }
//
//    public static void showForeamSuccessDialogWithConfirm(Activity context, int msgRes, OnClickListener ls) {
//        if (context.isFinishing())
//            return;
//        ForeamConfirmDialog confirmDlg = new ForeamConfirmDialog(context, ForeamConfirmDialog.STYLE_ONE_BUTTON);
//        confirmDlg.setData(R.drawable.p_icon_success, context.getString(msgRes), ls);
//        confirmDlg.show();
//    }
//
//    public static void showForeamSuccessDialog(Activity context, int msgRes) {
//        showForeamHintDialog(context, R.drawable.p_icon_success, context.getString(msgRes), DEFAULT_DISAPPEAR_TIME, null);
//    }
//
//    public static void showForeamSuccessDialog(Activity context, int msgRes, OnClickListener ls) {
//        showForeamHintDialog(context, R.drawable.p_icon_success, context.getString(msgRes), 0, ls);
//    }
//
//    public static void showForeamFailDialog(Activity context, String msgStr) {
//        showForeamHintDialog(context, R.drawable.p_icon_fail, msgStr, DEFAULT_DISAPPEAR_TIME, null);
//    }
//
//    public static void showForeamFailDialog(Activity context, int msgRes) {
//        if (context == null)
//            return;
//        showForeamFailDialog(context, context.getResources().getString(msgRes));
//    }
//
//    public static void showForeamFailDialog(Activity context, String msgStr, OnClickListener ls) {
//        showForeamHintDialog(context, R.drawable.p_icon_fail, msgStr, 0, ls);
//    }
//
//    public static void showForeamFailDialog(Activity context, int msgRes, OnClickListener ls) {
//        showForeamFailDialog(context, context.getResources().getString(msgRes), ls);
//    }
//
//    public static void showForeamHintDialog(Activity context, int iconRes, int msgRes) {
//        showForeamHintDialog(context, iconRes, context.getResources().getString(msgRes), DEFAULT_DISAPPEAR_TIME, null);
//    }
//
//
//
//    public static void showForeamHintDialog(Activity context, int iconRes, int msgRes,DialogMessage dialogMessage) {
//        showForeamHintDialog(context, iconRes, context.getResources().getString(msgRes), DEFAULT_DISAPPEAR_TIME, null,dialogMessage);
//    }
//
//    public static void showForeamHintDialog(Activity context, int iconRes, String msg) {
//        showForeamHintDialog(context, iconRes, msg, DEFAULT_DISAPPEAR_TIME, null);
//    }
//
//    public static void showForeamHintDialog(Activity context, int iconRes, int msgRes, int millsecondToDisappear) {
//        showForeamHintDialog(context, iconRes, context.getResources().getString(msgRes), millsecondToDisappear, null);
//    }
//
//    public static void showForeamHintDialog(Activity context, int iconRes, String msg, int millsecondToDisappear) {
//        showForeamHintDialog(context, iconRes, msg, millsecondToDisappear, null);
//    }
//
////    public static void showForeamHintDialog(Activity context, int iconRes, String msg, int millsecondToDisappear, OnClickListener ls) {
////        if (context == null || context.isFinishing())
////            return;
////        if (ls != null) {
////            showAlertDialog(context, null, msg, ls, null);
////
////        } else {
////            ForeamHintDialog hintDlg = new ForeamHintDialog(context);
////            hintDlg.setData(iconRes, msg, millsecondToDisappear,null);
////            hintDlg.show();
////        }
////    }
//
////    public static void showForeamHintDialog(Activity context, int iconRes, String msg, int millsecondToDisappear, OnClickListener ls,DialogMessage dialogMessage) {
////        if (context == null || context.isFinishing())
////            return;
////        if (ls != null) {
////            showAlertDialog(context, null, msg, ls, null);
////
////        } else {
////            ForeamHintDialog hintDlg = new ForeamHintDialog(context);
////            hintDlg.setData(iconRes, msg, millsecondToDisappear,dialogMessage);
////            hintDlg.show();
////        }
////    }
//
////    public static void showForeamConnectCamFailedDialog(Activity context, int iconRes, String msg, int btnStyle, OnBtnClick ls) {
////        if (context == null || context.isFinishing())
////            return;
////        ForeamConnectCamFailedDialog hintDlg = new ForeamConnectCamFailedDialog(context, btnStyle);
////        hintDlg.setData(iconRes, msg, ls);
////        hintDlg.show();
////    }
//
    public static void showConfirmDialog(Activity mContext, String title_str, String messStr, String enterStr, OnClickListener enterLs, String cancelStr, OnClickListener cancelLs) {
        if (mContext.isFinishing())
            return;
        ForeamConfirmDialog confirmDlg = new ForeamConfirmDialog(mContext, ForeamConfirmDialog.STYLE_TWO_BUTTONS);
        confirmDlg.setData(R.drawable.p_icon_warning, messStr, enterLs);
        confirmDlg.show();
    }

    public static void showConfirmDialog(Activity mContext, String title_str, String messStr, String enterStr, OnClickListener enterLs, String cancelStr, OnClickListener cancelLs, int type) {
        if (mContext.isFinishing())
            return;
        ForeamConfirmDialog confirmDlg = new ForeamConfirmDialog(mContext, type);
        confirmDlg.setData(R.drawable.p_icon_warning, messStr, enterLs);
        confirmDlg.show();
    }
//
//    private static ForeamConfirmDialog showAlertDialog(Activity mContext, String title_str, String messStr, OnClickListener enterLs, OnClickListener cancelLs) {
//
//        return showAlertDialog(mContext, title_str, messStr, true, enterLs, null);
//    }
//
//    private static ForeamConfirmDialog showAlertDialog(Activity mContext, String title_str, String messStr, boolean isclickoutsidedismiss, OnClickListener enterLs, OnClickListener cancelLs) {
//        ForeamConfirmDialog confirmDlg = new ForeamConfirmDialog(mContext, ForeamConfirmDialog.STYLE_ONE_BUTTON);
//        confirmDlg.setData(R.drawable.p_icon_warning, messStr, enterLs);
//        confirmDlg.setCanceledOnTouchOutside(isclickoutsidedismiss);
//        if (!mContext.isFinishing())
//            confirmDlg.show();
//        return confirmDlg;
//    }
//
//    public static void showAlertDialog(Activity mContext, String title_str, String messStr, OnClickListener enterLs) {
//        showAlertDialog(mContext, title_str, messStr, enterLs, null);
//    }
//
//    public static ForeamConfirmDialog showAlertDialog(Activity mContext, int title_id, int msgid, OnClickListener enterLs) {
//        return showAlertDialog(mContext, mContext.getResources().getString(title_id), mContext.getResources().getString(msgid), enterLs, null);
//    }
//
//    public static ForeamConfirmDialog showAlertDialog(Activity mContext, int title_id, int msgid, boolean outsideclickdismiss, OnClickListener enterLs) {
//        return showAlertDialog(mContext, mContext.getResources().getString(title_id), mContext.getResources().getString(msgid), enterLs, null);
//    }
//
//    public static void showAlertDialog(Activity mContext, int title_str, int messStr, OnClickListener enterLs, OnClickListener cancelLs) {
//        showAlertDialog(mContext, mContext.getResources().getString(title_str), mContext.getResources().getString(messStr), enterLs, cancelLs);
//    }
//
    public static void showConfirmDialog(Activity mContext, int title_str, int messStr, OnClickListener enterLs) {
        showConfirmDialog(mContext, mContext.getResources().getString(title_str), mContext.getResources().getString(messStr), null, enterLs, null, null);
    }
//
//    public static void showInputDialog(Activity mContext, int title_str, int input_hint_res, final String defaultText, final OnChangeTextListener ls) {
//        if (mContext.isFinishing())
//            return;
//        InputDialog mDlg = new InputDialog(mContext, title_str, input_hint_res, defaultText, ls);
//        mDlg.show();
//        /*
//         * AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//		 * builder.setTitle(title_str); final EditText et = new
//		 * EditText(mContext); et.setText(defaultText);
//		 * et.setHint(input_hint_res); builder.setView(et);
//		 * builder.setNegativeButton(R.string.enter, new OnClickListener() {
//		 *
//		 * @Override public void onClick(DialogInterface dialog, int which) {
//		 * String outText = et.getText().toString(); ls.onChangeText(outText);
//		 * dialog.dismiss();
//		 *
//		 * } }); builder.setPositiveButton(R.string.cancel, null);
//		 * builder.create(); builder.show();
//		 */
//    }
//
//    public static void showInputDialog(Activity mContext, int title_str, int input_hint_res, final String defaultText, int title_right_str, boolean isDismiss, final OnChangeTextListener ls) {
//        if (mContext.isFinishing())
//            return;
//        InputDialog mDlg = new InputDialog(mContext, title_str, input_hint_res, defaultText, title_right_str, isDismiss, ls);
//        mDlg.show();
//
//    }
//
//    public static void showRetryDialog(Activity mContext, int title_str, int title_right_str, Intent intent, boolean isDismiss, final View.OnClickListener ls) {
//        if (mContext.isFinishing())
//            return;
//        RetryUploadDialog mDlg = new RetryUploadDialog(mContext, title_str, title_right_str, intent, isDismiss, ls);
//        mDlg.show();
//
//    }
//
//    public static ConfirmDialog2 showConfirmDialog2(Activity mContext, int title_str, int content, int lBtnStr, int rBtnStr, final OnClickListener ls) {
//        if (mContext.isFinishing())
//            return null;
//        ConfirmDialog2 mDlg = new ConfirmDialog2(mContext, mContext.getResources().getString(title_str), mContext.getResources().getString(content),
//                mContext.getResources().getString(lBtnStr), mContext.getResources().getString(rBtnStr), ls);
//        return mDlg;
//    }
//
//    public static ConfirmDialog2 showConfirmDialog2(Activity mContext, int title_str, String content, int lBtnStr, int rBtnStr, final OnClickListener ls) {
//        if (mContext.isFinishing())
//            return null;
//        ConfirmDialog2 mDlg = new ConfirmDialog2(mContext, mContext.getResources().getString(title_str), content,
//                mContext.getResources().getString(lBtnStr), mContext.getResources().getString(rBtnStr), ls);
//        return mDlg;
//    }
//
//    public static ConfirmDialog2 showConfirmDialog2(Activity mContext, String title_str, String content, String lBtnStr, String rBtnStr, final OnClickListener ls) {
//        if (mContext.isFinishing())
//            return null;
//        ConfirmDialog2 mDlg = new ConfirmDialog2(mContext, title_str, content, lBtnStr, rBtnStr, ls);
//        return mDlg;
//    }
//
//    public static ConfirmDialogFragment showConfirmDialogFragment(Activity mContext, String title_str, String content, String lBtnStr, String rBtnStr, final OnClickListener ls) {
//        if (mContext.isFinishing())
//            return null;
//        ConfirmDialogFragment mDlg = new ConfirmDialogFragment(mContext, title_str, content, lBtnStr, rBtnStr, ls);
//        return mDlg;
//    }
}
