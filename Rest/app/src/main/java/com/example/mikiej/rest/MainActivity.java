package com.example.mikiej.rest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {



    //variables for holding userName and Password & button objects
    EditText userName;
    EditText password;
    Button ButtonSendUserDetails;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }



    }



    @Override
    protected void onStart() {
        super.onStart();
        //new HttpRequestTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            new HttpRequestTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }



//    private class HttpRequestTask extends AsyncTask<Void, Void, User> {
//        @Override
//        protected User doInBackground(Void... params) {
//            try {
//
//                TextView userNameLabel = (TextView) findViewById(R.id.id_value);
//
//                final String url = "http://192.168.0.19:8080/HomeNetwork/greeting?name=mj&password=mj@123";
//                RestTemplate restTemplate = new RestTemplate();
//                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
////                Greeting greeting = restTemplate.getForObject(url, Greeting.class);
////                return greeting;
//
//                User user = restTemplate.getForObject(url, User.class);
//                return user;
//
//            } catch (Exception e) {
//                Log.e("MainActivity", e.getMessage(), e);
//            }
//
//            return null;
//        }
//protected void onPostExecute(User user) {
//    TextView greetingIdText = (TextView) findViewById(R.id.id_value);
//    TextView greetingContentText = (TextView) findViewById(R.id.content_value);
//    greetingIdText.setText(user.getUserName());
//    greetingContentText.setText(user.getUserRole());
//
//}
//
//}



    private class HttpRequestTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {

            String username = userName.getText().toString();

            String userPassword = password.getText().toString();

            //final String url = "http://192.168.0.19:8080/HomeNetwork/greeting?name=" + username + "&password=" + userPassword;
            try {
                final String url = "http://192.168.0.19:8080/HomeNetwork/greeting?name=" + username + "&password=" + userPassword;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//                Greeting greeting = restTemplate.getForObject(url, Greeting.class);
//                return greeting;

                User user = restTemplate.getForObject(url, User.class);
                return user;

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }



        protected void onPostExecute(User user) {
            TextView greetingIdText = (TextView) findViewById(R.id.id_value);
            TextView greetingContentText = (TextView) findViewById(R.id.content_value);
            greetingIdText.setText(user.getUserName());
            greetingContentText.setText(user.getUserRole());

        }

    }




    public void sendUserDetails(View Button)
    {

        //Get userName from text input
        userName = (EditText) findViewById(R.id.userName);
        //Get password from text input
        password = (EditText) findViewById(R.id.password);
        //create button object
        ButtonSendUserDetails = (Button) findViewById(R.id.ButtonSendUserDetails);
//        String username = userName.getText().toString();
//
//        String userPassword = password.getText().toString();

        //final String url = "http://192.168.0.19:8080/HomeNetwork/greeting?name=" + username + "&password=" + userPassword;
        //final String url = "http://192.168.0.19:8080/HomeNetwork/greeting";
        //check whether the msg empty or not
//        if(username.length()>0 && userPassword.length()>0) {



//                userName.setText(""); //reset the name text field
//                password.setText(""); //reset the password text field
                new HttpRequestTask().execute();

//        } else {
//            //display message if text field is empty
//            Toast.makeText(getBaseContext(),"All fields are required",Toast.LENGTH_SHORT).show();
//            //return null;
//        }
    }


}