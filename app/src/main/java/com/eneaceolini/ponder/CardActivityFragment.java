package com.eneaceolini.ponder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class CardActivityFragment extends Fragment  {

    DisplayMetrics metrics;
    int maxWidth,maxHeight;
    private Typeface typeFace;
    private Typeface typeFace2;
    private static LinearLayout.LayoutParams frameParam;
    private LinearLayout frame;
    private GestureDetector mGestureDetector;
    public CardModel obj;
    public String key;
    public GestureDetector gg;

    public CardActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_card, container, false);


        GestureDetector detector = new GestureDetector(getActivity(), new GestureListener());
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        maxWidth = metrics.widthPixels*7/10;
        maxHeight = metrics.heightPixels*6/10;
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookie-Regular.ttf");
        typeFace2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/YanoneKaffeesatz-Regular.ttf");
        frame = (LinearLayout)rootView.findViewById(R.id.frame);
        ScrollView v = (ScrollView) ((LinearLayout)frame.getChildAt(0)).getChildAt(0);
        LinearLayout textContainer = (LinearLayout)((ScrollView) ((LinearLayout)frame.getChildAt(0)).getChildAt(0)).getChildAt(0);
        ((TextView)textContainer.getChildAt(0)).setText(obj.getSpk());
        ((TextView)textContainer.getChildAt(0)).setTypeface(typeFace);
        ((TextView)textContainer.getChildAt(1)).setText(obj.getTitle());
        ((TextView)textContainer.getChildAt(1)).setTypeface(typeFace2);
        ((TextView)textContainer.getChildAt(2)).setText(obj.getFrom());
        ((TextView)textContainer.getChildAt(2)).setTypeface(typeFace2);
        ((TextView)textContainer.getChildAt(3)).setText(obj.getDate());
        ((TextView)textContainer.getChildAt(3)).setTypeface(typeFace2);
        ((TextView)textContainer.getChildAt(4)).setText(obj.getTime());
        ((TextView)textContainer.getChildAt(4)).setTypeface(typeFace2);
        //((TextView)textContainer.getChildAt(0)).setText(obj.getLocation());
        ((TextView)textContainer.getChildAt(5)).setText(obj.getAbst());
        ((TextView)textContainer.getChildAt(5)).setTypeface(typeFace2);
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureListener();
        final GestureDetector gd = new GestureDetector(getActivity(), gestureListener);



        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gd.onTouchEvent(motionEvent);
                return false;
            }
        });

        new LoadImages(getActivity(),"").execute("");
        return rootView;
    }

    public class LoadImages extends AsyncTask<String, Void, Boolean> {

        Context mContext = null;
        String strNameToSearch = "";

        //Result data
        String strFirstName;
        String strLastName;
        int intAge;
        int intPoints;

        Exception exception = null;

        LoadImages(Context context, String nameToSearch){
            mContext = context;
            //nameToSearch  = nameToSearch.replaceAll("\\s","+");
            strNameToSearch = nameToSearch;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httpget = new HttpGet("http://www.pondertalks.com/account/mytalks/?format=json");
                httpget.setHeader("Authorization", "Token " + key);
                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httpget);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                            "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                    } catch (Exception squish) {
                    }
                }
                Log.d("RESULT MY TALKS",""+result);

            }catch (Exception e){}
                /*

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    SimpleDateFormat myFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat myFormat_time = new SimpleDateFormat("h:mm a");

                    try {
                        String reformSTRDate;
                        String reformSTRTime;

                        if(jsonArray.length() == 0)

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            reformSTRDate = myFormat_date.format(fromUser.parse(c.getString(DATE)));
                            reformSTRTime = myFormat_time.format(fromUser.parse(c.getString(DATE)));
                            Bitmap bmp;
                            try {

                                bmp = BitmapFactory.decodeStream((InputStream)
                                        new java.net.URL(c.getString(URL)).getContent());

                            } catch (MalformedURLException e) {
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
                                e.printStackTrace();
                            } catch (IOException e) {
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
                                e.printStackTrace();
                            }

                            try {
                                bmp = getResizedBitmap(bmp, maxHeight, maxWidth);
                                cardInfoVector.add(new CardInfo(c.getString(URL), bmp
                                        , c.getString(SPEAKER), c.getString(TITLE)
                                        , c.getString(SERIES), c.getString(FROM),
                                        reformSTRDate, reformSTRTime, c.getString(ABSTRACT)));

                            }catch(Exception e){
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
                                bmp = getResizedBitmap(bmp, maxHeight, maxWidth);
                                cardInfoVector.add(new CardInfo(c.getString(URL), bmp
                                        , c.getString(SPEAKER), c.getString(TITLE)
                                        , c.getString(SERIES), c.getString(FROM),
                                        reformSTRDate, reformSTRTime, c.getString(ABSTRACT)));
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //String aJsonString = jObject.getString("pic");
                //Log.d("URL:",aJsonString);

            }catch (Exception e){
                Log.e("ClientServerDemo", "Error:", e);
                exception = e;
            }
            */

            return true;
        }





        @Override
        protected void onPostExecute(Boolean valid) { //POPULATE
        }
    }



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


    class CardInfo {
        Bitmap url;
        String URL, speaker,title,series,from,date, time,abst;

        public CardInfo(String URL,Bitmap url, String speaker, String title, String series,
                        String from, String date, String time, String abst){
            this.URL = URL;
            this.url = url;
            this.speaker = speaker;
            this.title = title;
            this.series = series;
            this. from = from;
            this.date = date;
            this.time = time;
            this.abst = abst;
        }



    }





    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (e.getAction() == 1) {
                Log.d("BOH", "but double");
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (e.getAction() == 1) {
                Log.d("Gesure", "DOUBLE");
            }
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (e.getAction() == 1) {
                Log.d("SIngle", "CONF");
            }
            return true;
        }
    }
}
