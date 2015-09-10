package com.example.mikiej.rest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {



    //variables for holding userName and Password & button objects
    EditText userName;
    EditText password;
    Button ButtonSendUserDetails;
    EditText ipAddress;
    EditText portAddress;


    public final static String USERNAME = "com.example.mikiej.rest.MESSAGE";


    /**
     * Initial method called to instantiate the application
     * @param savedInstanceState
     */
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

//            new HttpRequestTask().execute();
//            return true;
        }
        return super.onOptionsItemSelected(item);
    }


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




//ref: assistance from https://github.com/spring-projects/spring-android-samples/blob/master/spring-android-basic-auth/client/src/org/springframework/android/basicauth/MainActivity.java 28/07/2015

    /**
     * Main Class used to communicate with the web server
     */
    private class HttpRequestTask extends AsyncTask<Void, Void, User> {

        /**
         * Sets up the variables by obtaining them from the view
         * Connects to the web server via RestTemplate object
         * @param params
         * @return User
         */
        @Override
        protected User doInBackground(Void... params) {

            String username = userName.getText().toString();

            String userPassword = password.getText().toString();

            String ipToSearch = ipAddress.getText().toString();

            String portToSearch = portAddress.getText().toString();

            int port =  Integer.parseInt(portToSearch);

            if(isUrlReachable(ipToSearch, port)) {



                try {


                    final String serverURL = "http://" + ipToSearch + ":" + portToSearch + "/HomeNetwork/restfulGateway/login";

                    //use HttpAuth to send the username and password and have spring authenticate it rather than doing it manually
                    HttpHeaders headers = new HttpHeaders();
                    HttpAuthentication auth = new HttpBasicAuthentication(username, userPassword);
                    headers.setAuthorization(auth);
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

                    // Create a new RestTemplate instance
                    RestTemplate restTemplate = new RestTemplate();
                    //add JSON to the template for transferring of data
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    ResponseEntity<User> user = restTemplate.exchange(serverURL, HttpMethod.GET, new HttpEntity<>(headers), User.class);
                    //User user = restTemplate.getForObject(url, User.class);
                    return user.getBody();

                }
                catch (Exception e)
                {

                    Log.e("MainActivity", e.getMessage(), e);
                }

                return null;

            }
            else
            {
                //poor means of setting the view
                User temp = new User();
                temp.setUserName("ipDenided");
                return temp;
            }
        }

        /**
         * Establishes if the ip and port address provided in the view actually exist
         * @param ipAdd
         * @param port
         * @return
         */
    public boolean isUrlReachable(String ipAdd, int port)
    {
        boolean result = false;
        try
        {
            int timer = 20;
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAdd, port), timer);
            socket.close();
            result = true;
            return result;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return result;
        }
    }


        /**
         * On completion of the restfull communication with the server this method handles the view
         * by displaying teh returned user details
         * @param user
         */
        protected void onPostExecute(User user)
        {

            TextView access = (TextView) findViewById(R.id.access_value);
            TextView userN = (TextView) findViewById(R.id.userName);
            TextView userP = (TextView) findViewById(R.id.password);
            if (user != null)
            {
                //user the user name to identify if ip address is open or not - poor way of doing it - need new method
                if(!user.getUserName().equals("ipDenided"))
                {
                    String name = userName.getText().toString();
                    String role = user.getUserRole();


                    Intent intent = new Intent(MainActivity.this, DisplayResultActivity.class);
                    intent.putExtra(USERNAME, userName.getText().toString());

                    intent.putExtra("userObject", user);

                    intent.putExtra("IPADDRESS", ipAddress.getText().toString());
                    intent.putExtra("PORT", portAddress.getText().toString());

                    startActivity(intent);
                }
                else
                {
                    access.setText("DENIED - Ip address and/or Port not responding");
                }

            }
            else
            {
                access.setText("DENIED - Password and/or Username incorrect");
                userN.setText("");
                userP.setText("");
            }



        }

    }


    /**
     * This method responds to the button submit being pressed in teh initial page
     * It then creates an object of HttpRequestTask() and executes it - this start the process for communication to the web server
     * @param Button
     */
    public void sendUserDetails(View Button)
    {

        //Get userName from text input
        userName = (EditText) findViewById(R.id.userName);
        //Get password from text input
        password = (EditText) findViewById(R.id.password);
        //create button object
        ButtonSendUserDetails = (Button) findViewById(R.id.ButtonSendUserDetails);


        ipAddress = (EditText) findViewById(R.id.ipaddress);
        portAddress = (EditText) findViewById(R.id.portaddress);


                new HttpRequestTask().execute();

    }

    /**
     * Responds to the chose file button being pressed
     * @param view
     */
    public void chooseFileButton(View view)
    {

        //must be passed a view
        Intent intent = new Intent(this, DisplayResultActivity.class);
        intent.putExtra(USERNAME, userName.getText().toString());

        startActivity(intent);


    }






}