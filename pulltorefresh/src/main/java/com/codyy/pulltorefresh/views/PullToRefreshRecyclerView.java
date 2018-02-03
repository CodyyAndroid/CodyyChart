package com.codyy.pulltorefresh.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.codyy.pulltorefresh.PullToRefreshBase;
import com.codyy.pulltorefresh.R;


/**
 * 下拉刷新RecyclerView.
 * 用法：{@link PullToRefreshBase }
 * {@link PullToRefreshBase#setOnPullEventListener(OnPullEventListener)}
 * Created by poe on 24/04/17.
 */

public class PullToRefreshRecyclerView extends PullToRefreshBase<SimpleRecyclerView> {

    public PullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        /**
         * 默认返回垂直布局
         * {@link com.codyy.tpmp.frameworklibrary.widgets.pulltorefresh.PullToRefreshBase.Orientation}
         */
        return Orientation.VERTICAL;
    }

    /**
     * 创建一个RecyclerView .
     *
     * @param context Context to create view with
     * @param attrs   AttributeSet from wrapped class. Means that anything you
     *                include in the XML layout declaration will be routed to the
     *                created View
     * @return
     */
    @Override
    protected SimpleRecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        SimpleRecyclerView recyclerView = new SimpleRecyclerView(context, attrs);
        //在xml中定义<item type="id" name="recycler_view"/>
        //供代码中的新建View使用
        recyclerView.setId(R.id.recycler_view);
        return recyclerView;
    }

    /**
     * 是否准备好加载更多.
     *
     * @return
     */
    @Override
    protected boolean isReadyForPullEnd() {
        return isLastItemVisible();
    }

    /**
     * 是否准备好下拉刷新.
     *
     * @return
     */
    @Override
    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    /**
     * 判断是否滑动到了最后一个item .
     *
     * @return
     */
    private boolean isLastItemVisible() {
        final RecyclerView.Adapter adapter = mRefreshableView.getAdapter();
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRefreshableView.getLayoutManager();
        if (null == adapter || adapter.getItemCount() == 0) {
            return true;
        } else {
            final int lastItemPosition = layoutManager.getItemCount() - 1;
            final int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

            /**
             * This check should really just be: lastVisiblePosition ==
             * lastItemPosition, but PtRListView internally uses a FooterView
             * which messes the positions up. For me we'll just subtract one to
             * account for it and rely on the inner condition which checks
             * getBottom().
             */
            if (lastVisiblePosition >= lastItemPosition - 1) {
                final int childIndex = lastVisiblePosition - layoutManager.findFirstVisibleItemPosition();
                final View lastVisibleChild = mRefreshableView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
                }
            }
        }

        return false;
    }

    /**
     * 是否上拉到了第一个item.
     *
     * @return
     */
    private boolean isFirstItemVisible() {
        try {
            final RecyclerView.Adapter adapter = mRefreshableView.getAdapter();
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRefreshableView.getLayoutManager();
            if (null == adapter || adapter.getItemCount() == 0) {
                return true;
            } else {
                final int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
                /**
                 * check if pull down if firstItem.getTop() >= RefreshView.getTop()
                 */
                if (firstItemPosition <= 0) {
                    final View lastVisibleChild = mRefreshableView.getChildAt(0);
                    return lastVisibleChild.getTop() >= mRefreshableView.getTop();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return false;
    }
}
