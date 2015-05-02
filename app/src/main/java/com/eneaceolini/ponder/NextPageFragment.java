package com.eneaceolini.ponder;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.eneaceolini.ponder.ContactsCompletionView;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.eneaceolini.ponder.Person;
import com.tokenautocomplete.TokenCompleteTextView;

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
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.Vector;


public class NextPageFragment extends Fragment implements TokenCompleteTextView.TokenListener{

    ProgressBar progBar;
    ToolBox toolBox;
    private Typeface typeFace;
    private Typeface typeFace2;
    DisplayMetrics metrics;
    int maxWidth,maxHeight;
    public FragmentHolderTest mainActivity;
    private final Double LAT = 47.3667;
    private final Double LON = 8.5500;
    private boolean firstCreated = false;
    EditText search;
    public boolean isConnected = false;
    TextView conn;
    Button refresh;
    NetworkInfo activeNetwork;
    BroadcastReceiver myConnRec;
    View rootView;
    NextPageFragment context ;
    Button actionBarSent,globalSearch;
    ContactsCompletionView completionView;
    Person[] people;
    ArrayAdapter<Person> adapter;
    TextView sorry;
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;



    protected final String SPEAKER = "speaker_name" , TITLE="title" , URL = "pic_cached" ,
            SERIES = "talk_series",FROM = "speaker_from",
            DATE = "start_time_date", ABSTRACT = "talk_abstract";

    public NextPageFragment() {
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
    }

    @Override
    public void onDestroy(){
        if(myConnRec!=null) getActivity().unregisterReceiver(myConnRec);
        super.onPause();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toolBox = ToolBox.getInstance();
        Log.d("little","comment for GitHub");
        rootView = inflater.inflate(R.layout.activity_next_page, container, false);
        context = this;

        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.to_get_in);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setX(-1000);
        toolBox.toGetIn = mViewPager;

        typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                                                                "fonts/Cookie-Regular.ttf");
        typeFace2 = Typeface.createFromAsset(getActivity().getAssets(),
                                                            "fonts/YanoneKaffeesatz-Regular.ttf");

        sorry = (TextView)rootView.findViewById(R.id.sorry);
        sorry.setTypeface(typeFace2);
        mCardContainer = (CardContainer) rootView.findViewById(R.id.layoutview);

        mCardContainer.mainActivity = mainActivity;
        mCardContainer.callingFragment = this;


        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        maxWidth = metrics.widthPixels*8/10;
        maxHeight = metrics.heightPixels*7/10;

        progBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progBar.setIndeterminate(true);
        progBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x389289));
        //tagLayout = (LinearLayout)rootView.findViewById(R.id.tagLayout);

        //CheckConnection


        //
        refresh = (Button)rootView.findViewById(R.id.button2);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {
                    goSearch("This week");
                    toolBox.isFirst=true;
                    v.setVisibility(View.INVISIBLE);
                    conn.setVisibility(View.INVISIBLE);
                }

            }
        });


        if(!firstCreated) {
            goSearch("This+week");
            toolBox.isFirst=true;
        }else {
            toolBox.isFirst = false;
            if (toolBox.adapter != null) goSearch2();
        }
        firstCreated = true;
        conn = (TextView)rootView.findViewById(R.id.textView7);
        Button b = (Button) rootView.findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.isFirst=true;
                toolBox.adapter = null;
                ListAdapter a = completionView.getAdapter();
                //String ss="";
                //for(int i = 0;i<a.getCount()-1;i++){
                //    ss += ((Person)a.getItem(i)).getTag().replaceAll("\\s","+")+"%2C";
                //}
                //ss += ((Person)a.getItem(a.getCount()-1)).getTag().replaceAll("\\s","+");
                Log.d("search",ss.substring(0,ss.length()-3));
                goSearch(ss.substring(0,ss.length()-3));
                sorry.setVisibility(View.INVISIBLE);
                mCardContainer.setAdapter(new SimpleCardStackAdapter(getActivity()));
                toolBox.adapter=null;
                toolBox.cardPassed=0;

            }
        });
/*
                EditText mainEdit = (EditText) rootView.findViewById(R.id.editText3);

                mainEdit.setFocusableInTouchMode(true);
                mainEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mainEdit, InputMethodManager.SHOW_IMPLICIT);
                //mainEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                mainEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            toolBox.isFirst=true;
                            toolBox.adapter = null;
                            goSearch(v.getText().toString());
                            mCardContainer.setAdapter(new SimpleCardStackAdapter(getActivity()));
                            toolBox.adapter=null;
                            toolBox.cardPassed=0;
                            handled = true;
                        }

                        return handled;
                    }
                });*/




        people = new Person[]{
                new Person("This Week"),
                new Person("Neuroscience"),
                new Person("Physics"),
                new Person("Within 200 Km from Zurich")
        };

        adapter = new FilteredArrayAdapter<Person>(getActivity(), R.layout.person_layout, people) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }

                Person p = getItem(position);
                ((TextView)convertView.findViewById(R.id.name)).setText(p.getTag());

                return convertView;
            }

            @Override
            protected boolean keepObject(Person obj, String mask) {
                mask = mask.toLowerCase();
                return obj.getTag().toLowerCase().startsWith(mask);
            }
        };

        completionView = (ContactsCompletionView)rootView.findViewById(R.id.searchView);
        completionView.setAdapter(adapter);
        completionView.setTokenListener(this);

        if (savedInstanceState == null) {
            //completionView.setPrefix("To: ");
            //completionView.addObject(people[0]);
            //completionView.addObject(people[1]);
        }




        return rootView;
    }



    public Button goSearch;
    private Vector<CardInfo> cardInfoVector = new Vector<>();
    CardContainer mCardContainer;

    public void goSearch2(){

        for(int i =0;i<toolBox.cardPassed;i++)
        toolBox.adapter.pop();

        toolBox.cardPassed = 0;
        toolBox.isFirst = true;

        mCardContainer.setOrientation(Orientations.Orientation.Ordered);


        mCardContainer.setAdapter(toolBox.adapter);

    }



    public CardModel getFirstCard(){
        return mCardContainer.getFirstCard();
    }

    public void goSearch(String searchString){

        //take tag and do request by async task
        //on post execute I populate
        LoadImages mDoPOST = new LoadImages(getActivity(), searchString);
        progBar.setVisibility(View.VISIBLE);
        mDoPOST.execute("");
    }
    public String strURL;
    public Vector<ImageView> vectorImages= new Vector<>();

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

            try{

                //Setup the parameters

                //Add more parameters as necessary

                //Create the HTTP request

                toolBox.adapter = null;
                DefaultHttpClient  httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httpget = new HttpGet("http://www.pondertalks.com/talks/?searchterms="+strNameToSearch+"&format=json");
                //httpget.setHeader("page", "2");
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
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }


                cardInfoVector.removeAllElements();

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    SimpleDateFormat myFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat myFormat_time = new SimpleDateFormat("h:mm a");

                    try {
                        String reformSTRDate;
                        String reformSTRTime;

                        if(jsonArray.length() == 0)
                            sorry.setVisibility(View.VISIBLE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        reformSTRDate = myFormat_date.format(fromUser.parse(c.getString(DATE)));
                        reformSTRTime = myFormat_time.format(fromUser.parse(c.getString(DATE)));
                        JSONObject venue = c.getJSONObject("venue");
                        Double localLAT = LAT;
                        Double localLONG = LON;
                        if(!venue.isNull("address")) {
                            JSONObject address = venue.getJSONObject("address");
                            if (address != null) {
                                JSONObject position = address.getJSONObject("position");
                                localLAT = Double.parseDouble(position.getString("lat"));
                                localLONG = Double.parseDouble(position.getString("long"));
                            }
                        }


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
                                    reformSTRDate, reformSTRTime, c.getString(ABSTRACT),
                                    localLAT,
                                    localLONG));

                        }catch(Exception e){
                            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
                            bmp = getResizedBitmap(bmp, maxHeight, maxWidth);
                            cardInfoVector.add(new CardInfo(c.getString(URL), bmp
                                    , c.getString(SPEAKER), c.getString(TITLE)
                                    , c.getString(SERIES), c.getString(FROM),
                                    reformSTRDate, reformSTRTime, c.getString(ABSTRACT),
                                    localLAT,
                                    localLONG));
                            Log.e("Loading Images","An image is null");
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

            return true;
        }





        @Override
        protected void onPostExecute(Boolean valid){ //POPULATE
            progBar.setVisibility(View.INVISIBLE);
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {

                    SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(getActivity());

                    mCardContainer.setOrientation(Orientations.Orientation.Ordered);

                    String cover=null;
                    for(int i=0;i<cardInfoVector.size();i++){
                        cover=cardInfoVector.elementAt(i).speaker;
                         if(cover.equals("null")) cover = " ";
                        adapter.add(new CardModel(cardInfoVector.elementAt(i).URL,
                                cardInfoVector.elementAt(i).from,
                                cardInfoVector.elementAt(i).date,
                                cardInfoVector.elementAt(i).time,
                                cardInfoVector.elementAt(i).abst,
                                cardInfoVector.elementAt(i).title,
                                cover,
                                cardInfoVector.elementAt(i).series,
                                cardInfoVector.get(i).url,
                                typeFace,typeFace2,
                                cardInfoVector.get(i).LAT,
                                cardInfoVector.get(i).LONG));
                    }
                    mCardContainer.setAdapter(adapter);

                    toolBox.adapter = adapter;
                }catch(Exception ef){Log.d("Not finished charging", ef.toString());}

            }
            if(exception != null){

            }
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
        Double LAT, LONG;


        public CardInfo(String URL,Bitmap url, String speaker, String title, String series,
                        String from, String date, String time, String abst,Double LAT, Double LONG){
            this.URL = URL;
            this.url = url;
            this.speaker = speaker;
            this.title = title;
            this.series = series;
            this. from = from;
            this.date = date;
            this.time = time;
            this.abst = abst;
            this.LAT = LAT;
            this.LONG = LONG;
        }



    }

    public class ConnectivityChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            debugIntent(intent, "grokkingandroid");
        }

        private void debugIntent(Intent intent, String tag) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                        final ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                        final android.net.NetworkInfo wifi = connMgr
                                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        final android.net.NetworkInfo mobile = connMgr
                                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                        if(wifi.getState().equals(NetworkInfo.State.CONNECTED) ||
                                mobile.getState().equals(NetworkInfo.State.CONNECTED)) {
                            isConnected = true;
                            Log.d(tag, "CONNECTED");
                        }else{
                            isConnected = false;
                            Log.d(tag, "DISCONNECTED");
                        }



            }
        }
    }

String ss="";

    private void updateTokenConfirmation() {
        StringBuilder sb = new StringBuilder("");
        for (Object token: completionView.getObjects()) {

            sb.append(token.toString().replaceAll("\\s","+"));
            sb.append("%2C");
        }

        ss=sb.toString();
    }


    @Override
    public void onTokenAdded(Object token) {
        //((TextView)findViewById(R.id.lastEvent)).setText("Added: " + token);
        updateTokenConfirmation();
        //System.out.println();
    }

    @Override
    public void onTokenRemoved(Object token) {
        //((TextView)findViewById(R.id.lastEvent)).setText("Removed: " + token);
        updateTokenConfirmation();
    }

    /*
    @Override
    public void onPause() {
        b = loadBitmapFromView(getView());
        super.onPause();
    }

    Bitmap b;

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
    */



    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {


        public DemoCollectionPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new OnlyCardFragment(toolBox.callToSwitch);
                case 1:

                    return new OnlyCardFragment(toolBox.callToSwitch);
                default:
                    return new OnlyCardFragment(toolBox.callToSwitch);
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
