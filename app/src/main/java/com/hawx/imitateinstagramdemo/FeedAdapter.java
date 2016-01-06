package com.hawx.imitateinstagramdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.ViewUtils;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    public void onBindViewHolder(FeedVH holder, final int position) {
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
