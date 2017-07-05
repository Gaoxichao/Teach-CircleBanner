package com.ryane.teach_circlebanner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: lijianchang
 * Create Time: 2017/7/5.
 * Email: lijianchang@yy.com
 */

public class MyCircleBanner extends ViewPager {
    private int mSelectedIndex = 0;     // 当前下标
    private Handler mUIHandler;
    private List<String> mInfos = new ArrayList<>();


    public MyCircleBanner(Context context) {
        this(context, null);
    }

    public MyCircleBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void play(List<String> mInfos) {
        if (null != mInfos && mInfos.size() > 0) {
            this.mInfos = mInfos;
            mUIHandler = new Handler(Looper.getMainLooper());
            // new一个Adapter
            MyAdapter adapter = new MyAdapter(mInfos, getContext());
            // 设置adapter
            setAdapter(adapter);
            // 设置监听器
            addOnPageChangeListener(mOnPageChangeListener);
            // 设置默认位置为中间位置
            setCurrentItem(getInitPosition());
            if (mInfos.size() >= 1) {
                // 开始自动播放
                startAdvertPlay();
            }
        }
    }

    /**
     * 轮播图片状态监听器
     */
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            Log.d("TAG", position + "");
            // 获取当前的位置
            mSelectedIndex = position;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                startAdvertPlay();
            }
        }
    };

    /**
     * 自动播放任务
     */
    private Runnable mImageTimmerTask = new Runnable() {
        @Override
        public void run() {
            if (mSelectedIndex == Integer.MAX_VALUE) {
                // 当滑到最右边时，返回返回第一个元素
                // 当然，几乎不可能滑到
                int rightPos = mSelectedIndex % mInfos.size();
                setCurrentItem(getInitPosition() + rightPos + 1, true);
            } else {
                // 常规执行这里
                setCurrentItem(mSelectedIndex + 1, true);
            }
        }
    };


    /**
     * 获取banner的初始位置,即0-Integer.MAX_VALUE之间的大概中间位置
     * 保证初始位置和数据源的第1个元素的取余为0
     *
     * @return
     */

    private int getInitPosition() {
        if (mInfos.isEmpty()) {
            return 0;
        }
        int halfValue = Integer.MAX_VALUE / 2;
        int position = halfValue % mInfos.size();
        // 保证初始位置和数据源的第1个元素的取余为0
        return halfValue - position;
    }

    /**
     * 开始广告滚动任务
     */
    private void startAdvertPlay() {
        stopAdvertPlay();
        mUIHandler.postDelayed(mImageTimmerTask, 1000);
    }

    /**
     * 停止广告滚动任务
     */
    private void stopAdvertPlay() {
        mUIHandler.removeCallbacks(mImageTimmerTask);
    }
}
