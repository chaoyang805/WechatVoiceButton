package me.chaoyang805.wechatvoicebutton.View;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import me.chaoyang805.wechatvoicebutton.AudioManager;
import me.chaoyang805.wechatvoicebutton.DialogManager;
import me.chaoyang805.wechatvoicebutton.R;

/**
 * Created by chaoyang805 on 2015/8/1.
 */
public class WechatVoiceButton extends Button implements AudioManager.AudioStateListener {

    private static final int STATE_NORMAL = 0x01;
    private static final int STATE_RECORDING = 0x02;
    private static final int STATE_WANT_CANCEL = 0x03;

    private static final int DISTANCE_Y_CANCEL = 70;

    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false;

    private DialogManager mDialogManager;
    private AudioManager mAudioManager;

    private static final int MSG_AUDIO_WELL_PREPARED = 0x04;
    private static final int MSG_VOICE_CHANGED = 0x05;
    private static final int MSG_DIALOG_DISMISSED = 0x06;
    private static final int MAX_VOICE_LEVEL = 7;
    private float mDuration;
    //是否触发了longClick
    private boolean isReady = false;


    private Runnable mGetVoiceLevel = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mDuration += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_WELL_PREPARED:
                    //
                    mDialogManager.showRecorderDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevel).start();
                    break;
                case MSG_DIALOG_DISMISSED:
                    mDialogManager.dismiss();
                    break;
                case MSG_VOICE_CHANGED:
                    if (isRecording) {
                        mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(MAX_VOICE_LEVEL));
                    }
                    break;
            }
        }
    };

    public WechatVoiceButton(Context context) {
        this(context, null);
    }

    public interface OnAudioFinishRecordListener {
        void onFinish(float seconds, String filePath);
    }

    private OnAudioFinishRecordListener mListener;

    public void setOnAudioFinishRecordListener(OnAudioFinishRecordListener listener) {
        mListener = listener;
    }

    public WechatVoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);

        String dir = Environment.getExternalStorageDirectory() + "/Music/me";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    @Override
    public void wellPrerared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_WELL_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //mAudioManager.prepareAudio();
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
                if (!isReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mDuration < 0.6f) {
                    mDialogManager.tooShort();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISSED, 1300);
                    mAudioManager.cancel();
                } else if (mCurState == STATE_RECORDING) {
                    //release
                    //callback to activity
                    mDialogManager.dismiss();
                    mAudioManager.release();
                    if (mListener != null) {
                        mListener.onFinish(mDuration, mAudioManager.getCurrentFilePath());
                    }
                } else if (mCurState == STATE_WANT_CANCEL) {
                    mAudioManager.cancel();
                    mDialogManager.dismiss();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        isRecording = false;
        isReady = false;
        changeState(STATE_NORMAL);

        mDuration = 0;
    }

    private boolean wantCancel(int x, int y) {

        if (x < 0 || x > getWidth()) {
            return true;
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
                if (isRecording) {
                    mDialogManager.recording();
                }
                break;
            case STATE_WANT_CANCEL:
                setBackgroundResource(R.drawable.button_recorder_recording);
                setText(R.string.recorder_button_want_cancel);
                mDialogManager.wantCancel();
                break;
        }
    }


}
