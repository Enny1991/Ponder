package com.eneaceolini.ponder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private UiLifecycleHelper uiHelper;
    private View otherView;
    private int ACCESS_INTERNAL = 0;
    private int ACCESS_FACEBOOK = 1;
    private int ACCESS_GOOGLE = 2;
    private int accessType ;
    private String globalServerKey;
    private String globalServerPass = "";
    private Dialog dialog;

    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "MainActivity";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    /**
     * A flag indicating that a PendingIntent is in p
     * and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;
    ImageView img2;

    private TextView textViewFirstName,textViewLastName,textViewAge,textViewPoints;
    private Button buttonGetData,nextPage;
    private LoginButton authFB;
    private Exception exception;
    private EditText editTextSearchString;
    private String strURL;
    private Typeface typeFace;
    public ToolBox toolBox;
    public Button fblogin;
    private ProgressBar logginIn;
    private LinearLayout loginLayout;
    private LinearLayout loading;
    Facebook fb;
    String APP_ID;
    Context context ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        //ToolBox
        if(toolBox!=null) toolBox=null;
        else toolBox=ToolBox.getInstance();
        toolBox.isFirst = true;
        toolBox.adapter = null;

        //General Grapichs
        typeFace= Typeface.createFromAsset(getAssets(), "fonts/YanoneKaffeesatz-Regular.ttf");
        ((TextView)findViewById(R.id.ponder)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.sign)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.textView13)).setTypeface(typeFace);
        ((EditText)findViewById(R.id.email)).setTypeface(typeFace);
        ((EditText)findViewById(R.id.pass)).setTypeface(typeFace);
        authFB = (LoginButton) findViewById(R.id.authButton);
        authFB.setBackgroundColor(getResources().getColor(R.color.fb_blue));
        //authFB.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        //authFB.setText("");
        logginIn = (ProgressBar)findViewById(R.id.progressBar2);
        logginIn.setIndeterminate(true);
        logginIn.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0x2A86C0));

        loginLayout = (LinearLayout)findViewById(R.id.loginLayout);
        loading = (LinearLayout)findViewById(R.id.loading);
        //FB



        otherView = (View) findViewById(R.id.other_views);
        otherView.setVisibility(View.GONE);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        //G+
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        //btnSignIn.setSize(SignInButton.SIZE_ICON_ONLY);
        //btnSignIn.setBackgroundResource(R.drawable.gplus);
        setGooglePlusButtonText(btnSignIn,"Sign in with Google +");
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

        // Button click listeners
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);
        nextPage = (Button) findViewById(R.id.nextPage);
        nextPage.setTypeface(typeFace);
        //data from server
        buttonGetData = (Button) findViewById(R.id.getData);
        editTextSearchString = (EditText) findViewById(R.id.editText);
        textViewFirstName = (TextView) findViewById(R.id.firstName);
        textViewLastName = (TextView) findViewById(R.id.lastName);
        textViewAge = (TextView) findViewById(R.id.age);
        textViewPoints = (TextView) findViewById(R.id.points);
        img2 = (ImageView) findViewById(R.id.profile_image);

        buttonGetData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the data
                DoPOST mDoPOST = new DoPOST(MainActivity.this, editTextSearchString.getText().toString());
                mDoPOST.execute("");
                buttonGetData.setEnabled(false);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        //build dialog
        dialog = new Dialog(MainActivity.this,R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.for_dialog_text);
        TextView a = (TextView)dialog.findViewById(R.id.message);
        a.setTypeface(typeFace);

        Button one =(Button)dialog.findViewById(R.id.button3);
        one.setTypeface(typeFace);
        one.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //checkDatabase
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor;
        String query = "SELECT * FROM personal ORDER BY _id ASC";
        cursor = db.rawQuery(query,null);
        //Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.img1);
        //bm = getResizedBitmap(bm,maxHeight,maxWidth);
        if(cursor.moveToNext()){
            globalServerKey = cursor.getString(1);
            globalServerPass = cursor.getString(2);
            new RetrieveKeyFromServer().execute("login");
            loading.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.INVISIBLE);
        }
    }


    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };





    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView mTextView = (TextView) v;
                mTextView.setText(buttonText);
                return;
            }
        }
    }

    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {

        // When Session is successfully opened (User logged-in)
        if (state.isOpened()) {
            loading.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.INVISIBLE);
            new RetrieveKeyFromServer().execute(new String[]{"facebook",session.getAccessToken()});
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                // callback after Graph API response with user
                // object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Set view visibility to true
                        //otherView.setVisibility(View.VISIBLE);
                        //Set Image
                        try {

                            int SDK_INT = android.os.Build.VERSION.SDK_INT;
                            if (SDK_INT > 8)
                            {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                        .permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                URL imgUrl = new URL("https://graph.facebook.com/"
                                        + user.getId() + "/picture?type=large");
                                InputStream in = (InputStream) imgUrl.getContent();
                                Bitmap bitmap = BitmapFactory.decodeStream(in);
                                toolBox.myPhoto = bitmap;
                                //img.setImageBitmap(bitmap);

                            }
                        }catch(IOException exc){}
                    }
                }
            }).executeAsync();

        } else if (state.isClosed()) {
            Log.d(TAG, "Logged out...");
            //otherView.setVisibility(View.GONE);
        }
    }

    private Boolean succLogin = true;

    class RetrieveKeyFromServer extends AsyncTask<String, Void, Boolean> {

        private Exception exception;
        private String[] myKeys;

        protected Boolean doInBackground(String... keys) {
            JSONObject jSonResult = null;
                myKeys = keys;
                String result = null;
                InputStream inputStream = null;
                // retrieve form server info FB
                HttpParams httpParameters = new BasicHttpParams();

                //Setup timeouts
                HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);

                HttpClient httpclient = new DefaultHttpClient(httpParameters);

                HttpPost httpPost = new HttpPost("https://www.pondertalks.com/rest-auth/"+keys[0]+"/");
                //httpPost.setHeader("Authorization","Token "+session.getAccessToken());


                try {
                    List<NameValuePair> nameValuePairs;
                    switch(keys.length){
                        case 2:
                            nameValuePairs = new ArrayList<>(1);
                            nameValuePairs.add(new BasicNameValuePair("access_token", keys[1]));
                            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                            break;
                        case 1:
                            nameValuePairs = new ArrayList<>(2);
                            nameValuePairs.add(new BasicNameValuePair("username", globalServerKey));
                            nameValuePairs.add(new BasicNameValuePair("password", globalServerPass));
                            //save to database

                            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                            break;
                    }
                    //httpclient.execute(httpPost);

                }catch (Exception e){
                    Log.e("Error+",e.toString());
                    succLogin = false;
                }
                try {
                    HttpResponse response = httpclient.execute(httpPost);
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
                    Log.e("Post Error",e.toString());
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }

                try {
                    jSonResult = new JSONObject(result);
                    Log.d("RESULT",jSonResult.getString("key").toString());
                    switch(keys.length){
                        case 2:
                            if(keys[0].equals("facebook"))nextPage(ACCESS_FACEBOOK,jSonResult.get("key").toString());
                            if(keys[0].equals("google"))nextPage(ACCESS_GOOGLE,jSonResult.get("key").toString());
                            break;
                        case 1:
                            nextPage(ACCESS_INTERNAL,jSonResult.get("key").toString());
                            break;
                    }


                }catch(Exception e){
                    Log.e("BAD JSon", e.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.INVISIBLE);
                            loginLayout.setVisibility(View.VISIBLE);
                            dialog.show();

                        }
                    });

                    return false;
                }
            return true;
        }

        protected void onPostExecute(String something) {

        }
    }



    class GetTokenFromGoogle extends AsyncTask<String, Void, Boolean> {

        private Exception exception;

        protected Boolean doInBackground(String... keys) {
            try {
                String accessToken = GoogleAuthUtil.getToken(MainActivity.this, globalGoogleEmail, "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/plus.profile.emails.read");
                Log.d("Google Token",accessToken);
                new RetrieveKeyFromServer().execute(new String[]{"google",accessToken});
            } catch (IOException transientEx) {
                // Network or server error, try later
                //Log.w(LOG_TAG, transientEx.toString());
                //onCompletedLoginAttempt(false);
            } catch (GooglePlayServicesAvailabilityException e) {
                //Log.w(LOG_TAG, "Google Play services not available.");
                Intent recover = e.getIntent();
                startActivityForResult(recover, 2);
            } catch (UserRecoverableAuthException e) {
                // Recover (with e.getIntent())
                //Log.w(LOG_TAG, "User must approve " + e.toString());
                Intent recover = e.getIntent();
                startActivityForResult(recover, 1);
            } catch (GoogleAuthException authEx) {
                Log.e("GoogleAcces", authEx.toString());
                // The call is not ever expected to succeed
                //Log.w(LOG_TAG, authEx.toString());
                //onCompletedLoginAttempt(false);
            }

            return true;
        }

        protected void onPostExecute(String something) {

        }
    }


    protected void onStart() {
        super.onStart();
        Log.d("Method", "onStart");
        if(!toolBox.wantsToLogOut) {
            mGoogleApiClient.connect();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(toolBox.wantsToLogOut) {
            loading.setVisibility(View.INVISIBLE);
            loginLayout.setVisibility(View.VISIBLE);
            signOutFromGplus();
            Log.d("Signin","OUT");

        }
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Method", "onPause");
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == 1 && responseCode == RESULT_OK) {
            Bundle extra = intent.getExtras();
            String oneTimeToken = extra.getString("authtoken");
            Log.d("RECOVERED TOKEN",oneTimeToken);
            new RetrieveKeyFromServer().execute(new String[]{oneTimeToken,"google"});

        }
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

        uiHelper.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        loading.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.INVISIBLE);
        //Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();
        // Update the UI after signin
        //updateUI(true);

    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     * */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            //btnRevokeAccess.setVisibility(View.VISIBLE);
            //llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            //llProfileLayout.setVisibility(View.GONE);
        }
    }

    String globalGoogleEmail;
    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {


        try {


            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);

                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                globalGoogleEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + globalGoogleEmail
                        + ", Image: " + personPhotoUrl);

                //txtName.setText(personName);
                //txtEmail.setText(globalGoogleEmail);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage().execute(personPhotoUrl);

                new GetTokenFromGoogle().execute("");

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        //updateUI(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                // Signin button clicked
                signInWithGplus();
                break;
            case R.id.btn_sign_out:
                // Signout button clicked
                signOutFromGplus();
                break;
            case R.id.btn_revoke_access:
                // Revoke access button clicked
                revokeGplusAccess();
                break;
        }
    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            loading.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.INVISIBLE);
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {

            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            //mGoogleApiClient.connect();
            //updateUI(false);
        }
        Log.d("Google","Sign out");
    }

    /**
     * Revoking access from google
     * */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                            //updateUI(false);
                        }

                    });
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {


        public LoadProfileImage() {

        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                toolBox.myPhoto = mIcon11;
                Log.d("LOADING...","pic");
                if(toolBox.myPhoto == null) Log.d("PHOTO","null");
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

        }
    }

    public class DoPOST extends AsyncTask<String, Void, Boolean>{

        Context mContext = null;
        String strNameToSearch = "";

        //Result data
        String strFirstName;
        String strLastName;
        int intAge;
        int intPoints;

        Exception exception = null;

        DoPOST(Context context, String nameToSearch){
            mContext = context;
            strNameToSearch = nameToSearch;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try{

                //Setup the parameters
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("NameToSearch", strNameToSearch));
                //Add more parameters as necessary

                //Create the HTTP request
                HttpParams httpParameters = new BasicHttpParams();

                //Setup timeouts
                HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);

                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost("http://172.19.12.186:8888/clientservertest/getimg.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                String result = EntityUtils.toString(entity);

                // Create a JSON object from the request response
                JSONObject jsonObject = new JSONObject(result);

                //Retrieve the data from the JSON object
                strFirstName = jsonObject.getString("name");
                strURL = jsonObject.getString("URL");
                //strLastName = jsonObject.getString("LastName");
                //intAge = jsonObject.getInt("Age");
                //intPoints = jsonObject.getInt("Points");

            }catch (Exception e){
                Log.e("ClientServerDemo", "Error:", e);
                exception = e;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean valid){
            //Update the UI
            //textViewFirstName.setText("First Name: " + strFirstName);
            //textViewLastName.setText("Last Name: " + strURL);
            //textViewAge.setText("Age: " + intAge);
            //textViewPoints.setText("Points: " + intPoints);

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    URL imgUrl = new URL(strURL);
                    InputStream in = (InputStream) imgUrl.getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    img2.setImageBitmap(bitmap);
                }catch(Exception ef){Log.d("error", ef.toString());}

            }

            buttonGetData.setEnabled(true);

            if(exception != null){
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    public void nextPage(int type,String key){

        Intent enter = new Intent(this, FragmentHolderTest.class);
        enter.putExtra("type",type);
        enter.putExtra("key",key);
        if(succLogin && globalServerKey != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TableNotes.COLUMN_EMAIL, globalServerKey);
            values.put(TableNotes.COLUMN_PASSWORD, globalServerPass);

            long id = db.insertOrThrow(TableNotes.TABLE_NAME, null, values);
            db.close();
        }

        startActivity(enter);
        overridePendingTransition(R.anim.transition1, R.anim.transition2);
    }

    public void nextPage(View v){

        globalServerKey = ((TextView)findViewById(R.id.email)).getText().toString();
        globalServerPass = ((TextView)findViewById(R.id.pass)).getText().toString();
        new RetrieveKeyFromServer().execute("login");
        loading.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.INVISIBLE);

    }


}
