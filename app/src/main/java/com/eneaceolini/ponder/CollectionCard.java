package com.eneaceolini.ponder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;


public class CollectionCard extends Fragment{

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    public static ImageView[] listOfViews;
    ViewPager mViewPager;
    static public ToolBox toolBox;
    static CollectionCard context;
    static Context cc;
    private Button deleteFromDB;

    public CollectionCard() {
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
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    static Vector<CardModel> listCard = new Vector<>();
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        //Log.d("original height:", "" + height + " instead of" + newHeight);
        //Log.d("original width:", "" + width + " instead of" + newWidth);
        int scale = 1;
        Matrix matrix = new Matrix();
        Bitmap resizedBitmap;
        if (newHeight < height && newWidth < width) {
            resizedBitmap = bm;
        } else {
            if ((newHeight - height) > (newWidth - width)) {
                scale = (int) Math.ceil(newHeight / height) + 1;
                resizedBitmap = Bitmap.createScaledBitmap(bm, width * scale, newHeight, true);
            } else {
                scale = (int) Math.ceil(newWidth / width) + 1;
                resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, height * scale, true);
            }
            // "RECREATE" THE NEW BITMAP

        }

        //Log.d("new height:", "" + resizedBitmap.getHeight());
        //Log.d("new width:", "" + resizedBitmap.getWidth());
        resizedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, newWidth, newHeight);

        return resizedBitmap;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = this;

        cc = getActivity();

        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Cookie-Regular.ttf");
        Typeface typeFace2 = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/YanoneKaffeesatz-Regular.ttf");
        // COllect talks
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor;
        String query = "SELECT * FROM talks ORDER BY _id ASC";
        cursor = db.rawQuery(query,null);
        //Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.img1);
        //bm = getResizedBitmap(bm,maxHeight,maxWidth);
        while(cursor.moveToNext()){
            listCard.add(new CardModel(cursor.getInt(10),cursor.getString(1),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(9),
                    cursor.getString(5),
                    cursor.getString(3),
                    cursor.getString(2),
                    null,
                    typeFace,typeFace2,0.0,0.0));

        }
        //
        listOfViews = new ImageView[listCard.size()];
        View rootView = inflater.inflate(R.layout.activity_collection_demo2, container, false);
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getChildFragmentManager());
        // Set up the ViewPager, attaching the adapter.

        toolBox = ToolBox.getInstance();
        toolBox.toNotifyAdapter = mDemoCollectionPagerAdapter;
        toolBox.myColl = this;
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);

        return rootView;
    }


    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            DemoObjectFragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            args.putInt(DemoObjectFragment.ARG_OBJECT, i);
            fragment.setArguments(args);
            fragment.setContext(context);
            return fragment;
        }

        @Override
        public int getCount() {
             return listCard.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + (position + 1)+" / "+getCount();
        }
    }




    public void changeDataSet(int position){
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Cookie-Regular.ttf");
        Typeface typeFace2 = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/YanoneKaffeesatz-Regular.ttf");
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor;
        String query = "SELECT * FROM talks ORDER BY _id ASC";
        cursor = db.rawQuery(query,null);
        //Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.img1);
        //bm = getResizedBitmap(bm,maxHeight,maxWidth);
        listCard = new Vector<>();
        while(cursor.moveToNext()){
            listCard.add(new CardModel(cursor.getInt(10),cursor.getString(1),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(9),
                    cursor.getString(5),
                    cursor.getString(3),
                    cursor.getString(2),
                    null,
                    typeFace,typeFace2,0.0,0.0));
        }
        db.close();
        cursor.close();
        if(position > -1) {
            mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getChildFragmentManager());
            // Set up the ViewPager, attaching the adapter.

            toolBox.toNotifyAdapter = mDemoCollectionPagerAdapter;

            mViewPager.setAdapter(mDemoCollectionPagerAdapter);
            mViewPager.setCurrentItem(position);
        }
    }

    public static class DemoObjectFragment extends Fragment {

        int index2;
        static int index;
        Button delete,save,pop;
        private TextView counter ;
        public CollectionCard context;
        public static final String ARG_OBJECT = "object";

        public void setContext(CollectionCard c){
            context = c;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
            Bundle args = getArguments();
            final ImageView mImageView = (ImageView)rootView.findViewById(R.id.image);
            final ProgressBar prog = (ProgressBar)rootView.findViewById(R.id.prog);

            delete = (Button) rootView.findViewById(R.id.delete_button);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFromDB(v);
                }
            });

            pop = (Button) rootView.findViewById(R.id.button4);
            pop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CardModel toPass = listCard.get(index2);
                    Drawable d = mImageView.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                    toPass.setCardImageDrawable(bitmap);
                    toolBox.callToSwitch.mainActivity.startDelete(toPass);


                }
            });

            save = (Button)rootView.findViewById(R.id.save_button);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //deleteFromDB(v);
                    Log.d("CLicked", "SAVE");
                }
            });

            if(listCard.get(args.getInt(ARG_OBJECT)).getSpk().trim().toString().equals("")
                    || listCard.get(args.getInt(ARG_OBJECT)).getSpk().trim().toString().equals(null)
                    || listCard.get(args.getInt(ARG_OBJECT)).getSpk().equals(" ")){
                (rootView.findViewById(R.id.spk)).setVisibility(View.GONE);
            }else {
                ((TextView) rootView.findViewById(R.id.spk)).setText(
                        listCard.get(args.getInt(ARG_OBJECT)).getSpk());
            }


            ((TextView) rootView.findViewById(R.id.title)).setText(
                    listCard.get(args.getInt(ARG_OBJECT)).getTitle());

            if(listCard.get(args.getInt(ARG_OBJECT)).getSeries().trim().toString().equals("")
                    || listCard.get(args.getInt(ARG_OBJECT)).getSeries().trim().toString().equals(null)
                    || listCard.get(args.getInt(ARG_OBJECT)).getSeries().equals(" ")
                    || listCard.get(args.getInt(ARG_OBJECT)).getSeries().equals("null")){
                (rootView.findViewById(R.id.series)).setVisibility(View.GONE);
            }else {
                ((TextView) rootView.findViewById(R.id.series)).setText(
                        listCard.get(args.getInt(ARG_OBJECT)).getSeries());
            }


            index= args.getInt(ARG_OBJECT);
            index2 = args.getInt(ARG_OBJECT);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        LoadImages loader = new LoadImages(listCard.get(index).geturl());
                        final Bitmap bmp = loader.execute("").get();
                        mImageView.post(new Runnable() {
                            public void run() {
                                prog.setVisibility(View.INVISIBLE);
                                mImageView.setImageBitmap(bmp);
                            }
                        });
                    }catch(Exception e){Log.e("Loading Images","error");}
                }
            }).start();



            return rootView;
        }

        public void deleteFromDB(View v){
            //TODO add a contextual dialog
            DatabaseHelper dbHelper = new DatabaseHelper(cc);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Log.d("title",listCard.get(index2).getTitle());
            String query = "SELECT * FROM talks ORDER BY _id ASC";


            int a = db.delete(TableNotes.TABLE_NAME_2, TableNotes.COLUMN_UID + " = " + listCard.get(index2).getId(), null);
            Log.d("DELETED RESULT", "" + a);
            db.close();
            context.changeDataSet(index2);


        }
    }


    static class LoadImages extends AsyncTask<String, Void, Bitmap> {

        Context context;
        String URL;

        public LoadImages(String URL){
            this.URL = URL;
        }

        @Override
        protected Bitmap doInBackground(String... arg0) {

            Bitmap bmp = null;
            try {


                bmp = BitmapFactory.decodeStream((InputStream)
                        new java.net.URL(URL).getContent());
                //Log.d("Load Images","Gooood");



            } catch (MalformedURLException e) {
                //bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.img1);
                //e.printStackTrace();
                //((ImageView) rootView.findViewById(R.id.image)).setImageBitmap(bmp);
                Log.e("Loading Images","Malformed URL");

            } catch (IOException e) {
                //bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
                //e.printStackTrace();
                //((ImageView) rootView.findViewById(R.id.image)).setImageBitmap(bmp);
                Log.e("Loading Images","IO Problem");

            }

            try {
                //bmp = getResizedBitmap(bmp, maxHeight, maxWidth);


            }catch(Exception e){
                //bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
                //bmp = getResizedBitmap(bmp, maxHeight, maxWidth);

                Log.e("Loading Images","An image is null");
            }


            return bmp;
        }





        @Override
        protected void onPostExecute(Bitmap bmp){ //POPULATE


        }

    }

}



