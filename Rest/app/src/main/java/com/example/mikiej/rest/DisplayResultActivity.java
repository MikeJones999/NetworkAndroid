package com.example.mikiej.rest;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;


public class DisplayResultActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);
        Intent intent = getIntent();

        String userName = intent.getStringExtra(MainActivity.USERNAME);



//        Bundle passedUserBundle = new Bundle();
//
//        passedUserBundle = getIntent().getExtras();
//        HashMap<String, Object> tempHM = new HashMap<String, Object>();
//        tempHM = (HashMap<String, Object>)passedUserBundle.getSerializable("returnedUser");
//        User userFound = (User) tempHM.get("hmHoldingUser");


        TextView userNameLabel = (TextView) findViewById(R.id.userName_value);


        TextView access_value = (TextView) findViewById(R.id.access_value);

        access_value.setText("GRANTED");
        userNameLabel.setText(userName);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_display_result,
                    container, false);
            return rootView;
        }
    }

}
