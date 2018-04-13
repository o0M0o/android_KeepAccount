package wxm.KeepAccount.ui.base.SwipeLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import wxm.KeepAccount.R;

/**
 * @author      chenjiawei
 * @version     create：2015/12/9
 *              modify by WangXM at 2018/04/13
 */
public class SwipeLayout extends LinearLayout {
    /**
     * listener for slide
     */
    public interface OnSlideListener {
        int SLIDE_STATUS_OFF = 0;
        int SLIDE_STATUS_START_SCROLL = 1;
        int SLIDE_STATUS_ON = 2;

        /**
         * event handler
         * @param view      slide view
         * @param status    slide status
         */
        void onSlide(View view, int status);
    }

    private LinearLayout mContentView;
    private RelativeLayout mRightView;

    private Scroller mScroller;
    private OnSlideListener mOnSlideListener;
    private int mHolderWidth = 120;
    private int mLastX = 0;
    private int mLastY = 0;
    private static final int TAN = 2;

    private int mSlideState = OnSlideListener.SLIDE_STATUS_OFF;

    public SwipeLayout(Context context) {
        super(context);
        initView();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * set content view
     * content view used for show content
     * @param view   content
     */
    public void setContentView(View view) {
        mContentView.addView(view);
    }

    /**
     * set right view
     * right view used do operation
     * @param v     operation view
     */
    public void setRightView(View v) {
        mRightView.addView(v);
    }

    /**
     * set slide listener
     * @param onSlideListener       listener for slide
     */
    public void setOnSlideListener(OnSlideListener onSlideListener) {
        mOnSlideListener = onSlideListener;
    }

    /**
     * close slide status
     */
    public void shrink() {
        if (getScrollX() != 0) {
            this.smoothScrollTo(0, 0);
        }
    }

    //如果其子View存在消耗点击事件的View，那么SwipeLayout的onTouchEvent不会被执行，
    // 因为在ACTION_MOVE的时候返回true，执行其onTouchEvent方法
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlide(this,
                            OnSlideListener.SLIDE_STATUS_START_SCROLL);
                    mSlideState = OnSlideListener.SLIDE_STATUS_START_SCROLL;
                }
                //这里需要记录mLastX，mLastY的值，不然当SwipeLayout已经处于开启状态时，
                // 用于再次滑动SwipeLayout时，会先立即复原到关闭状态，用户体验不太好
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                return false;

            case MotionEvent.ACTION_MOVE:
                //返回值为true表示本次触摸事件由自己执行，即执行SwipeLayout的onTouchEvent方法
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                //这里作用是来比较X、Y轴的滑动距离，如果X轴的滑动距离小于两倍的Y轴滑动距离，则不执行SwipeLayout的滑动事件
                if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
                    return false;
                }
                return true;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int scrollX = getScrollX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                //如果SwipeLayout是在譬如ScrollView、ListView这种可以上下滑动的View中
                //那么当用户的手指滑出SwipeLayout的边界，那么将会触发器ACTION_CANCEL事件
                //如果此情形发生，那么SwipeLayout将会处于停止状态，无法复原。
                //增加下面这句代码，就是告诉父控件，不要cancel我的事件，我的事件我继续处理。
                getParent().requestDisallowInterceptTouchEvent(true);
                int newScrollX = scrollX - deltaX;
                if (deltaX != 0) {
                    if (newScrollX < 0) {
                        //最小的滑动距离为0
                        newScrollX = 0;
                    } else if (newScrollX > mHolderWidth) {
                        //最大的滑动距离就是mRightView的宽度
                        newScrollX = mHolderWidth;
                    }
                    this.scrollTo(newScrollX, 0);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                int newScrollX = 0;
                //如果已滑动的距离满足下面条件，则SwipeLayout直接滑动到最大距离，不然滑动到最小距离0
                if (scrollX - mHolderWidth * 0.75 > 0) {
                    newScrollX = mHolderWidth;
                }
                this.smoothScrollTo(newScrollX, 0);
                if (mOnSlideListener != null) {
                    if (newScrollX == 0) {
                        mOnSlideListener.onSlide(this, OnSlideListener.SLIDE_STATUS_OFF);
                        mSlideState = OnSlideListener.SLIDE_STATUS_OFF;
                    } else {
                        mOnSlideListener.onSlide(this, OnSlideListener.SLIDE_STATUS_ON);
                        mSlideState = OnSlideListener.SLIDE_STATUS_ON;
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(false);
            }

            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return true;
    }

    /**
     * get slide status
     * @return      slide status
     */
    public int getSlideState() {
        return mSlideState;
    }

    /**
     * set slide width
     * @param newWidth      new slide width
     */
    public void setSlideWidth(int newWidth) {
        mHolderWidth = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, newWidth,
                getResources().getDisplayMetrics()));
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /// PRIVATE START
    /**
     * scroll self
     * @param destX     destination x
     * @param destY     destination y
     */
    private void smoothScrollTo(int destX, int destY) {
        // 缓慢滚动到指定位置
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    /**
     * init self
     */
    private void initView() {
        Context mContext = getContext();
        mScroller = new Scroller(mContext);
        setOrientation(LinearLayout.HORIZONTAL);
        View.inflate(mContext, R.layout.container_swipelayout, this);
        mContentView = findViewById(R.id.view_content);
        mRightView = findViewById(R.id.view_right);
        mHolderWidth = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHolderWidth,
                getResources().getDisplayMetrics()));
    }

    /// PRIVATE END
}
