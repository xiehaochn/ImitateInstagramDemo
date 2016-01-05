package com.hawx.imitateinstagramdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewAnimator;

import java.text.AttributedCharacterIterator;

/**
 * Created by Administrator on 2016/1/5.
 */
public class SendCommentButton extends ViewAnimator implements View.OnClickListener{
    private static final long RESET_STATE_DELAY_MILLIS = 2000;
    public static final int STATE_SEND=0;
    public static final int STATE_DONE=1;
    private int currentState;
    private OnSendClickListener onSendClickListener;
    private Runnable revertStateRunnable=new Runnable() {
        @Override
        public void run() {
            setCurrentState(STATE_SEND);
        }
    };
    public SendCommentButton(Context context) {
        super(context);
        inti();
    }
    public SendCommentButton(Context context, AttributeSet attr) {
        super(context,attr);
        inti();
    }

    private void inti() {
        LayoutInflater.from(getContext()).inflate(R.layout.comment_btn_send_layout,this,true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        currentState=STATE_SEND;
        super.setOnClickListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(revertStateRunnable);
        super.onDetachedFromWindow();
    }

    @Override
    public void onClick(View v) {
        if(onSendClickListener!=null){
            onSendClickListener.onSendClick(this);
        }
    }

    public void setCurrentState(int state) {
        if(state==currentState){
            return;
        }
        currentState=state;
        if(state==STATE_DONE){
            setEnabled(false);
            postDelayed(revertStateRunnable,RESET_STATE_DELAY_MILLIS);
            setInAnimation(getContext(),R.anim.slide_in_done);
            setOutAnimation(getContext(),R.anim.slide_out_send);
        }else {
            setEnabled(true);
            setInAnimation(getContext(),R.anim.slide_in_send);
            setOutAnimation(getContext(),R.anim.slide_out_done);
        }
        showNext();
    }
    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        this.onSendClickListener = onSendClickListener;
    }

    public interface OnSendClickListener {
        void onSendClick(View v);
    }
}
