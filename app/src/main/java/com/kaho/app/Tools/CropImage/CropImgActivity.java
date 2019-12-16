package com.kaho.app.Tools.CropImage;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kaho.app.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kaho.app.UI.KahooUi.AddingKahoo.AddNewKahooFragment.PERMISSIONS_REQUEST_TOKEN;
import static com.kaho.app.UI.KahooUi.AddingKahoo.AddNewKahooFragment.PHOTO_FROM_GALLERY;

public class CropImgActivity extends AppCompatActivity {

    private CropImageView mCropImageView;

    private Uri mCropImageUri;
    private int IMAGE_GALLERY_LOAD_REQUEST=3485;
    private TextView CropImage;
    private RelativeLayout selectToAddImage;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        CropImage=findViewById(R.id.crop_image);

        backButton=findViewById(R.id.ivBack);
        selectToAddImage=findViewById(R.id.add_image_layout);

        if (getIntent().hasExtra("selected_uri")) {
            Log.e("kjdfhsdkdsf","camera img uri crop screen intent data "+getIntent().getExtras().getString("selected_uri"));
            //selectToAddImage.setVisibility(View.GONE);
            Bundle bundle=getIntent().getExtras();
            Uri myImgUri = Uri.parse(bundle.getString("selected_uri"));
            //cameraImgUri=bundle.getu("selected_uri");
            //Log.e("kjdfhsdkdsf","camera img uri crop screen FROM BUNDEL "+bundle.getString("selected_uri"));
            launchCropImage(myImgUri,true);
        }else {
            if (CheckPermission()) {
                //startActivityForResult(getPickImageChooserIntent(), 200);
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_GALLERY_LOAD_REQUEST);
            }else {
                askForPermission();
            }
        }

        selectToAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckPermission()) {
                    //startActivityForResult(getPickImageChooserIntent(), 200);
                    Intent i = new Intent(
                            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, IMAGE_GALLERY_LOAD_REQUEST);
                }else {
                    askForPermission();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCropImageClick();
            }
        });

    }
    private boolean CheckPermission() {
        boolean grantPermission;
        if (ActivityCompat.checkSelfPermission(CropImgActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CropImgActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            grantPermission = false;
        } else {
            grantPermission = true;
        }
        return grantPermission;
    }

    /**
     * On load image button click, start pick image chooser activity.
     */
    public void onLoadImageClick(View view) {

    }

    /**
     * Crop the image and set it back to the cropping view.
     */
    public void onCropImageClick() {
        Bitmap cropped = mCropImageView.getCroppedImage();
        SaveImage(cropped);
        /*if (cropped != null){
            Intent intent=new Intent();
            intent.putExtra("user_profile_img",imageName);
            Log.e("djhfkfssf","user selected image "+imageName);
            setResult(RESULT_OK,intent);
            //startActivity(intent);
            CropImageActivity.this.finish();
            //mCropImageView.setImageBitmap(cropped);
        }*/
        //
    }
    String croppedImgUrl;
    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "CroppedImage"+n+".jpg";
        final File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            //     Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        croppedImgUrl=file.toString();
                        Intent intent=new Intent();
                        intent.putExtra("user_profile_img",croppedImgUrl);
                        Log.e("djhfkfssf","user selected image "+croppedImgUrl);
                        setResult(PHOTO_FROM_GALLERY,intent);
                        //startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e("sjhsfsfs"," request code "+requestCode);
        if (grantResults.length> 0) {
            boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Log.e("sjhsfsfs","read extrernal file status "+readExternalFile+" request code "+requestCode);
            if (readExternalFile){
                showImageChooser();
            }else {
                Toast.makeText(CropImgActivity.this,"Permission required to get images",Toast.LENGTH_SHORT).show();
            }
        }else {
            Log.e("sjfhskj","grant reuslt length is zero "+grantResults.length);
        }
        /*
        switch (requestCode) {
            case PERMISSIONS_REQUEST_TOKEN:

                break;
        }*/
    }

    public void askForPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(CropImgActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) +  ContextCompat
                    .checkSelfPermission(CropImgActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (CropImgActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (CropImgActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    /*Snackbar.make(CropImageActivity.this.findViewById(android.R.id.content),
                            R.string.PERMISSION_SNACK_BAR_MESSAGE,
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{Manifest.permission
                                                    .READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSIONS_REQUEST_TOKEN);
                                }
                            }).show();*/

                }else {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_TOKEN);
                }

            }else {
                showImageChooser();
            }


        }
        else{
            showImageChooser();
        }
    }

    private void showImageChooser() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_GALLERY_LOAD_REQUEST);
        //startActivityForResult(getPickImageChooserIntent(), 200);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_LOAD_REQUEST && resultCode == RESULT_OK && null != data) {
            if (data==null||data.getData()==null){
                finish();
            }else {
                launchCropImage(data.getData(),false);
            }

            // String picturePath contains the path of selected Image
        }
        /*if (resultCode == Activity.RESULT_OK) {

            if (data == null) {
                Intent intent = new Intent();
                intent.putExtra("user_profile_img", "");
                Log.e("sdfhsfdsf", "data extra is null result ok ");
                setResult(RESULT_OK, intent);
                //startActivity(intent);
                CropImageActivity.this.finish();
            } else {

            }

        }*/
    }

    private void launchCropImage(Uri selectedImage,boolean isFile) {
         //= data.getData();
        Uri imageUri;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        String picturePath;
        if (cursor!=null){
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath= cursor.getString(columnIndex);
            cursor.close();
            Log.e("sdfhsfdsf", "if condition inside on activity result picturePath " + picturePath);
        }else {
            picturePath=selectedImage.getPath();
            Log.e("sdfhsfdsf", "else condition inside on activity result picturePath " + picturePath);
        }

        Log.e("sdfhsfdsf", "inside on activity result picturePath " + picturePath);

        imageUri = Uri.fromFile(new File(picturePath));
        /*if (!isFile){

        }else {
            imageUri=selectedImage;
        }*/

        //Uri imageUri = getPickImageResultUri(data);
        Log.e("sdfhsfdsf", "inside on activity result result ok " + imageUri);
        //mCropImageView.setAspectRatio(1, 1);
        mCropImageView.setGuidelines(CropImageView.Guidelines.OFF);
        mCropImageView.setImageUriAsync(imageUri);
        CropImage.setVisibility(View.VISIBLE);
        selectToAddImage.setVisibility(View.GONE);
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCropImageView.setImageUriAsync(mCropImageUri);
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }
*/
    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            Log.e("ksdjfsklf","this is error "+e.getMessage());
            return true;
        } catch (Exception e) {
            Log.e("ksdjfsklf","this is exception error "+e.getMessage());
        }
        return false;
    }
}
