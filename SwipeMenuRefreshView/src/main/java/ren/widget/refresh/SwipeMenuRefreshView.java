package ren.widget.refresh;

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
    private static final int SWIPE_MENU_OPEN = 2;
    private static final int SWIPE_MENU_CLOSE = 3;
    private int pressX, pressY;//按下时的X Y坐标
    private int interceptStatus = 0; //是否拦截此次事件
    private int touchSlop = 50; //滑动距离判断
    private ListView listView;
    private SwipeLayout currentSwipeLayout;//侧滑菜单栏

    public SwipeMenuRefreshView(Context context) {
        super(context);
        init(context);
    }

    public SwipeMenuRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop(); //获取move的判断距离
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ListView)
                listView = (ListView) getChildAt(i);//获取listview
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (listView == null)
            return super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressX = (int) ev.getX(); //记录按下的X坐标
                pressY = (int) ev.getY();//记录按下的Y坐标
                if (currentSwipeLayout != null && currentSwipeLayout.getOpenStatus() != SwipeLayout.Status.Close) { //如果当前有打开或者正在打开的SwipeLayout
                    Log.d(TAG, "currentSwipeLayout.getOpenStatus() " + currentSwipeLayout.getOpenStatus());
                    interceptStatus = SWIPE_MENU_CLOSE;//此次用户操作为关闭SwipeLayout
                    Rect rect = new Rect();
                    currentSwipeLayout.getHitRect(rect);
                    //判断当前点击X Y坐标是否在当前SwipeLayout中，即用户是否点击这个SwipeLayout，有就不拦截时间交由SwipeLayout自己处理
                    if (rect.contains(pressX, pressY)) {
                        return false;
                    }
                    //如果没有就关闭并且拦截此时间顺序中所有事件
                    currentSwipeLayout.close();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //判断滑动距离是否是正常的滑动
                if (Math.abs(ev.getY() - pressY) < touchSlop && Math.abs(ev.getX() - pressX) < touchSlop)
                    return super.onInterceptTouchEvent(ev);
                //如果用户是滑动listview则交由父类onInterceptTouchEvent处理
                if (interceptStatus == REFRESH_STATUS)
                    return super.onInterceptTouchEvent(ev);
                    //用户如果是滑出SwipeLayout则不拦截时间交由SwipeLayout处理
                else if (interceptStatus == SWIPE_MENU_OPEN)
                    return false;
                //根据滑动角度判断用户是滑出SwipeLayout还是Listview
                double angle = Math.atan((ev.getY() - pressY) / (ev.getX() - pressX));//计算滑动的角度
                int degrees = (int) Math.toDegrees(angle);
                degrees = Math.abs(degrees);
                //大于45度则判断为Listview滑动
                if (degrees > 45) {
                    Log.d(TAG, "正在上下滑动");
                    //如果当前是SwipeLayout内点击的事件序列则不允许滑动
                    if (interceptStatus == SWIPE_MENU_CLOSE)
                        return true;
                    interceptStatus = REFRESH_STATUS; //标记为Listview滑动
                    return super.onInterceptTouchEvent(ev);
                } else { ////小于45度则判断为SwipeLayout滑动
                    Log.e(TAG, "正在左右滑动");
                    currentSwipeLayout = getCurrentSwipeLayout(); //获取当前滑出的SwipeLayout
                    interceptStatus = SWIPE_MENU_OPEN; //标记为SwipeLayout滑动
                    return false;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                restoreStatus();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.d("TAG", "onTouchEvent  SwipeMenuRefreshView" + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (interceptStatus == SWIPE_MENU_CLOSE)//如果是SwipeLayout关闭事件序列则拦截事件
                    return true;
                else
                    break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                restoreStatus();
                break;
            default:
                break;
        }
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
        if (listView.getChildAt(position) instanceof SwipeLayout)
            return (SwipeLayout) listView.getChildAt(position);//获取当前SwipeLayout
        else
            return null;
    }

    /**
     * 本次时间序列结束后还原所有变量
     */
    private void restoreStatus() {
        if (interceptStatus == SWIPE_MENU_CLOSE) {
            currentSwipeLayout.close();
            currentSwipeLayout = null;
        }
        interceptStatus = NORMAL_STATUS; //重置标记
    }
}
