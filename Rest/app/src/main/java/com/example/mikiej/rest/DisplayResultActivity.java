package com.example.mikiej.rest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.charset.Charset;


public class DisplayResultActivity extends Activity {

    User user = null;
    Uri imageToSend = null;
    String imagePath = "";
    String fileName = "";
    String filePath ="";

    static int IMAGE_SELECTED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);
        Intent intent = getIntent();

        //String userName = intent.getStringExtra(MainActivity.USERNAME);


        //get user object passed
        user = (User) intent.getSerializableExtra("userObject");

        TextView userNameLabel = (TextView) findViewById(R.id.userName_value);


        TextView access_value = (TextView) findViewById(R.id.access_value);

        access_value.setText("GRANTED");
        userNameLabel.setText(user.getUserName());

        Button findImage = (Button) findViewById(R.id.ButtonChooseFolder);
        findImage.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0) {

                Intent image = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(image, IMAGE_SELECTED);
            }
        });
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_display_result,
                    container, false);
            return rootView;
        }
    }

    //ref: http://viralpatel.net/blogs/pick-image-from-galary-android-app/ 26/07/2015
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_SELECTED && resultCode == RESULT_OK && null != data)
        {
            ImageView image = (ImageView) findViewById(R.id.imageViewer);
            imageToSend = data.getData();

            String[] fileLocation = {MediaStore.Images.Media.DATA};

            //search data base for the path name - cursor moves from location to location (SQL)
            Cursor cursor = getContentResolver().query(imageToSend, fileLocation, null, null, null);
            cursor.moveToFirst();

            int index = cursor.getColumnIndex(fileLocation[0]);
            imagePath = cursor.getString(index);
            cursor.close();

            TextView userNameLabel = (TextView) findViewById(R.id.userName_value);
            userNameLabel.setText("****DEBUG***** path: " + imagePath);

            image.setImageBitmap(BitmapFactory.decodeFile(imagePath));

            Log.d("imageloaded", "***DEBUG***   image found");






        }
    }


    public void uploadFile(View view)
    {
        //imagePath = "storage/extSdCard/DCIM/Camera/20141129_140410.jpg";
        imagePath = "/storage/extSdCard/DCIM/Camera/20150725_164220.jpg";

        Log.d("uploadMethod", "***DEBUG***    upload method called");

        if (imagePath !="")
        {
            Log.d("fileFound", "***DEBUG***    file found");
            new uploadImageFiler().execute();
        }
        else
        {
            Log.d("fileFound", "***DEBUG***    no file found");

            TextView access_value = (TextView) findViewById(R.id.access_value);

            access_value.setText("No file found");
        }
    }


    private class uploadImageFiler extends AsyncTask<Void, Void, String>
    {


        @Override
        protected void onPreExecute()
        {
            // setting progress bar to zero
            //progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            return beginUpload();
        }

        private String beginUpload()
        {


            Log.d("part1", "***DEBUG***    part1 method called");
            final String url = "http://" + "192.168.0.19" + ":" + "8080" + "/HomeNetwork/restfulGateway/mj/upload/1";
            RestTemplate restTemplate = new RestTemplate();
            FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            formHttpMessageConverter.setCharset(Charset.forName("UTF8"));

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
           restTemplate.getMessageConverters().add(formHttpMessageConverter);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());


            Log.d("part2", "***DEBUG***    part2 method called");
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
            parts.add("field 1", "value 1");

            //get filename
//            String splitFileLocation[] = imagePath.split("/");
//            fileName = splitFileLocation[splitFileLocation.length - 1];

            parts.add("file", new FileSystemResource(imagePath));

            HttpHeaders imageHeaders = new HttpHeaders();
            imageHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> imageEntity = new HttpEntity<>(parts, imageHeaders);
            Log.d("part3", "***DEBUG***    part3 method called");
//            restTemplate.postForLocation(url, parts);
            restTemplate.exchange(url, HttpMethod.POST, imageEntity, Boolean.class);
            return "complete";



        }

    }



}
