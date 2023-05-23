package com.example.cameragallerypermission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity2 extends AppCompatActivity implements ActivityToActivity {
    ImageView imageView, imgProfilePicture2;
    Button btnTakePhoto, btnTakeGallery, btnBack;
    File finalFile;
    Uri tempUri;
    Bitmap bitmap;
    Intent intent5;
    ByteArrayOutputStream bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView = findViewById(R.id.imgProfilePicture);
        imgProfilePicture2 = findViewById(R.id.imgProfilePicture2);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnTakeGallery = findViewById(R.id.btnTakeGallery);
        btnTakePhoto.setOnClickListener(view -> {
            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityIntent.launch(intent1);
        });
        btnTakeGallery.setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // this will open the whole new gallery activity
            startActivityIntentGallery.launch(galleryIntent);
        });
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(view -> {

            intent5 = new Intent(MainActivity2.this, MainActivity.class);
            intent5.putExtra("path", finalFile.getAbsolutePath());
            intent5.putExtra("byteArray", bytes.toByteArray());
            intent5.putExtra("bitmapImage", bitmap);


            startActivity(intent5);

        });
        //Package persistent Data folder mein folder create karan vaaste
//        File file = new File("/data/data/"+this.getPackageName()
//                +"/shared_prefs/"+this.getPackageName()+"ok.xml");
//        if(file.exists())
//            Log.e("SSS", "exist");
//        else{
//            file.mkdirs();
//            Log.e("SSS", "not exist");
//        }
//       File file3 = new File(getApplicationInfo().dataDir,"Creation_2"+"/inside_Creation");
//        Log.e("SSS", "path3 is : "+getApplicationInfo().dataDir );
//        if(file3.exists()){
//            Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            Log.e("SSS", "path3 is : "+Environment.getExternalStorageDirectory().getAbsolutePath() );
//            file3.mkdirs();
//        }
//
//        // inside mobile folders ke liye
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "VIRAL_MAIN5");
//        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "VIRAL_MAIN_DCIM");
//        Log.e("SSS", "path is : "+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() );
//        if(file.exists()){
//            Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            Log.e("SSS", "path1 is : "+Environment.getExternalStorageDirectory().getAbsolutePath() );
//            file.mkdirs();
//            file2.mkdirs();
//        }
//
//        if(file2.exists()){
//            Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            Log.e("SSS", "path2 is : "+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() );
//            file2.mkdirs();
//        }


    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        assert result.getData() != null;
                        Bundle bundle = result.getData().getExtras();

                        bitmap = (Bitmap) bundle.get("data");
                        imageView.setImageBitmap(bitmap);

                        tempUri = getImageUri(getApplicationContext(), bitmap);
                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        finalFile = new File(getRealPathFromURI(tempUri));
                        Log.e("KKK", "finalFile: " + finalFile.getAbsolutePath());

//                        //first get the path
//                        String path = Saveme(bitmap, "image_name.jpg");
//                        //set it in your gallery
//                        phone(bitmap, "my image", "my image test for gallery save");
//                        Log.e("JJJ", "onActivityResult: " + "*my image(3).jpg");
//                        File imgFile = new  File(path);
//                        if(imgFile.exists()){
//                            Log.e("JJJ", "imgFile.exists() "+ imgFile.exists());
//                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
//                            ImageView myImage = (ImageView) findViewById(R.id.imgProfilePicture2);
//
//                            myImage.setImageBitmap(myBitmap);
//                        }
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> startActivityIntentGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri selectedImageUri = data.getData();
                        InputStream inputStream = null;
                        try {
                            inputStream = getContentResolver().openInputStream(selectedImageUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgProfilePicture2.setImageBitmap(bitmap);
                        tempUri = getImageUri(getApplicationContext(), bitmap);
                        File finalFile2 = new File(getRealPathFromURI(tempUri));
                        Log.e("PPP", "onActivityResult:  "+finalFile2 );
                    }
                }
            }
    );

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "non", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    @Override
    public void DataSend(String name) {
        Log.e("MMM", "DataSend: " + name);

    }
}