package com.example.cameragallerypermission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView imgProfilePicture;
    Button takePhoto, openGallery, nextPage;
    BottomSheetDialog bsd;
    private static final int PICK_FROM_GALLERY = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextPage = findViewById(R.id.btnNextPage);
        nextPage.setOnClickListener(view -> {
            Log.e("MMM", "onClick: ");
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });

        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        String path = getIntent().getStringExtra("path");
        Log.e("LLL", "image path is: " + path);


        if (path != null) {
//            File file = new File(path);
//            Uri imageUri = Uri.fromFile(file);
//            Glide.with(this).load(imageUri).into(imgProfilePicture);
            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent()
                            .getByteArrayExtra("byteArray").length);
            imgProfilePicture.setImageBitmap(b);
        }
        imgProfilePicture.setOnClickListener(view -> {
            bsd = new BottomSheetDialog(MainActivity.this);
            View view1 = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.bottom_dialog_sheet, (LinearLayout) findViewById(R.id.sheet));
            bsd.setContentView(view1);
            bsd.show();

            takePhoto = bsd.findViewById(R.id.takePhoto);
            Objects.requireNonNull(takePhoto).setOnClickListener(view2 -> {
                if (checkPermission()) {
                    Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityIntent2.launch(iCamera);
                    bsd.dismiss();
                    Log.e("TAG", "Permission is granted: is called  ");
                    Log.e("TAG", "                                  ");
                } else {
                    requestPermission();
                    Log.e("TAG", "requestPermission : is called  ");
                    Log.e("TAG", "                                  ");
                }
            });

            openGallery = bsd.findViewById(R.id.openGallery);
            Objects.requireNonNull(openGallery).setOnClickListener(view22 -> {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // this will open the whole new gallery activity
                        startActivityIntent.launch(galleryIntent);
                        //startActivityForResult(galleryIntent,PICK_FROM_GALLERY);
//                        Intent iGALLERY = new Intent(MediaStore.ACTION_PICK_IMAGES); //this will open a gallery from bottom sheet dialog
//                        startActivityIntent.launch(iGALLERY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bsd.dismiss();
            });
        });


    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.e("TAG", "Permission: not granted  ");
            Log.e("TAG", "                                  ");
            return false;
        }
        Log.e("TAG", "Permission: granted  ");
        Log.e("TAG", "                                  ");
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("TAG", "onRequestPermissionsResult is called  " + grantResults[0]);
        Log.e("TAG", "                                  ");
        Log.e("TAG", " request code " + requestCode);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("TAG", "PackageManager.PERMISSION_GRANTED of CAMERA is called  "
                            + PackageManager.PERMISSION_GRANTED);
                    Log.e("TAG", "                                  ");
                    Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityIntent2.launch(iCamera);
                    bsd.dismiss();

                } else {
                    showMessageOKCancel(
                            (dialog, which) -> {
                                Log.e("TAG", "Device settings is called ");
                                Log.e("TAG", "                                   ");
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            });
                    Log.e("TAG", "showMessageOKCancel is called ");
                    Log.e("TAG", "                              ");
                }
                break;

            case PICK_FROM_GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("TAG", "PICK_FROM_GALLERY is called");
                    Log.e("TAG", "                                   ");
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // this will open the whole new gallery activity
                    startActivityIntent.launch(galleryIntent);
                    //startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                }
                break;
        }
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) { //when onActivity result is called
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                Uri uri = data.getData();
//                imgProfilePicture.setImageURI(uri);
//            }
//        }
//    }


    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("You need to allow access permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
        Log.e("TAG", "showMessageOKCancel is called ");
        Log.e("TAG", "                              ");
    }


    ActivityResultLauncher<Intent> startActivityIntent2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e("TAG", "startActivityIntent2 is called");
                    Log.e("TAG", "                               ");

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        assert result.getData() != null;
                        Bundle bundle = result.getData().getExtras();

                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        imgProfilePicture.setImageBitmap(bitmap);
                    }
                }
            });

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.e("TAG", "startActivityIntent is called");
                        Log.e("TAG", "                                   ");
                        Intent data = result.getData();
                        assert data != null;
                        Uri selectedImageUri = data.getData();

                        InputStream inputStream = null;
                        try {
                            inputStream = MainActivity.this.getContentResolver().openInputStream(selectedImageUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgProfilePicture.setImageBitmap(bitmap);
                    }
                }
            });

}

