package org.deuxpiedsdeuxroues.velobs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;


public class ReviewProximityPoiActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_proximity_poi);

        TextView addresstv = (TextView) findViewById(R.id.adresstextview);
        addresstv.setText("dans la ville de " + VelobsSingleton.getInstance().poi.getVille() + ", " + VelobsSingleton.getInstance().poi.getAddress());

        TextView distancetv = (TextView) findViewById(R.id.distancetextview);
        distancetv.setText("à " + VelobsSingleton.getInstance().poi.getDistance() + " mètres");
        TextView categorytv = (TextView) findViewById(R.id.categorytextview);
        categorytv.setText("Catégorie : " + VelobsSingleton.getInstance().poi.getCategory());
        TextView desctv = (TextView) findViewById(R.id.descriptiontextview);
        desctv.setText("Description : " + VelobsSingleton.getInstance().poi.getDescription());

        if (VelobsSingleton.getInstance().poi.getPhoto().trim().length()>0) {

            new DownloadImageTask((ImageView) findViewById(R.id.photoView))
                    .execute("http://" + this.getString(R.string.url_servername) + "/resources/pictures/" + VelobsSingleton.getInstance().poi.getPhoto());
        } else {
            ImageView photoView = (ImageView) findViewById(R.id.photoView) ;
            photoView.requestLayout();
            photoView.getLayoutParams().height = 1;
        }


        Button cancel = (Button) findViewById(R.id.prevButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                finish();

            }
        });

        final Context ct = this;

        Button maj = (Button) findViewById(R.id.updateButton);
        maj.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent myIntent = new Intent(ReviewProximityPoiActivity.this, UpdateProximityPoiActivity.class);
                ReviewProximityPoiActivity.this.startActivity(myIntent);
            }
        });

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if (result.getHeight()<result.getWidth()) {
                bmImage.requestLayout();
                bmImage.getLayoutParams().height = dpToPx(135) ;
            }


            bmImage.setImageBitmap(result);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }



}
