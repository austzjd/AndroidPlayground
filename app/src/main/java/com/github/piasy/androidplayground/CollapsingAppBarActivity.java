package com.github.piasy.androidplayground;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Piasy{github.com/Piasy} on 15/10/2.
 */
public class CollapsingAppBarActivity extends Activity {
    @Bind(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.mAppBarLayout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.mCoordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    private int mAppBarHeight = -1;
    private int mCurrentAppBarOffset;   // -height ==> 0
    private final float APP_BAR_AUTO_COLLAPSE_RATION = 0.3F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collapsing_app_bar);
        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Adapter adapter = new Adapter(this, new Adapter.Action() {
            @Override
            public void onClick() {
                mAppBarLayout.setExpanded(false);
            }
        });
        mRecyclerView.setAdapter(adapter);
        adapter.setContentCount(40);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mAppBarHeight == -1) {
                    mAppBarHeight = appBarLayout.getHeight();
                }
                mCurrentAppBarOffset = -verticalOffset;
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if ((float) mCurrentAppBarOffset / mAppBarHeight <
                            APP_BAR_AUTO_COLLAPSE_RATION) {
                        mAppBarLayout.setExpanded(true);
                    } else {
                        mAppBarLayout.setExpanded(false);
                    }
                }
            }
        });
    }

    public static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final Context mContext;
        private final Action mAction;

        public Adapter(Context context, Action action) {
            mContext = context;
            mAction = action;
        }

        private int mContentCount = 0;

        public void setContentCount(int contentCount) {
            mContentCount = contentCount;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ui_recycler_view_horizontal_item, parent, false));
        }

        public interface Action {
            void onClick();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTextView.setText(String.valueOf(position));
            holder.mLlContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Click: " + position, Toast.LENGTH_SHORT).show();
                    mAction.onClick();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mContentCount;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        LinearLayout mLlContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.mNumber);
            mLlContainer = (LinearLayout) itemView.findViewById(R.id.mLlContainer);
        }
    }
}
