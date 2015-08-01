package me.chaoyang805.wechatvoicebutton.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.chaoyang805.wechatvoicebutton.MainActivity;
import me.chaoyang805.wechatvoicebutton.R;

/**
 * Created by chaoyang805 on 2015/8/1.
 */
public class WechatVoiceAdapter extends ArrayAdapter<MainActivity.Record> {

    private int mMinItemWidth;
    private int mMaxItemWidth;
    private LayoutInflater mInflater;


    public WechatVoiceAdapter(Context context, List<MainActivity.Record> datas) {
        super(context, -1, datas);
        mInflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_recorder, null, false);
            holder = new ViewHolder();
            holder.duration = (TextView) convertView.findViewById(R.id.tv_duration);
            holder.length = convertView.findViewById(R.id.frame_length);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.duration.setText(Math.round(getItem(position).getDuration()) + "\"");
        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        lp.width = (int) (mMinItemWidth + mMaxItemWidth / 60f * getItem(position).getDuration());

        return convertView;
    }

    private class ViewHolder {
        TextView duration;
        View length;
    }
}
