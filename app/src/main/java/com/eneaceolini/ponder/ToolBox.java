package com.eneaceolini.ponder;

import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.widget.Button;

/**
 * Created by Enea on 24/01/15.
 */

public class ToolBox {

    private static ToolBox me;
    public Button yep ;
    public Button nop ;
    public SimpleCardStackAdapter adapter;
    public int cardPassed=0;
    public boolean isFirst=true;
    public boolean comesFromSearch=false;
    public Bitmap myPhoto;
    CollectionCard.DemoCollectionPagerAdapter toNotifyAdapter = null;
    CollectionCard myColl;
    NonSwipeableViewPager lowerViewPager ;
    public NextPageFragment callToSwitch;
    public ViewPager toGetIn;
    public boolean wantsToLogOut = false;



    public static ToolBox getInstance(){
        if (me==null) me=new ToolBox();
        return me;

    }

    private ToolBox(){
        super();
    }



}

