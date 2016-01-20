package com.hawx.imitateinstagramdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/5.
 */
public class CommentsActivity extends AppCompatActivity implements GlobalMenuView.OnHeaderClickListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.contentroot)
    LinearLayout contentRoot;
    @Bind(R.id.rvComments)
    RecyclerView rvComments;
    @Bind(R.id.llAddComment)
    LinearLayout llAddComment;
    @Bind(R.id.btnSendComment)
    SendCommentButton sendCommentButton;
    @Bind(R.id.et_comment)
    EditText editText;

    private Context context;
    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;
    private MenuItem inboxMenuItem;
    private SendCommentButton.OnSendClickListener onSendClickListener=new SendCommentButton.OnSendClickListener() {
        @Override
        public void onSendClick(View v) {
            if((editText.getText().toString()).equals("")){
                v.startAnimation(AnimationUtils.loadAnimation(context,R.anim.send_error));
            }else {
                SendCommentButton sendCommentButton = (SendCommentButton) v;
                sendCommentButton.setCurrentState(SendCommentButton.STATE_DONE);
                commentsAdapter.addItem();
                commentsAdapter.setAnimationsLocked(false);
                commentsAdapter.setDelayEnterAnimation(false);
                //修正BUG：点击SEND之后添加commentitem错位。方案：先提示adapter数据变化，在滚动至最后一行
                commentsAdapter.notifyDataSetChanged();
                rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());
            }
        }
    };
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);
        ButterKnife.bind(this);
        context=this;
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        commentsAdapter=new CommentsAdapter(this);
        rvComments.setLayoutManager(new LinearLayoutManager(this){
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        });
        rvComments.setAdapter(commentsAdapter);
        drawingStartLocation=getIntent().getIntExtra(ARG_DRAWING_START_LOCATION,0);
        if(savedInstanceState==null){
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
        setupComments();
        sendCommentButton.setOnSendClickListener(onSendClickListener);
        setupDrawer();
    }

    private void setupDrawer() {
        GlobalMenuView menuView = new GlobalMenuView(this);
        menuView.setOnHeaderClickListener(this);

        drawerLayout = DrawerLayoutInstaller.from(this)
                .drawerRoot(R.layout.drawer_root)
                .drawerLeftView(menuView)
                .withNavigationIconToggler(toolbar)
                .build();
    }

    private void startIntroAnimation() {
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(140);
        contentRoot.animate()
                .scaleY(1)
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(300)
                .start();
        setTheme(R.style.AppTheme);
    }
    private void setupComments() {
        rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.isCommentActivityOpen=false;
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(500)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        inboxMenuItem=menu.findItem(R.id.action_inbox);
        inboxMenuItem.setActionView(R.layout.menu_item_view);
        return true;
    }

    @Override
    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                ProfileAvtivity.startFromLocation(startingLocation, CommentsActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }
}
