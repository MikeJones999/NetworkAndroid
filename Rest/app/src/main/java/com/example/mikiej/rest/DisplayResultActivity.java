package com.example.mikiej.rest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;


public class DisplayResultActivity extends Activity {

    User user = null;
    Uri imageToSend = null;
    String imagePath = "";
    String fileName = "";
    String ipAddress = "";
    String port = "";
    int actualPort = 0;
    TextView access_value = null;
    TextView filecompleted;

    static int IMAGE_SELECTED = 1;

    /**
     * Utilised in the creation of second screen
     * obtains the information from teh previous screen and populates the stated variables
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);
        Intent intent = getIntent();




        //get user object passed
        user = (User) intent.getSerializableExtra("userObject");
        TextView userNameLabel = (TextView) findViewById(R.id.userName_value);

        access_value = (TextView) findViewById(R.id.access_value);
        access_value.setText("GRANTED");
        userNameLabel.setText(user.getUserName());

        ipAddress = (String) intent.getSerializableExtra("IPADDRESS");
        port = (String) intent.getSerializableExtra("PORT");
        actualPort = Integer.parseInt(port);

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

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Used to obtain current view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.activity_display_result, container, false);
            return rootView;
        }
    }

    //assistance concept ref: http://viralpatel.net/blogs/pick-image-from-galary-android-app/ 26/07/2015

    /**
     * This method is used to locate teh file identified in the choosing of file option.
     * Once the file is identified it is returend and displayed on screen.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //set these to null to ensure each time a new photo is selected - nothing remains of the old
        imageToSend = null;
        imagePath = "";


        if (requestCode == IMAGE_SELECTED && resultCode == RESULT_OK && null != data)
        {
            ImageView image = (ImageView) findViewById(R.id.imageViewer);
            imageToSend = data.getData();

            String[] fileLocation = {MediaStore.Images.Media.DATA};

            //search data base for the path name - cursor moves from location to location (SQL)
            Cursor pointer = getContentResolver().query(imageToSend, fileLocation, null, null, null);
            pointer.moveToFirst();

            int index = pointer.getColumnIndex(fileLocation[0]);
            imagePath = pointer.getString(index);
            pointer.close();

            TextView fileChosen = (TextView) findViewById(R.id.fileChosen_value);
            //get filename
            String splitFileLocation[] = imagePath.split("/");
            fileName = splitFileLocation[splitFileLocation.length - 1];
            fileChosen.setText(fileName);

            //this sets the image without resizing
            //image.setImageBitmap(BitmapFactory.decodeFile(imagePath));

            Log.d("imageloaded", "***DEBUG***   image found");

            File file = new File(imagePath);
            Bitmap imageFile = decodeFile(file); //Venky's method
            imageFile = Bitmap.createScaledBitmap(imageFile,450, 450, true);
            image.setImageBitmap(imageFile);
        }
    }


    //REF: http://stackoverflow.com/questions/6410364/how-to-scale-bitmap-to-screen-size
    //Author Venky - accessed 30/07/2015
    /**
     * Method used to reduce the size of an image so that it can be displayed on teh android screen
     * Author Venky - http://stackoverflow.com/questions/6410364/how-to-scale-bitmap-to-screen-size
     * @param File f
     * @return Bitmap
     */
    private Bitmap decodeFile(File f)
    {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true)
            {
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                //scale++;
                scale*=2;
            }
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        }
        catch (FileNotFoundException e)
        {
            Log.d("imageNotloaded", "***DEBUG***   image not resized");
        }
        return null;
    }


    /**
     *
     * @param view
     */
    public void uploadFile(View view)
    {

        Log.d("uploadMethod", "***DEBUG***    upload method called");

        if (imagePath !="")
        {
            Log.d("fileFound", "***DEBUG***    file found");
        }
        else
        {
            Log.d("fileFound", "***DEBUG***    no file found");
            TextView access_value = (TextView) findViewById(R.id.fileChosen_value);
            access_value.setText("No file found or selected - please try again");
        }
    }


    /**
     * Main class that has responsibility of uploading the file to the server
     */
    private class uploadImageFiler extends AsyncTask<String, String, String>
    {

           @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            return beginUpload();
        }

        /**
         * Method that carries out the uploading of the file to the server
         * @return String version of boolean
         */
        private String beginUpload()
        {

            Log.d("part1", "***DEBUG***    part1 method called");
            final String url = "http://" + ipAddress + ":" + actualPort + "/HomeNetwork/restfulGateway/" + user.getUserName() + "/upload/1";

            RestTemplate restTemplate = new RestTemplate();
            FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            formHttpMessageConverter.setCharset(Charset.forName("UTF8"));

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(formHttpMessageConverter);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());


            Log.d("part2", "***DEBUG***    part2 method called");
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

            //verify
            parts.add("field 1", "value 1");


            parts.add("file", new FileSystemResource(imagePath));

            HttpHeaders imageHeaders = new HttpHeaders();
            imageHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> imageEntity = new HttpEntity<>(parts, imageHeaders);
            Log.d("part3", "***DEBUG***    part3 method called");
            Object result = restTemplate.exchange(url, HttpMethod.POST, imageEntity, Boolean.class);

            String uploaded = result.toString();

            return uploaded;

        }


        @Override
        protected void onPostExecute(String result) {

            filecompleted =  (TextView) findViewById(R.id.fileuploadcomplete);
            if (result.equals("false"))
            {
                Log.d ("Uploaded", "***DEBUG*** did not upload");

                filecompleted.setText("File was not uploaded successfully - please try again. Ensure Server is on");
            }
            else
            {
                Log.d ("Uploaded", "***DEBUG*** upload correctly");

                filecompleted.setText("File was uploaded successfully");
            }

        }

    }



}
