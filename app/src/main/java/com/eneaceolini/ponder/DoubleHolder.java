package com.eneaceolini.ponder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;


public class DoubleHolder extends ActionBarActivity {


    NextPageFragment nextPage = new NextPageFragment();
    SettActivityFragment settPage = new SettActivityFragment();
    MapFragmentD mapPage = new MapFragmentD();
    CollectionCard likedCard = new CollectionCard();
    CardPlusMap cardPage = new CardPlusMap();
    NextPageFragment f ;
    ActionBar actionBar;
    public CardModel obj;
    MapFragmentD mapFrag;
    Button actionBarSent;
    Button actionBarLocations;
    ToolBox toolBox;
    Button actionBarStaff;
    String globalKey;
    CardListMap secondPagerAdapter;
    DisplayMetrics metrics;
    int maxWidth,maxHeight;


    DoubleHolder myContext;


    NonSwipeableViewPager mViewPager;
    ViewPager secondLayerPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.double_holder);
        Intent e = getIntent();
        globalKey = e.getStringExtra("key");
        f = new NextPageFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new NextPageFragment())
                    .commit();
        }

        myContext = this;
        Log.d("starting","next");

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        maxWidth = metrics.widthPixels*8/10;
        maxHeight = metrics.heightPixels*7/10;

        //Retrive key

        if(toolBox!=null) toolBox = null;
        else toolBox = ToolBox.getInstance();

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
        actionBarStaff.setOnClickListener(new myClickStar());//actionBarStaff.setText("Staff");
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



    public void doubleTap(CardModel obj){
        this.obj = obj;

    }

    @Override
    public void onBackPressed() {
        if(secondPagerAdapter != null) {
            (f.getView().findViewById(R.id.pager)).setVisibility(View.VISIBLE);
            TranslateAnimation anim = new TranslateAnimation(0,0,-v.getHeight(),0);
            anim.setDuration(400);
            anim.setInterpolator(new BounceInterpolator());

            v.startAnimation(anim);
            secondPagerAdapter = null;
            secondLayerPager = (ViewPager) f.getView().findViewById(R.id.pager2);
            secondLayerPager.setAdapter(null);
            secondLayerPager.setVisibility(View.GONE);
        }else{
                super.onBackPressed();
            }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public class myClickStar implements View.OnClickListener {
        @Override
        public void onClick(View v){

        }
    }


    public void open(){ //settings
        if(secondPagerAdapter != null) onBackPressed();
        toolBox.lowerViewPager.setCurrentItem(2);
        actionBarStaff.setBackgroundResource(R.drawable.star1);
        actionBarSent.setBackgroundResource(R.drawable.card1);
        actionBarLocations.setBackgroundResource(R.drawable.sett2);
        toolBox.isFirst = false;
    }

    public void open3(){ // liked cards
        if(secondPagerAdapter != null) onBackPressed();
        toolBox.lowerViewPager.setCurrentItem(1);
        actionBarSent.setBackgroundResource(R.drawable.card1);
        mapFrag = null;
        actionBarStaff.setBackgroundResource(R.drawable.star2);
        actionBarLocations.setBackgroundResource(R.drawable.sett1);
        toolBox.isFirst = false;
    }

    public void open2(){ // search
        if(secondPagerAdapter != null) onBackPressed();
        toolBox.lowerViewPager.setCurrentItem(0);
        actionBarSent.setBackgroundResource(R.drawable.card2);
        actionBarStaff.setBackgroundResource(R.drawable.star1);
        actionBarLocations.setBackgroundResource(R.drawable.sett1);
    }

    View v;

    public void switchFragments(CardModel obj,View v){
        this.v=v;


        this.obj = obj;
        Log.d("ENTERED","doubleTap in DOUBLE");

        (f.getView().findViewById(R.id.pager)).setVisibility(View.GONE);
        secondPagerAdapter = new CardListMap(getSupportFragmentManager());
        secondLayerPager = (ViewPager) f.getView().findViewById(R.id.pager2);
        secondLayerPager.setAdapter(secondPagerAdapter);
        secondLayerPager.setVisibility(View.VISIBLE);


    }

    public class CardListMap extends FragmentStatePagerAdapter {


        public CardListMap(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    Log.d("CREATE","0");
                    return null;
                case 1:
                    Log.d( "CREATE","1");
                    CardActivityFragment a = new CardActivityFragment();
                    a.obj = obj;
                    a.key = globalKey;
                    return a;
                default:
                    Log.d("CREATE","2");
                    return new MapFragmentD(obj);
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





}
