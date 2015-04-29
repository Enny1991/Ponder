package com.eneaceolini.ponder;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;


public class CardPlusMap extends Fragment{

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter = null;
    ImageView yellowInCard;
    ImageView yellowInMap;
    ViewPager mViewPager;
    static String globalKey;
    static CardModel obj;
    public static DoubleHolder myFrag;
    private Bitmap b = null;

    public CardPlusMap() {
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }




    @Override
    public void onStop(){
        super.onPause();
        Log.d("Called","ON STOP");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public CardPlusMap(CardModel obj,String key){
        this.obj = obj;
        this.globalKey = key;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.activity_collection_demo, container, false);
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getChildFragmentManager());
        yellowInCard = (ImageView)rootView.findViewById(R.id.yellowincard);
        yellowInMap = (ImageView)rootView.findViewById(R.id.yellowinmap);
        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:
                        yellowInCard.setImageDrawable(getResources().getDrawable(R.drawable.orange));
                        yellowInMap.setImageDrawable(getResources().getDrawable(R.drawable.gray));
                        break;
                    case 1:
                        yellowInCard.setImageDrawable(getResources().getDrawable(R.drawable.gray));
                        yellowInMap.setImageDrawable(getResources().getDrawable(R.drawable.orange));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Log.d("At The End","of thecret");
        return rootView;
    }


    public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    Log.d( "CREATE","0");
                    CardActivityFragment a = new CardActivityFragment();
                    a.obj = obj;
                    a.key = globalKey;
                    return a;
                default:
                    Log.d("CREATE","1");
                    return new MapFragmentD(obj);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    @Override
    public void onPause() {
        b = loadBitmapFromView(getView());
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

    @Override
    public void onDestroyView() {
        BitmapDrawable bd = new BitmapDrawable(b);
        getView().findViewById(R.id.pager).setBackground(bd);
        b = null;
        super.onDestroyView();
    }

}

