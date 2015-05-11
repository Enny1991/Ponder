package com.eneaceolini.ponder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class SettActivityFragment extends Fragment {

    public SettActivityFragment(){}
    TextView text;
    EditText edit,a;
    private Typeface typeFace;
    private Typeface typeFace2;
    String key, email, location, language, unsubscribed, notify_terms;
    TextView emailT, locationT, languageT, unsubscribedT, notify_termsT,
            emailS, locationS, languageS, unsubscribedS, notify_termsS;
    ImageView myPhoto;
    ToolBox toolBox;
    private Dialog dialog,dialog2;
    Button emailB, locationB, languageB, unsubscribedB, notify_termsB;
    final private String EMAIL = "email";
    final private String LANGUAGE = "language";
    final private String LOCATION = "location";
    final private String NOTIFY = "notify_terms";
    private String GLOBAL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_bohact, container, false);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Cookie-Regular.ttf");
        typeFace2 = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/YanoneKaffeesatz-Regular.ttf");

        toolBox = ToolBox.getInstance();
        myPhoto = (ImageView)rootView.findViewById(R.id.my_photo);
        if(toolBox.myPhoto == null) Log.d("PHOTO","null");
        myPhoto.setImageBitmap(toolBox.myPhoto);

        emailS = (TextView)rootView.findViewById(R.id.my_email_solid);
        languageS = (TextView)rootView.findViewById(R.id.my_language_solid);
        locationS = (TextView)rootView.findViewById(R.id.my_location_solid);
        notify_termsS = (TextView)rootView.findViewById(R.id.my_notify_solid);



        emailB = (Button)rootView.findViewById(R.id.my_email_but);
        languageB = (Button)rootView.findViewById(R.id.my_language_but);
        locationB = (Button)rootView.findViewById(R.id.my_location_but);
        notify_termsB = (Button)rootView.findViewById(R.id.my_notify_but);

        emailB.setOnClickListener(new ClickToModify(EMAIL));
        languageB.setOnClickListener(new ClickToModify(LANGUAGE));
        locationB.setOnClickListener(new ClickToModify(LOCATION));
        notify_termsB.setOnClickListener(new ClickToModify(NOTIFY));

        emailT = (TextView)rootView.findViewById(R.id.my_email);
        languageT = (TextView)rootView.findViewById(R.id.my_language);
        locationT = (TextView)rootView.findViewById(R.id.my_location);
        notify_termsT = (TextView)rootView.findViewById(R.id.my_notify);

        emailT.setTypeface(typeFace2);
        languageT.setTypeface(typeFace2);
        locationT.setTypeface(typeFace2);
        notify_termsT.setTypeface(typeFace2);

        emailS.setTypeface(typeFace);
        languageS.setTypeface(typeFace);
        locationS.setTypeface(typeFace);
        notify_termsS.setTypeface(typeFace);

        //build dialog
        dialog = new Dialog(getActivity(), R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change);
        a = (EditText)dialog.findViewById(R.id.editText2);
        a.setTypeface(typeFace);

        Button one =(Button)dialog.findViewById(R.id.back);
        one.setTypeface(typeFace);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button save =(Button)dialog.findViewById(R.id.save);
        save.setTypeface(typeFace);
        save.setOnClickListener(new ClickToModify2(GLOBAL));




        new LoadPref().execute(""); //TODO do not load here but before.

        return rootView;
    }


    class ClickToModify implements View.OnClickListener{

        private String indexToModify;

        public ClickToModify(String type){
            indexToModify = type;
        }
        @Override
        public void onClick(View v){
            dialog.show();
            GLOBAL = indexToModify;
        }
    }


    class ClickToModify2 implements View.OnClickListener{

        private String indexToModify;

        public ClickToModify2(String type){
            indexToModify = type;
        }
        @Override
        public void onClick(View v){
            new ChangePref(indexToModify).execute("");
            dialog.dismiss();
        }
    }




    public class LoadPref extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {
            String result = null;
            try {

                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httpget = new HttpGet("http://www.pondertalks.com/account/rest/");
                httpget.setHeader("Authorization", "Token " + key);
                InputStream inputStream = null;

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
                //Log.d("RESULT MY TALKS", "" + result);

            }catch (Exception e){
                Log.e("error in settings",e.toString());
            }

            try {

                JSONObject jsonObject = new JSONObject(result);
                Log.d("result",""+result);
                email = jsonObject.getString("email");
                location = jsonObject.getString("location");
                language = jsonObject.getString("language");
                unsubscribed = jsonObject.getString("unsubscribed");
                notify_terms = jsonObject.getString("notify_terms");


            }catch(Exception e){}

            return true;
        }





        @Override
        protected void onPostExecute(Boolean valid) { //POPULATE
            emailT.setText(email);
            locationT.setText(location);
            languageT.setText(language);
            notify_termsT.setText(notify_terms);
        }
    }

    public class ChangePref extends AsyncTask<String, Void, Boolean> {

        String toModify;

        public ChangePref(String modify){
            toModify = modify;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            String result = null;
            try {

                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPut httpput = new HttpPut("http://www.pondertalks.com/account/rest/");
                httpput.setHeader("Authorization", "Token " + key);
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("email", emailT.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair(GLOBAL, a.getText().toString()));
                httpput.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                InputStream inputStream = null;

                try {
                    HttpResponse response = httpclient.execute(httpput);
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
                //Log.d("RESULT MY TALKS", "" + result);

            }catch (Exception e){
                Log.e("error in settings", e.toString());
                //build dialog

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog2 = new Dialog(getActivity(),R.style.PauseDialog);
                        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog2.setContentView(R.layout.for_dialog_text);
                        TextView a = (TextView)dialog2.findViewById(R.id.message);
                        a.setTypeface(typeFace);

                        Button one =(Button)dialog2.findViewById(R.id.button3);
                        one.setTypeface(typeFace);
                        one.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                            }
                        });
                        dialog2.show();
                    }
                });
            }

            try {

                JSONObject jsonObject = new JSONObject(result);
                Log.d("result",""+result);
                email = jsonObject.getString("email");
                location = jsonObject.getString("location");
                language = jsonObject.getString("language");
                unsubscribed = jsonObject.getString("unsubscribed");
                notify_terms = jsonObject.getString("notify_terms");


            }catch(Exception e){}

            return true;
        }





        @Override
        protected void onPostExecute(Boolean valid) { //POPULATE
            emailT.setText(email);
            locationT.setText(location);
            languageT.setText(language);
            notify_termsT.setText(notify_terms);
            Toast.makeText(getActivity(), "Changed succefully!",Toast.LENGTH_SHORT).show();
        }
    }


    public void g(){
        text.setText(edit.getText());
    }


}
