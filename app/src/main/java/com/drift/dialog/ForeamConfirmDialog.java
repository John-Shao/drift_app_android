package com.drift.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drift.camcontroldemo.R;
import com.drift.foreamlib.util.StringUtil;

public class ForeamConfirmDialog extends Dialog {
	private int mIconRes;
	private int mSecondFade;
	private String mText;
	private ImageView iv_title = null;
	private TextView tv_title = null;
	private ViewGroup ll_two_buttons_bg = null;
	private ViewGroup rl_left_button = null;
	private ViewGroup rl_right_button = null;
	private TextView tv_one_button = null;
	private int mStyle;
	private Activity mContext;
	
	private String mOneButtonText = null;
	//private boolean isDismiss=false;
	public final static int STYLE_ONE_BUTTON = 1;
	public final static int STYLE_TWO_BUTTONS = 2;
	private OnClickListener mOnClickConfirmListener = null;

	public ForeamConfirmDialog(Activity context, int style) {
		super(context, R.style.no_bg_dialog);
		mStyle = style;
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_foreamconfirm);
		initView();
		getWindow().setWindowAnimations(R.style.Animations_PopUpMenu_Appear);
	}

	private void initView() {
		iv_title = (ImageView) findViewById(R.id.iv_title);
		tv_title = (TextView) findViewById(R.id.tv_title);
		ll_two_buttons_bg = (ViewGroup) findViewById(R.id.ll_two_buttons_bg);
		rl_left_button = (ViewGroup) findViewById(R.id.rl_left_button);
		rl_right_button = (ViewGroup) findViewById(R.id.rl_right_button);
		tv_one_button = (TextView) findViewById(R.id.tv_one_button);
		
		if(!StringUtil.isNon(mOneButtonText)){
			tv_one_button.setText(mOneButtonText);
		}
		if (!StringUtil.isNon(mText)) {
			tv_title.setText(mText);
		} else {
			tv_title.setVisibility(View.GONE);
		}
		if (mIconRes != -1) {
			iv_title.setImageResource(mIconRes);
		} else {
			iv_title.setVisibility(View.GONE);
		}
		switch (mStyle) {
		case STYLE_ONE_BUTTON:
			ll_two_buttons_bg.setVisibility(View.GONE);
			break;
		case STYLE_TWO_BUTTONS:
			tv_one_button.setVisibility(View.GONE);
			break;
		}
		rl_left_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		rl_right_button.setOnClickListener(clickConfirmListener);
		tv_one_button.setOnClickListener(clickConfirmListener);
	}

	private View.OnClickListener clickConfirmListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mOnClickConfirmListener != null) {
				mOnClickConfirmListener.onClick(ForeamConfirmDialog.this,1);
			}
			dismiss();

		}
	};
	
	public void setOneButtonText(int strRes){
		setOneButtonText( getContext().getString(strRes));
		
	}
	public void setOneButtonText(String str){
		mOneButtonText = str;
		if(tv_one_button!=null){
			tv_one_button.setText(str);
		}
	}
	public void setData(int iconRes, String message, OnClickListener ls) {
		setData(iconRes,message,-1,ls);
		
	}
	public void setData(int iconRes, String message, int buttonText,OnClickListener ls) {
		if(buttonText!=-1){
			mOneButtonText = getContext().getString(buttonText);
		}
		mText = message;
		if(tv_title!=null)tv_title.setText(mText);
		mIconRes = iconRes;
		if(iv_title!=null)iv_title.setImageResource(mIconRes);
		mOnClickConfirmListener = ls;
		
	}
	@Override
	public void show() {
		if(mContext.isFinishing())return;
		super.show();
		// if (mSecondFade != 0) {
		// iv_title.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// if (ForeamConfirmDialog.this.isShowing()) {
		// dismiss();
		// }
		// }
		// }, mSecondFade);
		// }
	}

}
