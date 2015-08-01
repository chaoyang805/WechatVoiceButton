package me.chaoyang805.wechatvoicebutton;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by chaoyang805 on 2015/8/1.
 */
public class DialogManager {

    private Dialog mDialog;
    private Context mContext;
    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLabel;

    public DialogManager(Context context) {
        mContext = context;
    }

    public void showRecorderDialog() {
        mDialog = new Dialog(mContext, R.style.ThemeRecorderDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_dialog,null);
        mDialog.setContentView(view);
        mIcon = (ImageView) mDialog.findViewById(R.id.iv_icon);
        mVoice = (ImageView) mDialog.findViewById(R.id.iv_voice);
        mLabel = (TextView) mDialog.findViewById(R.id.tv_label);

        mDialog.show();
    }

    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLabel.setText(R.string.up_finger_to_cancel);
        }
    }

    public void wantCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLabel.setText(R.string.recorder_button_want_cancel);
        }
    }

    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_too_short);
            mLabel.setText(R.string.voice_too_short);
        }

    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {

            int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }
}
