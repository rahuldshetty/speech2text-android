package com.rahuldshetty.speech2text;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView,output,refresh;
    private Text2Speech tts;
    ClientInternet internet;

    private Camera camera;
    private CameraPreview preview;
    private FrameLayout frameLayout;

    final static String TAG = "ERROR";

    private String requestString,responseString;

    private Bitmap photo=null;
    private byte[] img_stream;

    String postUrl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internet = new ClientInternet(getApplicationContext(),this);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.micBtn);
        tts = new Text2Speech(getApplicationContext());
        frameLayout = findViewById(R.id.imageView);
        output = findViewById(R.id.outputView);
        refresh = findViewById(R.id.refreshBtn);


        postUrl =  internet.getPostUrl();

        getCamera();

        checkPermission();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    getCamera();
                    camera.takePicture(null,null,mPicture);
                }
                catch (Exception e)
                {
                    getCamera();
                    camera.startPreview();
                    tts.speak("Try again");
                }
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    refresh_status();
                }
                catch (Exception e){
                    tts.speak("Try again");
                }
            }
        });

    }

    void refresh_status(){
        getCamera();
        camera.startPreview();

        final EditText edittext = new EditText(this);
        edittext.setText("192.168.1.103");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage("Enter IP Address:");
        alert.setTitle("Update IPv4 Address");

        alert.setView(edittext);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = edittext.getText().toString();
                postUrl = ClientInternet.getPostUrl(value);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();


    }

    void getCamera(){
        if(camera == null)
        {
            camera = CameraCam.getCameraInstance();
            preview = new CameraPreview(this,camera);
            frameLayout.addView(preview);
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            photo = BitmapFactory.decodeByteArray(data,0,data.length);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG,50,stream);

            byte[] byteArray = stream.toByteArray();

            photo = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            photo = Utils.rotateImage(photo,90);

            Bitmap bmp = photo;
            ByteArrayOutputStream new_stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            img_stream = stream.toByteArray();

            output.setImageBitmap(photo);

            startVoiceInput();

        }
    };

    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }



    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) || !(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) ) {
                //request persmission
                requestPermissions(new String[]{ Manifest.permission.RECORD_AUDIO , Manifest.permission.CAMERA}, 101);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case 100: {
                    if (resultCode == RESULT_OK && null != data) {
                        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        requestString = result.get(0);
                        textView.setText("Request: " + result.get(0));
                        getCamera();
                        camera.startPreview();

                        // start the uploading task

                        RequestBody postBodyImage = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("image", "android.jpg", RequestBody.create(MediaType.parse("image/*jpg"), img_stream))
                                .build();

                        postRequest(postBodyImage);

                    }
                    break;
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    void startVoiceInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        tts.speak("Listening");

        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {

        }
    }



    void postRequest(RequestBody postBody) {
        System.out.println(postUrl);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                System.out.println("FAILED"+ e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            responseString = response.body().string();
                            textView.setText("Request: " + requestString + "\nResponse: "+responseString);
                            tts.speak(responseString);

                        } catch (IOException e) {
                            e.printStackTrace();
                            tts.speak("Failed to contact the server");
                        }
                    }
                });
            }
        });
    }


}
