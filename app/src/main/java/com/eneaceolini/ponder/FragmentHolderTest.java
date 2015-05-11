package com.eneaceolini.ponder;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentHolderTest extends ActionBarActivity {


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
    Typeface typeFace,typeFace2;
    Toolbar toolBar;
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
        typeFace = Typeface.createFromAsset(getAssets(),
                "fonts/Cookie-Regular.ttf");
        typeFace2 = Typeface.createFromAsset(getAssets(),
                "fonts/YanoneKaffeesatz-Regular.ttf");
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

        // Set up your ActionBar
        //actionBar = getSupportActionBar();
        toolBar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_back));
        if(toolBox.myPhoto != null) {

            //CircleImageView myCircle = new CircleImageView(this);
            //myCircle.setImageBitmap(toolBox.myPhoto);

            getSupportActionBar().setIcon(new BitmapDrawable(cropRound(toolBox.myPhoto)));

        }else{
            getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.owl_icon_ponder2));
        }

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
                else{
                    toolBox.wantsToLogOut = true;
                    super.onBackPressed();
                }
                return true;
            case R.id.action_search:
                card.setIcon(getResources().getDrawable(R.drawable.ic_card_sel));
                sett.setIcon(getResources().getDrawable(R.drawable.ic_gear));
                star.setIcon(getResources().getDrawable(R.drawable.ic_star));
                open();
                return true;
            case R.id.action_star:
                card.setIcon(getResources().getDrawable(R.drawable.ic_card));
                sett.setIcon(getResources().getDrawable(R.drawable.ic_gear));
                star.setIcon(getResources().getDrawable(R.drawable.ic_star_sel));

                open2();
                return true;
            case R.id.action_settings:
                card.setIcon(getResources().getDrawable(R.drawable.ic_card));
                sett.setIcon(getResources().getDrawable(R.drawable.ic_gear_sel));
                star.setIcon(getResources().getDrawable(R.drawable.ic_star));
                open3();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    boolean exit = false;

    @Override
    public void onBackPressed() {
        if(!exit) {
            Toast.makeText(this, "Sure you want to Log Out", Toast.LENGTH_LONG).show();
            exit = true;
        }else{
            super.onBackPressed();
            toolBox.wantsToLogOut = true;
        }
    }

    public void startDelete(CardModel obj){
        Intent i = new Intent(this,ToDeleteActivity.class);
        toolBox.myPhoto = obj.getCardImageDrawable();
        i.putExtra("obj", obj);
        startActivity(i);
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

    }

    Dialog dialogNoTalks;
    public void showNoTalks(){
        dialogNoTalks = new Dialog(this,R.style.PauseDialog);
        dialogNoTalks.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNoTalks.setContentView(R.layout.for_dialog_text);
        TextView a = (TextView)dialogNoTalks.findViewById(R.id.message);
        a.setTypeface(typeFace);
        a.setText("Sorry!! No Talks for the specified TAGS");
        Button one =(Button)dialogNoTalks.findViewById(R.id.button3);
        one.setTypeface(typeFace);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNoTalks.dismiss();
            }
        });

        dialogNoTalks.show();
    }

    public Bitmap cropRound(Bitmap b){
        Bitmap bitmap = b;
        Bitmap output ;
        try {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
        }catch(Exception e){

            output = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2, bitmap.getHeight()/2, true);
            output = Bitmap.createBitmap(output.getWidth(), output
                    .getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);


        BitmapShader shader;
        shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        int radius = bitmap.getWidth()/2;
        RectF rect = new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight());

        // rect contains the bounds of the shape
        // radius is the radius in pixels of the rounded corners
        // paint contains the shader that will texture the shape
        canvas.drawRoundRect(rect, radius, radius, paint);
        return output;
    }


}



