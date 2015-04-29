package com.eneaceolini.ponder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class FragmentHolderTest extends ActionBarActivity {


    NextPageFragment nextPage = new NextPageFragment();
    SettActivityFragment settPage = new SettActivityFragment();
    MapFragmentD mapPage = new MapFragmentD();
    CollectionCard likedCard = new CollectionCard();
    CardPlusMap cardPage = new CardPlusMap();
    ActionBar actionBar;
    public CardModel obj;
    MapFragmentD mapFrag;
    Button actionBarSent;
    Button actionBarLocations;
    ToolBox toolBox;
    Button actionBarStaff;
    String globalKey;
    DoubleHolder upperContext;
    private Bitmap b = null;

    FragmentHolderTest myContext;

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter = null;
    NonSwipeableViewPager mViewPager;




    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder2);


        Intent e = getIntent();
        globalKey = e.getStringExtra("key");
        myContext = this;
        Log.d("starting","next");
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        toolBox = ToolBox.getInstance();
        //toolBox.lowerViewPager = mViewPager;

        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar,
                null);

        ImageView pic = (ImageView)actionBarLayout.findViewById(R.id.barpic);
        if(toolBox.myPhoto != null) pic.setImageBitmap(toolBox.myPhoto);

        // Set up your ActionBar
        //actionBar = getActionBar();
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.rect2));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setCustomView(actionBarLayout);
        actionBar.setCustomView(actionBarLayout,new ActionBar.LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT));
        Toolbar parent =(Toolbar) actionBarLayout.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        //actionBar.setElevation(0);


        // cards
        actionBarSent = (Button) findViewById(R.id.action_bar_sent);
        actionBarSent.setBackgroundResource(R.drawable.card2);
        actionBarSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open2();
            }
        });
        //actionBarSent.setText("Sent");

        // star button
        actionBarStaff = (Button) findViewById(R.id.action_bar_staff);
        //actionBarStaff.setOnClickListener(new myClickStar());//actionBarStaff.setText("Staff");
        actionBarStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open3();
            }
        });
        // setts
        actionBarLocations = (Button) findViewById(R.id.action_bar_locations);
        actionBarLocations.setBackgroundResource(R.drawable.sett1);
        actionBarLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });
        //actionBarLocations.setText("HIPPA Locations");

    }

    public void open(){ //settings
        //if(secondPagerAdapter != null) onBackPressed();
        mViewPager.setCurrentItem(2);
        actionBarStaff.setBackgroundResource(R.drawable.star1);
        actionBarSent.setBackgroundResource(R.drawable.card1);
        actionBarLocations.setBackgroundResource(R.drawable.sett2);
        toolBox.isFirst = false;
    }

    public void open3(){ // liked cards
        //if(secondPagerAdapter != null) onBackPressed();
        mViewPager.setCurrentItem(1);
        actionBarSent.setBackgroundResource(R.drawable.card1);
        mapFrag = null;
        actionBarStaff.setBackgroundResource(R.drawable.star2);
        actionBarLocations.setBackgroundResource(R.drawable.sett1);
        toolBox.isFirst = false;
    }

    public void open2(){ // search
        //if(secondPagerAdapter != null) onBackPressed();
        mViewPager.setCurrentItem(0);
        actionBarSent.setBackgroundResource(R.drawable.card2);
        actionBarStaff.setBackgroundResource(R.drawable.star1);
        actionBarLocations.setBackgroundResource(R.drawable.sett1);
    }



    public void doubleTap(CardModel obj){
        this.obj = obj;


    }


    InternalFragmentHolder a;



    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {


        public DemoCollectionPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    a = new InternalFragmentHolder();
                    a.mainActivity = myContext;
                    return a;
                case 1:
                    CollectionCard b = new CollectionCard();
                    return b;
                default:
                    SettActivityFragment f = new SettActivityFragment();
                    f.key = globalKey;
                    return f;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }



    @Override
    public void onPause() {
        //b = loadBitmapFromView(getView());
        super.onPause();
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(),
                v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(),
                v.getHeight());
        v.draw(c);
        return b;
    }
/*
    @Override
    public void onDestroyView() {
        BitmapDrawable bd = new BitmapDrawable(b);
        getView().findViewById(R.id.container).setBackground(bd);
        b = null;
        super.onDestroyView();
    }
*/
    }



