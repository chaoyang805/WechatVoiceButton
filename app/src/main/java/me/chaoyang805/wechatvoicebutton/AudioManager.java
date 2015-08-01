package me.chaoyang805.wechatvoicebutton;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by chaoyang805 on 2015/8/1.
 */
public class AudioManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurFilePath;
    private boolean isPrepared = false;

    public String getCurrentFilePath() {
        return mCurFilePath;
    }

    public interface AudioStateListener {
        void wellPrerared();
    }

    private AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    private static AudioManager mInstance;

    private AudioManager(String dir) {
        mDir = dir;
    }

    ;

    public static AudioManager getInstance(String dir) {

        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    public void prepareAudio() {
        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = generateFileName();
            File file = new File(dir, fileName);

            mCurFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置mediarecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置编码格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrerared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机生成文件名
     *
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            try {
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    public void release() {
        if (isPrepared) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
        }
        mMediaRecorder = null;

    }

    public void cancel() {
        release();
        if (mCurFilePath != null) {
            File file = new File(mCurFilePath);
            file.delete();
            mCurFilePath = null;
        }

    }
}
