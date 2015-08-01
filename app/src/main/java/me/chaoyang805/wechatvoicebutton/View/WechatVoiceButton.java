package me.chaoyang805.wechatvoicebutton.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import me.chaoyang805.wechatvoicebutton.R;

/**
 * Created by chaoyang805 on 2015/8/1.
 */
public class WechatVoiceButton extends Button {

    private static final int STATE_NORMAL = 0x01;
    private static final int STATE_RECORDING = 0x02;
    private static final int STATE_WANT_CANCEL = 0x03;

    private static final int DISTANCE_Y_CANCEL = 70;

    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false;

    public WechatVoiceButton(Context context) {
        this(context, null);
    }

    public WechatVoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isRecording = true;
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (wantCancel(x, y)) {
                        changeState(STATE_WANT_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCurState == STATE_RECORDING) {
                    //release
                    //callback to activity
                }else if (mCurState == STATE_WANT_CANCEL) {
                    //mediarecorder.cancel
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantCancel(int x, int y) {

        if (x < 0 || x > getWidth()) {
            return  true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
        }
        switch (state) {
            case STATE_NORMAL:
                setBackgroundResource(R.drawable.button_recorder_normal);
                setText(R.string.recorder_button_normal);
                break;
            case STATE_RECORDING:
                setBackgroundResource(R.drawable.button_recorder_recording);
                setText(R.string.recorder_button_recording);
                break;
            case STATE_WANT_CANCEL:
                setBackgroundResource(R.drawable.button_recorder_recording);
                setText(R.string.recorder_button_want_cancel);
                break;
        }
    }
}
