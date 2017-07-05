*人的理想志向往往和他的能力成正比。 —— 约翰逊*

# 摘要 #

![](http://osjnd854m.bkt.clouddn.com/test.jpg)

图片轮播已经成为了很多App必备功能，且不说它具有炫酷的视觉效果，对于很多靠广告收入的App来说，图片轮播是必不可少的，因为它通过轮播减少了广告位对界面的占用。虽然图片轮播非常的常用了，但是相信很多开发者对图片轮播的实现还是一知半晓，作为一个有抱负、有追求的程序员，我们还是希望刨根问底，所以，必要时重复造下轮子还是有必要的，何况图片轮播并没有我们想象的那么困难，尤其在Android技术如此成熟的今天，结合官方控件来实现还是非常容易的，当然，这篇文章是比较适合刚入门的Android开发者和初级Android开发者，我还是不敢在大牛面前班门弄斧的，希望大牛们多多包涵。

言归正传，本文标题是**手把手教学**，所以本文会用简单粗俗的语言教大家，如何从`想`->`做`->`实现`,希望做到真正授之以渔，而非授之以鱼。

# Stpe1.脑子想 #

在做任何东西之前的第一步，就是我们得在脑子里有一个思考过程。

就像我们做这个图片轮播，首先我们就会想，在Google提供的官方Api中，有没有类似的控件已经有实现相似的功能？然后我们在脑子里想啊想，终于，想到了两个比较常用、比较流行的控件 `ViewPager`、`RecyclerView`。`尤其ViewPager`，它已经基本实现了图片轮播的功能，只是缺少了自动播放；而`RecyclerView`我们都知道，它已经支持了水平的瀑布流，大家试想，当我们将`RecyclerView`设为水平布局，并且每一个item宽度为屏幕宽度，同样我们也可以实现图片轮播的功能。假如，你真的没有想到这两个控件，你可以通过`自定义View`来实现，当然，这个过程相对会比较复杂。

OK。在经历了上面一段脑子思考之后，我就选择了采用`ViewPager`来实现，因为它是最接近图片轮播的一个官方控件。那么我们还会想`ViewPager`距离我们理想的图片轮播到底有多少差距，首先，它还不支持自动播放，其次，它并不能从最后一张滑动回第一张。

经历完上面的脑力劳动之后，就该进行接下来一步，动手做！

# Step2.动手做 #

从脑子想完之后，我们选择了`ViewPager`来实现图片轮播，但是面临了两个需要解决的问题：

1. `ViewPager`如何实现自动播放？

2. `ViewPager`如何实现从最后一张滑到第一张？

有了问题，我们就会想着怎么去解决。

- 第一个问题比较简单， 我们都知道ViewPager的Api里有一个方法叫做`setCurrentItem(int position)`，顾名思义，就是设置当前的Item为数据源的第position个数据，那么我们就可以通过一个runnable的`run()`方法里面调用这个方法，然后在每次页面切换完成时，延时执行这个runnable即可。

- 第二个问题会比较复杂，我们都知道ViewPager是无法从最后一页设置到第一页，但是，我们能不能将ViewPager的Adapter里面设置它的`size()`为一个非常大的值呢？这样我们就可以实现无限循环了。那我们怎么保证数据的正确性呢？假如数据源只有几个数据，而Adapter里面的`size()`非常大，我们就可以通过取余的方式来保证滑动页面一直对应着数据源的几个数据。还有就是，假如Adapter的`size()`非常大，我们在Adapter的`instantiateItem(ViewGroup container, int position)`中就会需要返回很多new出来的View，这样子会造成不必要的内存浪费，所以，我们可以通过一个ArrayList<Object>来作为缓存，当我们Adapter的`destroyItem(ViewGroup container, int position, Object object)`方法中，将废弃的object存到缓存中，重复利用，避免了内存浪费。

这两个问题就这样轻松地被解决了，也许会有人问，为什么这part叫动手做呢，不是想想就好了吗？要知道，这是我已经想好的思路，假如你面对的是一个没有接触过的问题，假如你不动动手在纸上构思，你的空想并不能给你带来什么。

那么接下来，我们就该实现了！

# Step3.敲代码 #

当你梳理清楚了前面两步的问题，那么当你敲代码实现的时候就非常简单了。

## (1) 实现MyCircleBanner继承ViewPager ##

首先，实现一个类MyCircleBanner继承于ViewPager,然后重写构造方法。

	public class MyCircleBanner extends ViewPager {
	    public MyCircleBanner(Context context) {
	        super(context);
	    }
	}

## (2) 实现一个ViewPager的Adapter ##

首先，在MyCircleBanner实现一个内部类BannerAdapter继承PagerAdapter，它要求我们必须重写`getCount() `和`isViewFromObject(View view, Object object)`，并设置全局变量`mViewCaches`和`mInfos`,其中`mViewCaches`用以缓存页面没被使用时被ViewPager置空的对象，`mInfos`作为数据源集合。

	class BannerAdapter extends PagerAdapter{
		private final ArrayList<Object> mViewCaches = new ArrayList<>();    //缓存ViewPager废弃的对象
        private List<String> mInfos;	//数据源

        public BannerAdapter(List<String> mInfos) {
            this.mInfos = mInfos;
        }

	    @Override
	    public int getCount() {
	        return 0;
	    }
	
	    @Override
	    public boolean isViewFromObject(View view, Object object) {
	        return false;
	    }
	}

在第二步中，我们讨论到了一个问题，那就是，当`ViewPager`滑到最后一页时无法滑到第一页，所以我们可以再getCount()方法里面下手，返回一个很大的值，我们取为`Integer.MAX_VALUE`，即`2^31 - 1`，非常大的一个数，足以模拟近乎无限循环，所以`getCount()`可以这么实现：

	@Override
    public int getCount() {
        if (null != mInfos) {
            // 当只有一张图片的时候，不滑动，返回1即可
            if (mInfos.size() == 1) {
                return 1;
            } else {
                // 否则循环播轮播，返回Int型的最大值
                return Integer.MAX_VALUE;
            }
        }
        else return 0;  // mInfos为空时返回0
    }

而`isViewFromObject(View view, Object object)`则直接这样写：

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

在BannerAdapter中，除了实现这两个方法还不够的，还需要实现`instantiateItem`和`destroyItem`这两个方法。

那么这两个方法是什么意思呢？首先`public Object instantiateItem(ViewGroup container, int position)`这个方法是说，当ViewPager显示初始化到该页面时，需要执行的方法，我们可以看到参数有一个`container`，即整个ViewPager的外布局，`position`就是初始化到该页面的位置，并且需要我们返回一个Object类型，可以理解为返回我们显示当前ViewPager页面的View，做一个轮播图，我们可以直接返回一个ImageView。而另一个方法`void destroyItem(ViewGroup container, int position, Object object)`，就是当该页面已经超出了用户的可视范围时，需要执行的方法。

在实现这个方法之前，我们来讲解一下ViewPager这两个方法的执行机制：

![](http://osjnd854m.bkt.clouddn.com/des.png)

所以ViewPager每一次都是只有当前显示页和相邻两页被初始化，试想，假如我们将`size()`设置到很大，我们一直向右滑，不断执行`instantiateItem`方法，然后我们在`instantiateItem`方法里不断地new一个ImageView出来，要知道`destroyItem`默认是空实现，那么就会有越来越多没有用到的ImageView占用了内存，所以这时做缓存非常有必要，那么这两个方法的实现可以如下：

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

所以，我们的BannerAdapter也就大功告成，整个BannerAdapter的代码如下：

	class MyAdapter extends PagerAdapter {
	    private final ArrayList<Object> mViewCaches = new ArrayList<>();     //缓存ViewPager废弃的对象
	    private List<String> mInfos;    //数据源
	    private Context context;
	
	    public MyAdapter(List<String> mInfos, Context context) {
	        this.mInfos = mInfos;
	        this.context = context;
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


## (3) 实现自动轮播功能 ##

首先需要实现一个Runnable任务，主要就是调用`setCurrentItem（）`方法来设置ViewPager滑动到下一页，当然要判断一些极端case，例如滑动到最右边时，处理为返回到第一个。

而`getInitPosition()`则是获取到从0-Integer.MAX_VALUE的中间左右位置，该位置并要和数据源的第一个元素取余为0，这样就保证了ViewPager默认是从0-Integer.MAX_VALUE的中间位置开始滑动，使得它左右都可以实现近乎无限循环滑动。

`startAdvertPlay()`则是把任务延时加到任务队列，这里设置延时3s，`stopAdvertPlay()`则是在ViewPager被Destroy时，清空任务队列。

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


## (4) 设置ViewPager的监听器 ##

实现`OnPageChangeListener`可以完成自动轮播功能，当ViewPager每次切换界面完成时都会执行三个方法，之所以在`onPageScrollStateChanged()`方法里面调用`startAdvertPlay()`是因为当手指按下ViewPager时，我们不会执行这个任务，只有当手指离开ViewPager时，才会执行。

	/**
     * 轮播图片状态监听器
     */
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            // 获取当前的位置
            mSelectedIndex = position;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
			// 当手指离开屏幕时，才会执行
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                startAdvertPlay();
            }
        }
    };

## (5) 提供MyCircleBanner调用接口 ##

在完成了上面接口和方法实现之后，那么就需要在`MyCircleBanner`内提供接口，传入数据即可实现轮播控件自动播放。

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

所以整个MyCircleBanner的代码是这样的：

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

## (6) 用法 ##

到此为止，自定义的轮播控件已经完成，我们只要在Xml里面添加该控件，像这样：

    <com.ryane.teach_circlebanner.MyCircleBanner
        android:id="@+id/mBanner"
        android:layout_width="match_parent"
        android:layout_height="200dp" />

然后，在Activity的`oncreate()`方法中：

    mBanner = (MyCircleBanner) findViewById(R.id.mBanner);

	// 设置数据源
    List<String> mInfos = new ArrayList<>();
    mInfos.add("http://onq81n53u.bkt.clouddn.com/photo1.jpg");
    mInfos.add("http://onq81n53u.bkt.clouddn.com/photo2.jpg");
    
	// 使用mBanner的接口，直接自动播放 
    mBanner.play(mInfos);

那么，一个轮播控件就完成了。

![](http://osjnd854m.bkt.clouddn.com/pic.gif)


# 后记 #

到此为止，相信大家已经可以自己实现一个图片轮播了，我把自己的实现过程完整地告诉大家，也是希望大家能够在遇到问题时，能够践行`想`->`做`->`实现`这个过程，如何能够静下心来，认真地走这个过程，那么我想很多困难都迎刃而解。

当然，这个Demo只是一个比较简略的实现，在这里强烈安利一波我的一个开源控件：

**[AdPlayBanner](https://github.com/ryanlijianchang/AdPlayBanner)：功能丰富、一键式使用的图片轮播插件**

这个Demo也同步上传到Github，如果大家想看源码，可以移步这里。

Github: