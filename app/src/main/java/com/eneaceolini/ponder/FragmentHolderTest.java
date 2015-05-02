package com.eneaceolini.ponder;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


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

        //ImageView pic = (ImageView)actionBarLayout.findViewById(R.id.barpic);
        if(toolBox.myPhoto != null) {


                //HOME.setIcon(new BitmapDrawable(toolBox.myPhoto));

        }
        // Set up your ActionBar
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_back));
        actionBar.setIcon(R.drawable.ic_launcher);

    }

    public void open3(){ //settings
        mViewPager.setCurrentItem(2);
        toolBox.isFirst = false;
    }

    public void open2(){ // liked cards
        mViewPager.setCurrentItem(1);
        toolBox.isFirst = false;
    }

    public void open(){ // search
        mViewPager.setCurrentItem(0);
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

    MenuItem card ;
    MenuItem star ;
    MenuItem sett ;
    MenuItem HOME;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        //HOME = menu.findItem(android.R.id.home);
        //HOME.setIcon(new BitmapDrawable(toolBox.myPhoto));
        card = menu.getItem(0);
        star = menu.getItem(1);
        sett = menu.getItem(2);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!exit) {
                    Toast.makeText(this, "Sure you want to Log Out", Toast.LENGTH_LONG).show();
                    //item.setIm(new BitmapDrawable(toolBox.myPhoto));
                    exit=true;
                }
                else
                    super.onBackPressed();
                return true;
            case R.id.action_search:
                card.setIcon(getResources().getDrawable(R.drawable.card2));
                sett.setIcon(getResources().getDrawable(R.drawable.sett1));
                star.setIcon(getResources().getDrawable(R.drawable.star1));
                open();
                return true;
            case R.id.action_star:
                card.setIcon(getResources().getDrawable(R.drawable.card1));
                sett.setIcon(getResources().getDrawable(R.drawable.sett1));
                star.setIcon(getResources().getDrawable(R.drawable.star2));
                open2();
                return true;
            case R.id.action_settings:
                card.setIcon(getResources().getDrawable(R.drawable.card1));
                sett.setIcon(getResources().getDrawable(R.drawable.sett2));
                star.setIcon(getResources().getDrawable(R.drawable.star1));
                open3();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    boolean exit = false;

    @Override
    public void onBackPressed() {
        if(!exit)
        Toast.makeText(this,"Sure you want to Log Out",Toast.LENGTH_LONG).show();
        else
        super.onBackPressed();
    }
}



