package com.ryane.teach_circlebanner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: lijianchang
 * Create Time: 2017/7/5.
 * Email: lijianchang@yy.com
 */

class MyAdapter extends PagerAdapter {
    private final ArrayList<Object> mViewCaches = new ArrayList<>();     //缓存ViewPager废弃的对象
    private List<String> mInfos;    //数据源
    private Context context;

    public MyAdapter(List<String> mInfos, Context context) {
        this.mInfos = mInfos;
        this.context = context;
    }

    public void setmInfos(List<String> mInfos) {
        this.mInfos = mInfos;
    }

    @Override
    public int getCount() {
        if (null != mInfos) {
            // 当只有一张图片的时候，不可滑动
            if (mInfos.size() == 1) {
                return 1;
            } else {
                // 否则循环播放滑动
                return Integer.MAX_VALUE;
            }
        } else {
            return 0;  // mInfos为空时返回0
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mInfos != null && mInfos.size() > 0) {
            ImageView imageView;
            // 当缓存集合数量为0时
            if (mViewCaches.isEmpty()) {
                imageView = new ImageView(context);   // 新建一个ImageView
                // 设置ImageView的基本宽高，和ScaleType
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                // 当缓存集合有数据时，复用，然后缓存不再持有它的引用
                imageView = (ImageView) mViewCaches.remove(0);
            }
            // 使用Picasso加载网络图片
            Picasso.with(context).load(mInfos.get(position % mInfos.size())).into(imageView);

            // 把ViewPager这个布局加载ImageView进来
            container.addView(imageView);
            return imageView;
        } else {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 当页面不可见时，该View就会被ViewPager传到这个方法的object中，我们拿到该object转为ImageView
        ImageView imageView = (ImageView) object;
        // 在ViewPager布局中移除这个view
        container.removeView(imageView);
        // 加到缓存里
        mViewCaches.add(imageView);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
