package com.eneaceolini.ponder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    EditText edit;
    String key, email, location, language, unsubscribed, notify_terms;
    TextView emailT, locationT, languageT, unsubscribedT, notify_termsT;
    ImageView myPhoto;
    ToolBox toolBox;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_bohact, container, false);
        toolBox = ToolBox.getInstance();
        myPhoto = (ImageView)rootView.findViewById(R.id.my_photo);
        if(toolBox.myPhoto == null) Log.d("PHOTO","null");
        myPhoto.setImageBitmap(toolBox.myPhoto);
        emailT = (TextView)rootView.findViewById(R.id.my_email);
        languageT = (TextView)rootView.findViewById(R.id.my_language);
        locationT = (TextView)rootView.findViewById(R.id.my_location);
        notify_termsT = (TextView)rootView.findViewById(R.id.my_notify);
        new LoadPref().execute(""); //TODO do not load here but before.

        return rootView;
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

        @Override
        protected Boolean doInBackground(String... arg0) {
            String result = null;
            try {

                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPut httpput = new HttpPut("http://www.pondertalks.com/account/rest/");
                httpput.setHeader("Authorization", "Token " + key);
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("email", emailT.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("language", emailT.getText().toString()));
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


    public void g(){
        text.setText(edit.getText());
    }


}
