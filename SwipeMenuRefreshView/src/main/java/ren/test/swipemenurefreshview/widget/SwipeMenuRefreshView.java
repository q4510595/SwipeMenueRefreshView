package ren.test.swipemenurefreshview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;

/**
 * Created by Ren on 2017/10/16 0016.
 * TODO
 */

public class SwipeMenuRefreshView extends SwipeRefreshLayout {

    private static final String TAG = "SwipeMenuRefreshView";
    private static final int NORMAL_STATUS = 0;
    private static final int REFRESH_STATUS = 1;
    private static final int SWIPE_MENU_STATUS = 2;
    private static final int SWIPE_MENU_CLOSING = 3;
    private int pressX, pressY;//按下时的X Y坐标
    private int interceptStatus = 0; //是否拦截此次事件
    private int touchSlop = 80;
    private ListView listView;
    private SwipeLayout currentSwipeLayout;

    public SwipeMenuRefreshView(Context context) {
        super(context);
        init(context);
    }

    public SwipeMenuRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ListView)
                listView = (ListView) getChildAt(i);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (listView == null)
            return super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressX = (int) ev.getX();
                pressY = (int) ev.getY();
                if (currentSwipeLayout != null && currentSwipeLayout.getOpenStatus() == SwipeLayout.Status.Open) {
                    interceptStatus = SWIPE_MENU_CLOSING;
                    Rect rect = new Rect();
                    currentSwipeLayout.getHitRect(rect);
                    if (rect.contains(pressX, pressY)) {
                        return false;
                    }
                    currentSwipeLayout.close();
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (interceptStatus == REFRESH_STATUS)
                    return super.onInterceptTouchEvent(ev);
                else if (interceptStatus == SWIPE_MENU_STATUS)
                    return false;
                else if (interceptStatus == SWIPE_MENU_CLOSING)
                    return true;
                if (Math.abs(ev.getY() - pressY) < touchSlop && Math.abs(ev.getX() - pressX) < touchSlop)
                    return super.onInterceptTouchEvent(ev);
                double angle = Math.atan((ev.getY() - pressY) / (ev.getX() - pressX));//计算滑动的角度
                int degrees = (int) Math.toDegrees(angle);
                degrees = Math.abs(degrees);
                if (degrees > 45) {
                    Log.d(TAG, "正在上下滑动");
                    interceptStatus = REFRESH_STATUS;
                    return super.onInterceptTouchEvent(ev);
                } else {
                    Log.e(TAG, "正在左右滑动");
                    currentSwipeLayout = getCurrentSwipeLayout();
                    interceptStatus = SWIPE_MENU_STATUS;
                    return false;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                interceptStatus = NORMAL_STATUS;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (interceptStatus == SWIPE_MENU_CLOSING)
                    return true;
                else
                    break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                interceptStatus = NORMAL_STATUS;
                break;
        }
        Log.d("TAG", "onTouchEvent  SwipeMenuRefreshView");
        return super.onTouchEvent(ev);
    }

    /**
     * 获取当前点击位置的子View即SwipeLayout
     *
     * @return 当前的SwipeLayout
     */
    private SwipeLayout getCurrentSwipeLayout() {
        int position = listView.pointToPosition(pressX, pressY);//根据按下的X Y坐标获取position
        position = position - listView.getFirstVisiblePosition();//获取当前显示view的下标
        return (SwipeLayout) listView.getChildAt(position);//获取当前SwipeLayout
    }
}
