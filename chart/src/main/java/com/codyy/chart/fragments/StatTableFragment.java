package com.codyy.chart.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.chart.R;
import com.codyy.chart.entities.StatTableModel;
import com.codyy.chart.views.FeedBackScrollView;
import com.codyy.pulltorefresh.PullToRefreshBase;
import com.codyy.pulltorefresh.views.PullToRefreshRecyclerView;
import com.codyy.pulltorefresh.views.SimpleRecyclerView;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * 统计表格fragment
 * Created by ldh on 2017/5/6.
 */
public class StatTableFragment extends Fragment {
    private final static String TAG = "StatTableFragment";

    private PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    private SimpleRecyclerView mSimpleRecyclerView;
    private FeedBackScrollView mScrollView;
    private TextView mTableTitleTv;

    private StatTableModel<?> mTableModel;
    private StatisticsAdapter2 mStatisticsAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stat_table, container, false);
        mPullToRefreshRecyclerView = (PullToRefreshRecyclerView) rootView.findViewById(R.id.rcv_statistics);
        mPullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        mPullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<SimpleRecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<SimpleRecyclerView> refreshView) {
                Log.d(TAG, "onRefresh~");
            }
        });
        mSimpleRecyclerView = mPullToRefreshRecyclerView.getRefreshableView();
        mSimpleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTableTitleTv = (TextView) rootView.findViewById(R.id.tv_row_title);
        mScrollView = (FeedBackScrollView) rootView.findViewById(R.id.scroll_view);
        mStatisticsAdapter = new StatisticsAdapter2(mScrollView);
        mSimpleRecyclerView.setAdapter(mStatisticsAdapter);
        updateData();
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TableLvActivity", "ScrollView event=" + event);
                mScrollView.handleTouchEvent(event);
                return true;
            }
        });
        mSimpleRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            private float initialX;

            private float initialY;

            private static final int MAX_CLICK_DURATION = 400;

            private static final int MAX_CLICK_DISTANCE = 10;

            /**
             * 记录有没有down事件
             */
            private boolean mHasDown;

            private int mInitialPosition = -1;

            private boolean stayedWithinClickDistance;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch event=" + event);
                mSimpleRecyclerView.onTouchEvent(event);
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        initialY = event.getY();
                        mScrollView.handleTouchEvent(event);
                        mHasDown = true;
                        stayedWithinClickDistance = true;
                        break;
                    case MotionEvent.ACTION_UP: {
                        if (mHasDown) mScrollView.handleTouchEvent(event);
                        mHasDown = false;
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:
                        if (mHasDown) mScrollView.handleTouchEvent(event);
                        mHasDown = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (stayedWithinClickDistance
                                && distance(initialX, initialY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                            stayedWithinClickDistance = false;
                        }
                    default:
                        if (mHasDown) mScrollView.handleTouchEvent(event);
                        break;
                }
                return true;
            }
        });
        return rootView;
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void setTableModel(StatTableModel<?> statTableModel) {
        this.mTableModel = statTableModel;
        updateData();
    }

    private void updateData() {
        if (mTableModel == null) return;
        if (mStatisticsAdapter != null) {
            mStatisticsAdapter.setStatTableModel(mTableModel);
            mStatisticsAdapter.notifyDataSetChanged();
        }
        if (mTableTitleTv != null) {
            mTableTitleTv.setEms(mTableModel.getEms());
            mTableTitleTv.setText(mTableModel.getTitle());
        }
    }

    class StatisticsAdapter2 extends RecyclerView.Adapter<StatisticViewHolder> implements FeedBackScrollView.OnScrollChangeListener {
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private StatTableModel mStatTableModel;
        //private String[] mColumns = {"应受邀教室", "实受邀教室", "受邀教室占比", "计划课时" , "实听课时", "实听课时占比", "平均听课"};
        private int[] mColumnWidth;
        private int mCurrentLeft;
        private Set<FeedBackScrollView> mViews = new LinkedHashSet<>();
        private FeedBackScrollView mScrollView;
        private Drawable mDivider;
        private int mPadding;

        public StatisticsAdapter2(FeedBackScrollView scrollView) {
            mContext = scrollView.getContext();
            mLayoutInflater = LayoutInflater.from(mContext);
            mDivider = scrollView.getResources().getDrawable(R.drawable.divider_column);
            mScrollView = scrollView;
            mScrollView.setOnScrollChangeListener(this);

            mPadding = (int)(mContext.getResources().getDisplayMetrics().density*8);
        }

        public void setStatTableModel(StatTableModel tableModel) {
            mStatTableModel = tableModel;
            initColumnWidth();
            setupColumnTitle();
        }

        private void initColumnWidth() {
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.sp16));
            textPaint.setTextAlign(Paint.Align.CENTER);
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.dp8);
            mColumnWidth = new int[mStatTableModel.columnCount()];
            for (int i = 0; i < mStatTableModel.columnCount(); i++) {
                mColumnWidth[i] = padding + padding +
                        (int) textPaint.measureText(mStatTableModel.getColumnTitle(i));
            }
        }

        private void setupColumnTitle() {
            LinearLayout columnTitleContainer = (LinearLayout) mScrollView.findViewById(R.id.ll_columns);
            columnTitleContainer.setDividerDrawable(mDivider);
            columnTitleContainer.removeAllViews();
            for (int i = 0; i < mStatTableModel.columnCount(); i++) {
                TextView textView = new TextView(columnTitleContainer.getContext());
                textView.setPadding(mPadding, 0, mPadding, 0);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                textView.setLines(1);
                textView.setText(mStatTableModel.getColumnTitle(i));
                columnTitleContainer.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        @Override
        public StatisticViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.item_right_scroll, null);
            return new StatisticViewHolder(view, mColumnWidth);
        }

        @Override
        public void onBindViewHolder(StatisticViewHolder holder, int position) {
            holder.rowTitleTv.setText(mStatTableModel.getRowTitle(position).length() > 6 ?
                    mStatTableModel.getRowTitle(position).substring(0, 5) + "..." : mStatTableModel.getRowTitle(position));
            holder.scrollView.smoothScrollTo(mCurrentLeft, 0);

            for (int i = 0; i < mColumnWidth.length; i++) {
                TextView cellTv = holder.cellTvs[i];
                cellTv.setTag(position);
                cellTv.setText(mStatTableModel.getCellStr(position, i));
            }
            mViews.add(holder.scrollView);
        }

        @Override
        public int getItemCount() {
            return mStatTableModel == null ? 0 : mStatTableModel.rowCount();
        }

        @Override
        public void onScrollChange(View view, int l, int t, int oldl, int oldt) {
            for (FeedBackScrollView scrollView : mViews) {
                if (!view.equals(scrollView)) {
                    scrollView.smoothScrollTo(l, 0);
                }
            }
            mCurrentLeft = l;
        }
    }

    class StatisticViewHolder extends RecyclerView.ViewHolder {
        LayoutInflater layoutInflater;
        TextView[] cellTvs;
        TextView rowTitleTv;
        FeedBackScrollView scrollView;
        LinearLayout columnLl;

        public StatisticViewHolder(View itemView, int[] count) {
            super(itemView);
            layoutInflater = LayoutInflater.from(itemView.getContext());
            rowTitleTv = itemView.findViewById(R.id.tv_row_title);
            scrollView = itemView.findViewById(R.id.scroll_view);
            columnLl = itemView.findViewById(R.id.ll_columns);
            setColumnWidth(count);
        }

        public void setColumnWidth(int[] columnWidth) {
            if (columnWidth == null || columnWidth.length == 0) {
                return;
            }
            cellTvs = new TextView[columnWidth.length];
            for (int i = 0; i < columnWidth.length; i++) {
                View view = layoutInflater.inflate(R.layout.item_stat_cell, columnLl, false);
                cellTvs[i] = (TextView) view.findViewById(R.id.tv_cell);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(columnWidth[i], ViewGroup.LayoutParams.WRAP_CONTENT);
                columnLl.addView(view, params);
            }
        }
    }

    public interface OnRowClickListener {
        void onRowClickListener(int position);
    }
}
