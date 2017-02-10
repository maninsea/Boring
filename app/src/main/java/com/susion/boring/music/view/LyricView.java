package com.susion.boring.music.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.susion.boring.R;
import com.susion.boring.music.model.Lyric;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by susion on 17/2/6.
 *
 * the each line of lyric format must is
 *   [00:00.00] 作词 : 吴梦奇/胡海泉/胥文雅 (aka 文雅) \n
 */
public class LyricView extends LinearLayout{

    private String mLyrics;
    private Activity mContext;
    private BufferedReader mReader;
    private List<Lyric> mData = new ArrayList<>();

    private Map<String, Lyric> mLyricMap = new HashMap();

    private int mPaddingTop;
    private int mLyricHeight;

    private ListView mLyricContainer;
    private BaseAdapter mAdapter;

    public LyricView(Context context) {
        super(context);
        init(context);
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr, Activity mContext) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public LyricView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Activity mContext) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = (Activity) context;
        mLyricContainer = new ListView(mContext);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mLyricContainer.setLayoutParams(containerParams);
        mLyricContainer.setDivider(null);
        mLyricContainer.setVerticalScrollBarEnabled(false);
        mLyricContainer.setScrollbarFadingEnabled(false);
        addView(mLyricContainer);
    }

    public void setLyrics(String lyrics) {
        loadLyrics(lyrics);
        initLyricView();
    }

    private void initLyricView() {

        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mData.size();
            }

            @Override
            public Object getItem(int i) {
                return mData.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int pos, View convertView, ViewGroup viewGroup) {
                TextView tv;
                if (convertView == null) {
                    tv  = getLyricTextView();
                    convertView = tv;
                } else {
                    tv = (TextView) convertView;
                }

                tv.setText(mData.get(pos).lyric);

                return convertView;
            }
        };
        mLyricContainer.setAdapter(mAdapter);

        post(new Runnable() {
            @Override
            public void run() {
                View listItem = mAdapter.getView(0, null, mLyricContainer);
                int widthSpec = MeasureSpec.makeMeasureSpec(mLyricContainer.getWidth(), MeasureSpec.AT_MOST);
                if (listItem.getLayoutParams() == null) {
                    listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                listItem.measure(widthSpec, 0);
                mLyricHeight = listItem.getMeasuredHeight();
            }
        });
    }

    @NonNull
    private TextView getLyricTextView() {
        TextView lyricView = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lyricView.setLayoutParams(params);
        lyricView.setGravity(Gravity.CENTER_HORIZONTAL);
        lyricView.setTextColor(getResources().getColor(R.color.lyric_color));
        lyricView.setTextSize(15);
        return lyricView;
    }

    // time format 00:00
    public void setCurrentLyricByTime(String currentTime){
        Lyric lyric = mLyricMap.get(currentTime);
        if (lyric != null) {
            mLyricContainer.smoothScrollToPosition(lyric.pos);
        }
    }

    private void loadLyrics(String lyrics){
        if (!mData.isEmpty()) {
            mData.clear();
            mLyricMap.clear();
        }
        mLyrics = lyrics;
        mReader = new BufferedReader(new StringReader(mLyrics));

        String lyricStr;
        int pos = 0;
        try{
            while ((lyricStr = mReader.readLine()) != null) {
                translateStringToLyric(lyricStr, pos);
                pos++;
            }
            mReader.close();
        }catch(Exception e){

        }
    }

    private void translateStringToLyric(String lyricStr, int pos) {
        String time = lyricStr.substring(lyricStr.indexOf("[")+1, lyricStr.indexOf("]"));
        String l = lyricStr.substring(lyricStr.indexOf("]")+1);
        Lyric lyric = new Lyric(time, l, pos);
        mData.add(lyric);
        mLyricMap.put(time.substring(0, time.indexOf(".")), lyric);
    }

}
