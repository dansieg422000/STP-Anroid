package com.evvo.wheelscroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Scroller class handles scrolling events and updates the 
 */
public class WheelScroller {
	/** Scrolling duration */
    private static final int SCROLLING_DURATION = 400;

    /** Minimum delta for scrolling */
    public static final int MINDELTAFORSCROLLING = 1;

    // Listener
    private ScrollingListener listener;
    
    // Context
    private Context context;
    // Messages
    private static final int messageScroll = 0;
    private static final int messageJustify = 1;
    
    // Scrolling
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private int lastScrollY;
    private float lastTouchedY;
    private boolean isScrollingPerformed;
	/**
     * Scrolling listener interface
     */
    public interface ScrollingListener {
        /**
         * Scrolling callback called when scrolling is performed.
         * @param distance the distance to scroll
         */
        void onScroll(int distance);

        /**
         * Starting callback called when scrolling is started
         */
        void onStarted();
        
        /**
         * Finishing callback called after justifying
         */
        void onFinished();
        
        /**
         * Justifying callback called to justify a view when scrolling is ended
         */
        void onJustify();
    }
    
    // gesture listener
    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Do scrolling in onTouchEvent() since onScroll() are not call immediately
            //  when user touch and move the wheel
            return true;
        }
        
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            lastScrollY = 0;
            final int maxY = 0x7FFFFFFF;
            final int minY = -maxY;
            scroller.fling(0, lastScrollY, 0, (int) -velocityY, 0, 0, minY, maxY);
            setNextMessage(messageScroll);
            return true;
        }
    };

    /**
     * Constructor
     * @param context the current context
     * @param listener the scrolling listener
     */
    public WheelScroller(Context context, ScrollingListener listener) {
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        
        scroller = new Scroller(context);

        this.listener = listener;
        this.context = context;
    }
    
    /**
     * Set the the specified scrolling interpolator
     * @param interpolator the interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        scroller.forceFinished(true);
        scroller = new Scroller(context, interpolator);
    }
    
    /**
     * Scroll the wheel
     * @param distance the scrolling distance
     * @param time the scrolling duration
     */
    public void scroll(int distance, int time) {
        scroller.forceFinished(true);

        lastScrollY = 0;
        
        scroller.startScroll(0, 0, 0, distance, time != 0 ? time : SCROLLING_DURATION);
        setNextMessage(messageScroll);
        
        startScrolling();
    }
   
    /**
     * Stops scrolling
     */
    public void stopScrolling() {
        scroller.forceFinished(true);
    }
    
    /**
     * Handles Touch event 
     * @param event the motion event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchedY = event.getY();
                scroller.forceFinished(true);
                clearMessages();
                break;
    
            case MotionEvent.ACTION_MOVE:
                // perform scrolling
                int distanceY = (int)(event.getY() - lastTouchedY);
                if (distanceY != 0) {
                    startScrolling();
                    listener.onScroll(distanceY);
                    lastTouchedY = event.getY();
                }
                break;
            default:
				break;
        }
        
        if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
            justify();
        }

        return true;
    }
    
    /**
     * Set next message to queue. Clears queue before.
     * 
     * @param message the message to set
     */
    private void setNextMessage(int message) {
        clearMessages();
        animationHandler.sendEmptyMessage(message);
    }

    /**
     * Clears messages from queue
     */
    private void clearMessages() {
        animationHandler.removeMessages(messageScroll);
        animationHandler.removeMessages(messageJustify);
    }
    
    // animation handler
    @SuppressLint("HandlerLeak")
	private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int currY = scroller.getCurrY(),delta = lastScrollY - currY;
            lastScrollY = currY;
            if (delta != 0) {
            	listener.onScroll(delta);
            }       
            // scrolling is not finished when it comes to final Y
            // so, finish it manually 
            if (Math.abs(currY - scroller.getFinalY()) < MINDELTAFORSCROLLING) {
                currY = scroller.getFinalY();
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == messageScroll) {
                justify();
            } else {
                finishScrolling();
            }
        }
    };
    
    /**
     * Justifies wheel
     */
    private void justify() {
        listener.onJustify();
        setNextMessage(messageJustify);
    }

    /**
     * Starts scrolling
     */
    private void startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true;
            listener.onStarted();
        }
    }

    /**
     * Finishes scrolling
     */
    void finishScrolling() {
        if (isScrollingPerformed) {
            listener.onFinished();
            isScrollingPerformed = false;
        }
    }
}