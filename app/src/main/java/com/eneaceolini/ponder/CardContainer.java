package com.eneaceolini.ponder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.transition.Scene;
import android.transition.Transition;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.eneaceolini.ponder.Orientations.Orientation;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

public class CardContainer extends AdapterView<ListAdapter> {
    public static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private static final double DISORDERED_MAX_ROTATION_RADIANS = Math.PI / 64;
    private int mNumberOfCards = -1;
    private int selectedItemIndex=-1;
    private int last = -3;
    private float lastDX = 0;
    public FragmentHolderTest mainActivity;
    public NextPageFragment callingFragment;
    public ToolBox toolBox;




    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            clearStack();
            ensureFull();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            clearStack();
        }
    };
    private final Random mRandom = new Random();
    private final Rect boundsRect = new Rect();
    private final Rect childRect = new Rect();
    private final Matrix mMatrix = new Matrix();
    private Context context;

    //TODO: determine max dynamically based on device speed
    private int mMaxVisible = 10;
    private GestureDetector mGestureDetector;
    private int mFlingSlop;
    private Orientation mOrientation;
    private ListAdapter mListAdapter;
    private float mLastTouchX;
    private float mLastTouchY;
    private View mTopCard;
    private View secTopCard;
    private View tTopCard;
    private int mTouchSlop;
    private int mGravity;
    private float maxX;
    private float maxY;
    private int mNextAdapterPosition;
    private boolean mDragging;
    private ActionBarActivity currentActivity;
    private NextPageFragment nextPage;

    public CardContainer(Context context) {

        super(context);

        setOrientation(Orientation.Ordered);
        setGravity(Gravity.CENTER);
        init();

    }


    public void GetActionBar(ActionBarActivity a,NextPageFragment b){
        currentActivity = a;
        nextPage = b;
    }

    public CardContainer(Context context, AttributeSet attrs) {

        super(context, attrs);
        toolBox = ToolBox.getInstance();
        initFromXml(attrs);
        init();
    }


    public CardContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        toolBox = ToolBox.getInstance();
        initFromXml(attrs);
        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mFlingSlop = viewConfiguration.getScaledMinimumFlingVelocity();
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    private void initFromXml(AttributeSet attr) {
        TypedArray a = getContext().obtainStyledAttributes(attr,
                R.styleable.CardContainer);

        setGravity(a.getInteger(R.styleable.CardContainer_android_gravity, Gravity.CENTER));
        int orientation = a.getInteger(R.styleable.CardContainer_orientation, 1);
        setOrientation(Orientation.fromIndex(orientation));

        a.recycle();
    }

    @Override
    public ListAdapter getAdapter() {
        return mListAdapter;
    }

    @Override
    public int getSelectedItemPosition(){
        return selectedItemIndex;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mListAdapter != null)
            mListAdapter.unregisterDataSetObserver(mDataSetObserver);

        clearStack();
        mTopCard = null;
        secTopCard = null;
        tTopCard = null;
        mListAdapter = adapter;
        mNextAdapterPosition = 0;
        adapter.registerDataSetObserver(mDataSetObserver);

        ensureFull();

        if (getChildCount() != 0) {
            tTopCard = getChildAt(getChildCount() - 3);
            if(tTopCard != null) tTopCard.setLayerType(LAYER_TYPE_HARDWARE,null);
            secTopCard = getChildAt(getChildCount() -2);
            if(secTopCard != null) secTopCard.setLayerType(LAYER_TYPE_HARDWARE,null);
            mTopCard = getChildAt(getChildCount() - 1);
            if(mTopCard != null)mTopCard.setOnClickListener(new MySpecialClick());
            mTopCard.setLayerType(LAYER_TYPE_HARDWARE, null);
        }
        mNumberOfCards = getAdapter().getCount();
        selectedItemIndex = 0;
        requestLayout();
    }

    public class MySpecialClick implements View.OnClickListener{

        @Override
        public void onClick(View v){
            callingFragment.mainActivity.startDelete((CardModel) getAdapter().getItem(selectedItemIndex));
        }
    }

    private void ensureFull() {
        while (mNextAdapterPosition < mListAdapter.getCount() && getChildCount() < mMaxVisible) {
            View view = mListAdapter.getView(mNextAdapterPosition, null, this);
            view.setLayerType(LAYER_TYPE_SOFTWARE, null);
            if(mOrientation == Orientation.Disordered) {
                view.setRotation(getDisorderedRotation());

            }
            addViewInLayout(view, 0, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                    mListAdapter.getItemViewType(mNextAdapterPosition)), false);

            requestLayout();

            mNextAdapterPosition += 1;
        }
    }

    private void clearStack() {
        removeAllViewsInLayout();
        mNextAdapterPosition = 0;
        mTopCard = null;
        secTopCard = null;
        tTopCard = null;
        selectedItemIndex=-1;
    }

    public Orientation getOrientation() {
        return mOrientation;
    }

    public void setOrientation(Orientation orientation) {
        if (orientation == null)
            throw new NullPointerException("Orientation may not be null");

        if(mOrientation != orientation) {
            this.mOrientation = orientation;
            if(orientation == Orientation.Disordered) {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    child.setRotation(getDisorderedRotation());
                }
            }
            else {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    child.setRotation(0);
                    maxX=child.getX();
                    maxY=child.getY();

                }
            }
            requestLayout();
        }

    }

    private float getDisorderedRotation() {
        return (float) Math.toDegrees(mRandom.nextGaussian() * DISORDERED_MAX_ROTATION_RADIANS);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int requestedWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int requestedHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int childWidth, childHeight;

        if (mOrientation == Orientation.Disordered) {
            int R1, R2;
            if (requestedWidth >= requestedHeight) {
                R1 = requestedHeight;
                R2 = requestedWidth;
            } else {
                R1 = requestedWidth;
                R2 = requestedHeight;
            }
            childWidth = (int) ((R1 * Math.cos(DISORDERED_MAX_ROTATION_RADIANS) - R2 * Math.sin(DISORDERED_MAX_ROTATION_RADIANS)) / Math.cos(2 * DISORDERED_MAX_ROTATION_RADIANS));
            childHeight = (int) ((R2 * Math.cos(DISORDERED_MAX_ROTATION_RADIANS) - R1 * Math.sin(DISORDERED_MAX_ROTATION_RADIANS)) / Math.cos(2 * DISORDERED_MAX_ROTATION_RADIANS));
        } else {
            childWidth = requestedWidth;
            childHeight = requestedHeight;
        }

        int childWidthMeasureSpec, childHeightMeasureSpec;
        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            assert child != null;
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            //child.setAlpha(0);
            if(i==getChildCount()-1){
                if(toolBox.isFirst) {
                    child.setY(child.getY() - 40);
                    child.setX(child.getX() - 40);
                    toolBox.isFirst=false;
                }else child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                //child.setAlpha(1);
            }
            //if(i==getChildCount()-2) child.setAlpha(1);

        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        for (int i = 0; i < getChildCount(); i++) {
            boundsRect.set(0, 0, getWidth(), getHeight());

            View view = getChildAt(i);
            int w, h;
            w = view.getMeasuredWidth();
            h = view.getMeasuredHeight();

            Gravity.apply(mGravity, w, h, boundsRect, childRect);
            if(i!=getChildCount()-1)
                view.layout(childRect.left, childRect.top, childRect.right, childRect.bottom);
            else
                view.layout(childRect.left, childRect.top, childRect.right, childRect.bottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTopCard == null) {
            return false;
        }
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        //Log.d("Touch Event", MotionEvent.actionToString(event.getActionMasked()) + " ");
        final int pointerIndex;
        final float x, y;
        final float dx, dy;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mTopCard.getHitRect(childRect);

                pointerIndex = event.getActionIndex();
                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                if (!childRect.contains((int) x, (int) y)) {
                    return false;
                }
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = event.getPointerId(pointerIndex);


                float[] points = new float[]{x - mTopCard.getLeft(), y - mTopCard.getTop()};
                mTopCard.getMatrix().invert(mMatrix);
                mMatrix.mapPoints(points);
                mTopCard.setPivotX(points[0]);
                mTopCard.setPivotY(points[1]);

                break;
            case MotionEvent.ACTION_MOVE:

                pointerIndex = event.findPointerIndex(mActivePointerId);

                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                dx = x - mLastTouchX;
                dy = y - mLastTouchY;

                if (Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }

                if(!mDragging) {
                    return true;
                }
                if(dx*lastDX >= 0)last = -3;
                else last = 3;
                //Log.d("dx - dy:", ""+dx+" "+dy);
                if(secTopCard!=null&&secTopCard.getTranslationY()+last > - 40  && secTopCard.getTranslationX()+last > -40) {
                    secTopCard.setTranslationY(secTopCard.getTranslationY() + last);
                    secTopCard.setTranslationX(secTopCard.getTranslationX() + last);
                }



                mTopCard.setTranslationX(mTopCard.getTranslationX() + dx);
                //mTopCard.setTranslationY(mTopCard.getTranslationY() + dy);

                mTopCard.setRotation(40 * mTopCard.getTranslationX() / (getWidth() / 2.f));

                mLastTouchX = x;
                mLastTouchY = y;
                lastDX = dx;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!mDragging) {
                    return true;
                }
                mDragging = false;
                mActivePointerId = INVALID_POINTER_ID;
                //if(tTopCard!=null)tTopCard.setAlpha(0);

                if(secTopCard != null) {
                    ValueAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(secTopCard,
                            PropertyValuesHolder.ofFloat("translationX", 0),
                            PropertyValuesHolder.ofFloat("translationY", 0),
                            PropertyValuesHolder.ofFloat("rotation", 0),
                            PropertyValuesHolder.ofFloat("pivotX", secTopCard.getWidth() / 2.f),
                            PropertyValuesHolder.ofFloat("pivotY", secTopCard.getHeight() / 2.f)
                    ).setDuration(250);
                    animator2.setInterpolator(new AccelerateInterpolator());
                    animator2.start();
                }
                ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mTopCard,
                        PropertyValuesHolder.ofFloat("translationX", - 40),
                        PropertyValuesHolder.ofFloat("translationY", - 40),
                        PropertyValuesHolder.ofFloat("rotation", 0),
                        PropertyValuesHolder.ofFloat("pivotX", mTopCard.getWidth() / 2.f ),
                        PropertyValuesHolder.ofFloat("pivotY", mTopCard.getHeight() / 2.f )
                ).setDuration(250);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);

                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;

        }

        return true;
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mTopCard == null) {
            return false;
        }
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        final int pointerIndex;
        final float x, y;
        final float dx, dy;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mTopCard.getHitRect(childRect);

                CardModel cardModel = (CardModel)getAdapter().getItem(0);

                if (cardModel.getOnClickListener() != null) {
                    cardModel.getOnClickListener().OnClickListener();
                }
                pointerIndex = event.getActionIndex();
                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                if (!childRect.contains((int) x, (int) y)) {
                    return false;
                }

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = event.getPointerId(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);
                if (Math.abs(x - mLastTouchX) > mTouchSlop || Math.abs(y - mLastTouchY) > mTouchSlop) {
                    float[] points = new float[]{x - mTopCard.getLeft(), y - mTopCard.getTop()};
                    mTopCard.getMatrix().invert(mMatrix);
                    mMatrix.mapPoints(points);
                    mTopCard.setPivotX(points[0]);
                    mTopCard.setPivotY(points[1]);
                    return true;
                }
        }

        return false;
    }

    @Override
    public View getSelectedView() {

        //throw new UnsupportedOperationException();
        return null;

    }

    @Override
    public void setSelection(int position) {
        throw new UnsupportedOperationException();
    }

    public int getGravity() {
        return mGravity;
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        int viewType;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.viewType = viewType;
        }
    }

    public void likeTop() {
        //Log.d("like", "top");
        //saving cards
        CardModel cardModel = (CardModel) getAdapter().getItem(selectedItemIndex);
        DatabaseHelper dbHelper = new DatabaseHelper(mainActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableNotes.COLUMN_URL, cardModel.geturl());
        values.put(TableNotes.COLUMN_SPK, cardModel.getSpk());
        values.put(TableNotes.COLUMN_TITLE,cardModel.getTitle());
        values.put(TableNotes.COLUMN_SERIES,cardModel.getSeries());
        values.put(TableNotes.COLUMN_FROM,cardModel.getFrom());
        values.put(TableNotes.COLUMN_DATE,cardModel.getDate());
        values.put(TableNotes.COLUMN_TIME,cardModel.getTime());
        values.put(TableNotes.COLUMN_ABSTRACT,cardModel.getAbst());
        Log.d("from",cardModel.getFrom());
        long id = db.insertOrThrow(TableNotes.TABLE_NAME_2, null, values);
        db.close();
        Log.d("PONDER DB","Insert in "+id);
        //

        final View topCard = mTopCard;
        final View secTop = secTopCard;


        if(topCard!=null){
            //toolBox.cardPassed++;
            tTopCard = getChildAt(getChildCount() - 4);
            secTopCard = getChildAt(getChildCount() - 3);
            //if (secTopCard != null) secTopCard.setAlpha(1);
            mTopCard = getChildAt(getChildCount() - 2);
            if(mTopCard != null)mTopCard.setOnClickListener(new MySpecialClick());

            //if (mTopCard != null) mTopCard.setAlpha(1);
            cardModel = (CardModel) getAdapter().getItem(0);

            if (secTopCard != null)
                secTopCard.setLayerType(LAYER_TYPE_HARDWARE, null);
            if (mTopCard != null)
                mTopCard.setLayerType(LAYER_TYPE_HARDWARE, null);

            //toolBox.yep.setText("Flyying");

            if (cardModel.getOnCardDimissedListener() != null)
                cardModel.getOnCardDimissedListener().onLike();

            //cardModel.getOnCardDimissedListener().onLke();

            if (secTopCard != null) {
                ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(secTop,
                        PropertyValuesHolder.ofFloat("translationX", -40),
                        PropertyValuesHolder.ofFloat("translationY", -40),
                        PropertyValuesHolder.ofFloat("rotation", 0),
                        PropertyValuesHolder.ofFloat("pivotX", secTopCard.getWidth() / 2.f),
                        PropertyValuesHolder.ofFloat("pivotY", secTopCard.getHeight() / 2.f)
                ).setDuration(250);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
            }

            topCard.animate()
                    .setDuration(1000)
                    .alpha(.75f)
                    .setInterpolator(new LinearInterpolator())
                    .x(1800)
                    .y(1800)
                    .rotation(Math.copySign(45, 500))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            removeViewInLayout(topCard);
                            selectedItemIndex++;
                            toolBox.toNotifyAdapter.notifyDataSetChanged();
                            ensureFull();
                            toolBox.cardPassed++;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            onAnimationEnd(animation);
                        }
                    });
        }
    }

    public void dislikeTop() {

        //Log.d("dislike", "top");
        //


        final View topCard = mTopCard;
        final View secTop = secTopCard;
        if (topCard != null) {

            tTopCard = getChildAt(getChildCount() - 4);
            secTopCard = getChildAt(getChildCount() - 3);
            //if (secTopCard != null) secTopCard.setAlpha(1);
            mTopCard = getChildAt(getChildCount() - 2);
            if(mTopCard != null)mTopCard.setOnClickListener(new MySpecialClick());

            //if (mTopCard != null) mTopCard.setAlpha(1);
            CardModel cardModel = (CardModel) getAdapter().getItem(0);

            if (secTopCard != null)
                secTopCard.setLayerType(LAYER_TYPE_HARDWARE, null);
            if (mTopCard != null)
                mTopCard.setLayerType(LAYER_TYPE_HARDWARE, null);

            //toolBox.yep.setText("Flyying");

            if (cardModel.getOnCardDimissedListener() != null)
                cardModel.getOnCardDimissedListener().onDislike();


            //cardModel.getOnCardDimissedListener().onLke();
            if (secTopCard != null) {
                ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(secTop,
                        PropertyValuesHolder.ofFloat("translationX", -40),
                        PropertyValuesHolder.ofFloat("translationY", -40),
                        PropertyValuesHolder.ofFloat("rotation", 0),
                        PropertyValuesHolder.ofFloat("pivotX", secTopCard.getWidth() / 2.f),
                        PropertyValuesHolder.ofFloat("pivotY", secTopCard.getHeight() / 2.f)
                ).setDuration(250);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
            }

            topCard.animate()
                    .setDuration(1000)
                    .alpha(.75f)
                    .setInterpolator(new LinearInterpolator())
                    .x(-1800)
                    .y(1800)
                    .rotation(Math.copySign(45, 500))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            removeViewInLayout(topCard);
                            selectedItemIndex++;
                            toolBox.toNotifyAdapter.notifyDataSetChanged();

                            ensureFull();
                            toolBox.cardPassed++;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            onAnimationEnd(animation);
                        }
                    });
        }
    }

    boolean FINISHED = false;

    public CardModel getFirstCard(){
        return (CardModel) getAdapter().getItem(selectedItemIndex);
    }


    private class GestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            //CardModel cardModel = (CardModel) getAdapter().getItem(selectedItemIndex);

            //mainActivity.doubleTap(cardModel);

            return false;
        }




        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            //callingFragment.startTransition((CardModel) getAdapter().getItem(selectedItemIndex));
            /*
            final View topCard = mTopCard;
            final RelativeLayout par = (RelativeLayout)(topCard.getParent()).getParent();
            final RelativeLayout l = (RelativeLayout)par.getParent();

            final int initW = topCard.getWidth();
            final int initH = topCard.getHeight();
            final LinearLayout sView = (LinearLayout) par.getChildAt(1);
            float w = par.getWidth();
            float h = par.getHeight();

            TranslateAnimation anim = new TranslateAnimation(0,0,0,-sView.getHeight());
            anim.setDuration(800);
            anim.setInterpolator(new AnticipateOvershootInterpolator());
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    par.setVisibility(GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            TranslateAnimation anim2 = new TranslateAnimation(0,0,0,((CardContainer)topCard.getParent()).getHeight());
            anim2.setDuration(800);
            anim2.setFillAfter(true);
            anim.setFillAfter(true);
            anim2.setInterpolator(new AnticipateOvershootInterpolator());




            AlphaAnimation anim4 = new AlphaAnimation(0,1);
            anim4.setDuration(800);
            anim4.setFillAfter(true);

            //anim4.setStartOffset(400);
            toolBox.toGetIn.startAnimation(anim4);
            //toolBox.callToSwitch.switchFragments();
            sView.startAnimation(anim);
            topCard.startAnimation(anim2);

           ((CardContainer) topCard.getParent()).startAnimation(anim2);











            //ANIMATOR
/*


            final ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(topCard,
                    PropertyValuesHolder.ofFloat("translationY", ((CardContainer)topCard.getParent()).getHeight())
            ).setDuration(400);
            animator.setInterpolator(new AnticipateOvershootInterpolator());
            animator.start();
            final ValueAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(topCard.getParent(),
                    PropertyValuesHolder.ofFloat("translationY", ((CardContainer)topCard.getParent()).getHeight())
                    ).setDuration(400);
            animator2.setInterpolator(new AnticipateOvershootInterpolator());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d("FINISHED", "Animation");
                    //animation.reverse();
                    //animator.reverse();

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    onAnimationEnd(animation);
                }
            });
            animator2.start();

            //

            //sView.setBackgroundColor(Color.TRANSPARENT);
            /*
            sView.animate()
                    .setDuration(50)
                    .translationY(10)
                    .setInterpolator(new AccelerateInterpolator())

                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            sView.animate()
                                    .setDuration(250)
                                    .translationY(-sView.getHeight() - 10)
                                    .setInterpolator(new AccelerateInterpolator())
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            Log.d("FINISHED", "Animation");
                                            mainActivity.switchFragments(cardModel, sView);

                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {
                                            onAnimationEnd(animation);
                                        }
                                    });

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            onAnimationEnd(animation);
                        }
                    });
            /*
            sView.animate()
                    .setDuration(250)
                    .translationY(-sView.getHeight())
                    //.alpha(0.0f)
                    .setInterpolator(new LinearInterpolator());
               */

            /*
            topCard.animate()
                    .setDuration(250)
                    .scaleX(w/topCard.getWidth())
                    .scaleY(h/topCard.getHeight())
                    .translationX(20)
                    .translationY(20)
                    .setInterpolator(new LinearInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Log.d("FINISHED","Animation");
                            mainActivity.switchFragments(cardModel);
                            sView.animate()
                                    .setDuration(10)
                                    .translationY(0)
                                    .setInterpolator(new LinearInterpolator());

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            onAnimationEnd(animation);
                        }
                    });
            */

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //Log.d("Fling", "Fling with " + velocityX + ", " + velocityY);
            final View topCard = mTopCard;
            final View secTop = secTopCard;
            float dx = e2.getX() - e1.getX();

            if (Math.abs(dx) > mTouchSlop &&
                    Math.abs(velocityX) > Math.abs(velocityY) &&
                    Math.abs(velocityX) > mFlingSlop * 5) {
                float targetX = topCard.getX();
                float targetY = topCard.getY();
                long duration = 0;

                boundsRect.set(0 - topCard.getWidth() - 100, 0 - topCard.getHeight() - 100, getWidth() + 100, getHeight() + 100);

                while (boundsRect.contains((int) targetX, (int) targetY)) {
                    targetX += velocityX / 10;
                    targetY += velocityY / 10;
                    duration += 100;
                }

                duration = Math.min(500, duration);
                tTopCard = getChildAt(getChildCount() -4);
                secTopCard = getChildAt(getChildCount() - 3);
                //if(secTopCard!=null) secTopCard.setAlpha(1);
                mTopCard = getChildAt(getChildCount() - 2);
                if(mTopCard != null)mTopCard.setOnClickListener(new MySpecialClick());

                //if(mTopCard!=null)mTopCard.setAlpha(1);
                CardModel cardModel = (CardModel)getAdapter().getItem(0);

                if(secTopCard != null)
                    secTopCard.setLayerType(LAYER_TYPE_HARDWARE,null);
                if(mTopCard != null)
                    mTopCard.setLayerType(LAYER_TYPE_HARDWARE, null);

                //toolBox.yep.setText("Flyying");



                if (cardModel.getOnCardDimissedListener() != null) {
                    if ( targetX > 0 ) {
                        cardModel.getOnCardDimissedListener().onDislike();
                        likeTop();
                        Log.d("CARD","liked");
                        //dislike
                    } else {
                        cardModel.getOnCardDimissedListener().onLike();
                        Log.d("CARD","disliked");
                    }
                }

                if(secTopCard!=null) {
                    ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(secTop,
                            PropertyValuesHolder.ofFloat("translationX", -40),
                            PropertyValuesHolder.ofFloat("translationY", -40),
                            PropertyValuesHolder.ofFloat("rotation", 0),
                            PropertyValuesHolder.ofFloat("pivotX", secTopCard.getWidth() / 2.f),
                            PropertyValuesHolder.ofFloat("pivotY", secTopCard.getHeight() / 2.f)
                    ).setDuration(250);
                    animator.setInterpolator(new AccelerateInterpolator());
                    animator.start();
                }

                if(targetX>0){
                    final boolean LIKE = true;
                }else{

                    final boolean LIKE = false;
                }

                topCard.animate()
                        .setDuration(duration)
                        .alpha(.75f)
                        .setInterpolator(new LinearInterpolator())
                        .x(targetX)
                        .y(targetY)
                        .rotation(Math.copySign(45, velocityX))
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                removeViewInLayout(topCard);
                                toolBox.toNotifyAdapter.notifyDataSetChanged();
                                ensureFull();
                                toolBox.cardPassed++;
                                FINISHED = true;

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                onAnimationEnd(animation);
                            }
                        });
                selectedItemIndex++;
                if(targetX>0 ){
                    toInsert = (CardModel) getAdapter().getItem(selectedItemIndex-1);
                    InsertInDb mDoPOST = new InsertInDb(mainActivity);
                    mDoPOST.execute("");
                }else{
                }


                return true;
            } else
                return false;
        }
    }

    public CardModel toInsert;

        public  byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }

    public PositionFirstCard getInfoFirstCard(){
        return new PositionFirstCard(mTopCard.getX(), mTopCard.getY(),mTopCard.getWidth() / 2,mTopCard.getHeight() / 2,((CardModel) getAdapter().getItem(selectedItemIndex)));
    }

    public class InsertInDb extends AsyncTask<String, Void, Boolean> {

        Context mContext = null;



        InsertInDb(Context context){
            mContext = context;

        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Log.d("selected",""+selectedItemIndex);
            DatabaseHelper dbHelper = new DatabaseHelper(mainActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TableNotes.COLUMN_UID, toInsert.getId());
            values.put(TableNotes.COLUMN_URL, toInsert.geturl());
            values.put(TableNotes.COLUMN_SPK, toInsert.getSpk());
            values.put(TableNotes.COLUMN_TITLE,toInsert.getTitle());
            values.put(TableNotes.COLUMN_SERIES,toInsert.getSeries());
            values.put(TableNotes.COLUMN_FROM,toInsert.getFrom());
            values.put(TableNotes.COLUMN_DATE,toInsert.getDate());
            values.put(TableNotes.COLUMN_TIME,toInsert.getTime());
            //values.put(TableNotes.COLUMN_IMG,getBytes(toInsert.getCardImageDrawable()));
            values.put(TableNotes.COLUMN_ABSTRACT, toInsert.getAbst());
            long id = db.insertOrThrow(TableNotes.TABLE_NAME_2, null, values);
            db.close();
            Log.d("PONDER DB", "Insert in " + id);
            toolBox.myColl.changeDataSet(-1);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean valid){ //POPULATE

        }

    }


}
