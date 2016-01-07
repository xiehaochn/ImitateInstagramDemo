package com.hawx.imitateinstagramdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.ViewUtils;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/4.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedVH> {
    private static final int ANIMATION_ITEMS_COUNT=2;
    private Context context;
    private int lastAnimationPosition=-1;
    private int itemCount=0;
    private FeedMenuManager feedMenuManager;
    private onClickListener onclickListener;
    public FeedAdapter(Context context){
        this.context=context;
    }
    @Override
    public FeedVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feed,parent,false);
        return new FeedVH(view);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    @Override
    public void onBindViewHolder(final FeedVH holder, final int position) {
        runEnterAnimation(holder.itemView,position);
        if(position%2==0){
            holder.feed_center.setImageResource(R.drawable.img_feed_center_1);
            holder.feed_bottom.setImageResource(R.drawable.img_feed_bottom_1);
        }else {
            holder.feed_center.setImageResource(R.drawable.img_feed_center_2);
            holder.feed_bottom.setImageResource(R.drawable.img_feed_bottom_2);
        }
        holder.imageButton_comment.setTag(position);
        holder.imageButton_comment.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.btn_comment) {
                    if (onclickListener != null) {
                        onclickListener.onClick(v,(Integer)v.getTag());
                    }
                }
            }
        });
        holder.btn_context_menu.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_menu) {
                    feedMenuManager = FeedMenuManager.getInstance();
                    feedMenuManager.toggleContextMenuFromView(v, position, new FeedContextMenu.OnFeedContextMenuItemClickedListener() {
                        @Override
                        public void onReportClick(int feedItem) {
                            Toast.makeText(context,"You clicked Report",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSharePhotoClick(int feedItem) {
                            Toast.makeText(context,"You clicked SharePhoto",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCopyShareUrlClick(int feedItem) {
                            Toast.makeText(context,"You clicked CopyShare",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelClick(int feedItem) {
                            feedMenuManager.hideContextMenu();
                        }
                    });
                }
            }
        });
        holder.imageButton_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLikesCount(holder,position,true);
                updateLikeButton(holder,position,true);
            }
        });
        holder.feed_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!likesCounts.getLikedItemPosition(position)) {
                    showImageAnimation(holder, position);
                    updateLikesCount(holder, position, true);
                    updateLikeButton(holder,position,true);
                }
            }
        });
        updateLikesCount(holder,position,false);
        updateLikeButton(holder,position,false);
    }

    private void showImageAnimation(final FeedVH holder, int position) {
        holder.animLike.setVisibility(View.VISIBLE);
        holder.animBg.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params=holder.feed_center.getLayoutParams();
        holder.animBg.setLayoutParams(params);
        holder.animBg.setScaleY(0.1f);
        holder.animBg.setScaleX(0.1f);
        holder.animBg.setAlpha(1f);
        holder.animLike.setScaleY(0.1f);
        holder.animLike.setScaleX(0.1f);
        AnimatorSet animatorSet=new AnimatorSet();

        ObjectAnimator bounceAnimX_image=ObjectAnimator.ofFloat(holder.animLike,"scaleX",0.1f,1f);
        bounceAnimX_image.setDuration(400);
        bounceAnimX_image.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator bounceAnimY_image=ObjectAnimator.ofFloat(holder.animLike,"scaleY",0.1f,1f);
        bounceAnimY_image.setDuration(400);
        bounceAnimY_image.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator bounceDownAnimX_image=ObjectAnimator.ofFloat(holder.animLike,"scaleX",1f,0f);
        bounceDownAnimX_image.setDuration(400);
        bounceDownAnimX_image.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator bounceDownAnimY_image=ObjectAnimator.ofFloat(holder.animLike,"scaleY",1f,0f);
        bounceDownAnimY_image.setDuration(400);
        bounceDownAnimY_image.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.animBg, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(300);
        bgScaleYAnim.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.animBg, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(300);
        bgScaleXAnim.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.animBg, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(300);
        bgAlphaAnim.setInterpolator(new DecelerateInterpolator());

        animatorSet.playTogether(bounceAnimX_image,bounceAnimY_image,bgAlphaAnim,bgScaleXAnim,bgScaleYAnim);
        animatorSet.play(bounceDownAnimX_image).with(bounceDownAnimY_image).after(bounceAnimX_image);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                holder.animLike.setVisibility(View.VISIBLE);
                holder.animBg.setVisibility(View.VISIBLE);
            }
        });
        animatorSet.start();
    }

    private void updateLikeButton(final FeedVH holder,int position, boolean animated) {
        if(animated){
            if(likesCounts.getLikedItemPosition(position)){
                return;
            }
            AnimatorSet animatorSet=new AnimatorSet();
            ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.imageButton_like, "rotation", 0f, 360f);
            rotationAnim.setDuration(400);
            rotationAnim.setInterpolator(new OvershootInterpolator(1.f));

            ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.imageButton_like, "scaleX", 0.2f, 1f);
            bounceAnimX.setDuration(400);
            bounceAnimX.setInterpolator(new OvershootInterpolator(1.f));

            ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.imageButton_like, "scaleY", 0.2f, 1f);
            bounceAnimY.setDuration(400);
            bounceAnimY.setInterpolator(new OvershootInterpolator(1.f));
            bounceAnimY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    holder.imageButton_like.setImageResource(R.drawable.ic_btn_liked);
                }
            });
            animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
            animatorSet.start();
            likesCounts.setLikedItemPosition(position);
        }else{
            if(!likesCounts.getLikedItemPosition(position)){
                holder.imageButton_like.setImageResource(R.drawable.ic_btn_like);
            }else {
                holder.imageButton_like.setImageResource(R.drawable.ic_btn_liked);
            }
        }
    }

    private void updateLikesCount(FeedVH holder,int position,boolean animated) {
        if(animated){
            if(likesCounts.getLikedItemPosition(position)){
                return;
            }
            int currentLikesCount=likesCounts.getLikesCounts(position)+1;
            String currentLikesString=context.getResources().getQuantityString(R.plurals.likes_count,currentLikesCount,currentLikesCount);
            holder.textSwitcher.setText(currentLikesString);
            likesCounts.setLikesCounts(currentLikesCount,position);
        }else{
            int currentLikesCount=likesCounts.getLikesCounts(position);
            String currentLikesString=context.getResources().getQuantityString(R.plurals.likes_count,currentLikesCount,currentLikesCount);
            holder.textSwitcher.setCurrentText(currentLikesString);
        }
    }

    private void runEnterAnimation(View itemview, int position) {
        if(position>=ANIMATION_ITEMS_COUNT-1){
            return;
        }
        if(position>lastAnimationPosition){
            lastAnimationPosition=position;
            itemview.setTranslationY(Utils.getScreenHeight(context));
            itemview.animate()
                    .translationY(0).setInterpolator(new DecelerateInterpolator(3.f)).setDuration(700).start();
        }
    }

    public class FeedVH extends RecyclerView.ViewHolder{
        @Bind(R.id.btn_like)
        ImageButton imageButton_like;
        @Bind(R.id.btn_comment)
        ImageButton imageButton_comment;
        @Bind(R.id.feed_center)
        ImageView feed_center;
        @Bind(R.id.feed_bottom)
        ImageView feed_bottom;
        @Bind(R.id.btn_menu)
        ImageButton btn_context_menu;
        @Bind(R.id.like_counts_text)
        TextSwitcher textSwitcher;
        @Bind(R.id.anim_like)
        ImageView animLike;
        @Bind(R.id.anim_bg)
        ImageView animBg;
        public FeedVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public void updateItems() {
        itemCount = 10;
        notifyDataSetChanged();
    }

    public interface onClickListener{
        void onClick(View v,int position);
    }
    public void setOnClickListener(onClickListener onClickListener){
        this.onclickListener=onClickListener;
    }
}
