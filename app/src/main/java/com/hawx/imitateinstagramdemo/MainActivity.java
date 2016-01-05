package com.hawx.imitateinstagramdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/12/31.
 */
public class MainActivity extends AppCompatActivity {
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;
    public static boolean isCommentActivityOpen=false;
    private Toolbar toolbar;
    private MenuItem inboxMenuItem;
    private RecyclerView recyclerView;
    private FeedAdapter adapter;
    private ImageButton fab;
    private ImageView ivLogo;
    private boolean pendingIntroAnimation;
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        if(savedInstanceState==null){
            pendingIntroAnimation=true;
        }
        context=this;
        fab= (ImageButton) findViewById(R.id.btnCreate);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        ivLogo= (ImageView) toolbar.findViewById(R.id.ivLogo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        recyclerView= (RecyclerView) findViewById(R.id.rvFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new FeedAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new FeedAdapter.onClickListener() {
            @Override
            public void onClick(View v, int position) {
                if(!isCommentActivityOpen) {
                    isCommentActivityOpen = true;
                    final Intent intent = new Intent(context, CommentsActivity.class);
                    //Get location on screen for tapped view
                    int[] startingLocation = new int[2];
                    v.getLocationOnScreen(startingLocation);
                    intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        inboxMenuItem=menu.findItem(R.id.action_inbox);
        inboxMenuItem.setActionView(R.layout.menu_item_view);
        if(pendingIntroAnimation){
            pendingIntroAnimation=false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {
        fab.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        int actionbarSize=getResources().getDimensionPixelSize(R.dimen.btn_fab_size);
        toolbar.setTranslationY(-actionbarSize);
        ivLogo.setTranslationY(-actionbarSize);
        inboxMenuItem.getActionView().setTranslationY(-actionbarSize);
        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        ivLogo.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        inboxMenuItem.getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        fab.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
        adapter.updateItems();
    }
}
