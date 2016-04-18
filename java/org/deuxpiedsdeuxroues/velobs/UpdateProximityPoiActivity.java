package org.deuxpiedsdeuxroues.velobs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.deuxpiedsdeuxroues.velobs.picture.AlbumStorageDirFactory;
import org.deuxpiedsdeuxroues.velobs.picture.BaseAlbumDirFactory;
import org.deuxpiedsdeuxroues.velobs.picture.FroyoAlbumDirFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class UpdateProximityPoiActivity extends ActionBarActivity {

    private static final int ACTION_TAKE_PHOTO_B = 1;

    public static final int MSG_IND = 2;
    public static final int MSG_CNF = 1;
    public static final int MSG_ERR = 0;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;


    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;



    private String imageFileName ;
    private File laPhoto = null ;
    private File laPhotoResized = null ;

    private boolean withImage = false ;


    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_proximity_poi);

        EditText descriptionText = (EditText) findViewById(R.id.commentairetext);
        descriptionText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                sendCommentAndPicture();

            }
        });

        Button precedent = (Button) findViewById(R.id.prevButton);
        precedent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                finish();

            }
        });


        mImageView = (ImageView) findViewById(R.id.photoView);

        Button photo = (Button) findViewById(R.id.photobutton);
        setBtnListenerOrDisable(
                photo,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }


    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    Context ct = this ;

    private void sendCommentAndPicture() {

        DialogInterface.OnCancelListener mProgressCanceled = new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
            }
        };

        mProgressDialog = ProgressDialog.show(this, "Veuillez patienter",
                "l'envoi de votre commentaire et/ou photo commence...", true, true, mProgressCanceled);

        final EditText descriptionText = (EditText) findViewById(R.id.commentairetext);

        Thread sendProcess = new Thread((new Runnable() {

            public void run() {
                InputStream is = null;

                Message msg = null;
                String progressBarData = "Envoi des données ...";

                msg = mHandler.obtainMessage(MSG_IND, (Object) progressBarData);

                mHandler.sendMessage(msg);

                try {

                    try {
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(
                                "http://"+ct.getString(R.string.url_servername)+"/lib/php/mobile/velObsUpdatePoi.php");

                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                        if (withImage) {

                            FileBody fb = new FileBody(laPhotoResized, ContentType.create("image/jpg"), laPhotoResized.getName());
                            builder.addPart("photo1", fb);
                        }

                        builder.addTextBody("id", VelobsSingleton.getInstance().poi.getId());
                        builder.addTextBody("comment",descriptionText.getText().toString() );
                        builder.addTextBody("mail",VelobsSingleton.getInstance().mail);

                        final HttpEntity yourEntity = builder.build();

                        httpPost.setEntity(yourEntity);

                        HttpResponse httpResponse = httpClient
                                .execute(httpPost);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        progressBarData = "Erreur dans l'envoi";
                        System.out.println("UnsupportedEncodingException");
                        msg = mHandler.obtainMessage(MSG_ERR,
                                (Object) progressBarData);

                        mHandler.sendMessage(msg);

                    }  catch (org.apache.http.client.ClientProtocolException e) {
                        e.printStackTrace();
                        progressBarData = "Erreur dans l'envoi";
                        msg = mHandler.obtainMessage(MSG_ERR,
                                (Object) progressBarData);

                        mHandler.sendMessage(msg);

                    }  catch (IOException e) {
                        e.printStackTrace();
                        progressBarData = "Erreur dans l'envoi";
                        msg = mHandler.obtainMessage(MSG_ERR,
                                (Object) progressBarData);

                        mHandler.sendMessage(msg);

                    }

                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(is, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                            Log.i("Server response", line);
                        }
                        is.close();

                        if (sb.toString().trim().equalsIgnoreCase("sqlKO")) {
                            progressBarData = "Erreur dans l'envoi";
                            msg = mHandler.obtainMessage(MSG_ERR,
                                    (Object) progressBarData);

                            mHandler.sendMessage(msg);
                        } else {

                            msg = mHandler
                                    .obtainMessage(MSG_CNF,
                                            "Succès de l'envoi! Merci de votre collaboration");
 
                            mHandler.sendMessage(msg);
                        }

                    } catch (Exception e) {
                        Log.e("Buffer Error",
                                "Error converting result " + e.toString());
                        progressBarData = "Erreur dans l'envoi";
                        msg = mHandler.obtainMessage(MSG_ERR,
                                (Object) progressBarData);

                        mHandler.sendMessage(msg);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    progressBarData = "Erreur dans l'envoi";
                    msg = mHandler.obtainMessage(MSG_ERR,
                            (Object) progressBarData);

                    mHandler.sendMessage(msg);
                }

            }
        }));
        sendProcess.start();




    }

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_IND:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.setMessage(((String) msg.obj));
                    }
                    break;
                case MSG_CNF:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(),
                            "Envoi réussi! Merci de votre collaboration",
                            Toast.LENGTH_LONG).show();

                    Intent a = new Intent(ct,MainActivity.class);
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(a);


                    break;
                case MSG_ERR:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    Toast.makeText(getBaseContext(),
                            "Une erreur dans l'envoi s'est produite ...",
                            Toast.LENGTH_LONG).show();
                    break;
                default: // should never happen
                    break;
            }
        }
    };


    private String getAlbumName() {
        return getString(R.string.album_name);
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        return null;
                    }
                }
            }

        } 

        return storageDir;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setPic() {

        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);

        int new_width = 0;
        int new_height = 0;

        if (photoH>photoW) {
            new_height =1024;
            new_width = photoW*1024/photoH;
        } else {
            new_width=1024;
            new_height=photoH*1024/photoW;
        }

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, new_width, new_height, true);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageResizedFileName = timeStamp + "_resized";
        File albumF = getAlbumDir();
        try {
            laPhotoResized = File.createTempFile(imageResizedFileName, JPEG_FILE_SUFFIX, albumF);

            FileOutputStream fos_resized = new FileOutputStream(laPhotoResized);


            resized.compress(Bitmap.CompressFormat.JPEG, 100, fos_resized);
            fos_resized.flush();
            fos_resized.close();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        withImage = true ;
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    laPhoto = f ;
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }



    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                }
            };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } 


        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

   
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

}
