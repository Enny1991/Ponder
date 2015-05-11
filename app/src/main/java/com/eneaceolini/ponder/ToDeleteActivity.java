package com.eneaceolini.ponder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


public class ToDeleteActivity extends ActionBarActivity {
    // TODO TransistionManager

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter = null;
    ImageView yellowInCard;
    ImageView yellowInMap;
    ViewPager mViewPager;
    static String globalKey;
    static CardModel obj;
    public static DoubleHolder myFrag;
    private Bitmap b = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roll_for_dialog);
        Intent i = getIntent();
        obj = i.getParcelableExtra("obj");
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        yellowInCard = (ImageView)findViewById(R.id.yellowincard);
        yellowInMap = (ImageView)findViewById(R.id.yellowinmap);
        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
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
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDefaultDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_back2));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_to_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                onBackPressed();
            case R.id.action_settings:
                return true;
            case R.id.action_save:
                Toast.makeText(this, "To Save", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                //case 0:
                  //  return OnlyCardFragment.newInstance(obj);
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
    public void onBackPressed(){

        finish();
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }

}
