package me.chaoyang805.wechatvoicebutton;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.chaoyang805.wechatvoicebutton.View.WechatVoiceButton;
import me.chaoyang805.wechatvoicebutton.adapter.WechatVoiceAdapter;

public class MainActivity extends AppCompatActivity implements WechatVoiceButton.OnAudioFinishRecordListener, AdapterView.OnItemClickListener {

    private ListView mListView;
    private ArrayAdapter<Record> mAdapter;
    private List<Record> mDatas;
    private WechatVoiceButton wechatVoiceButton;
    private View mAnimView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listview);
        wechatVoiceButton = (WechatVoiceButton) findViewById(R.id.voice_button);
        wechatVoiceButton.setOnAudioFinishRecordListener(this);
        mDatas = new ArrayList<>();
        mAdapter = new WechatVoiceAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onFinish(float duration, String filePath) {
        Record record = new Record(duration, filePath);
        mDatas.add(record);
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mDatas.size() - 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //播放动画
        if (mAnimView != null) {
            mAnimView.setBackgroundResource(R.drawable.adj);
            mAnimView = null;
        }
        mAnimView = view.findViewById(R.id.recorder_anim);
        mAnimView.setBackgroundResource(R.drawable.anim_wave);
        AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
        anim.start();
        //播放音频
        MediaManager.playSound(mAdapter.getItem(position).getFilePath(),
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.drawable.adj);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    public class Record {
        public Record(float duration, String filePath) {
            this.duration = duration;
            this.filePath = filePath;
        }

        float duration;
        String filePath;

        public float getDuration() {
            return duration;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}
