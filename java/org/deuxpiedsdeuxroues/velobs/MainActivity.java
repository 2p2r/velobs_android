package org.deuxpiedsdeuxroues.velobs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    boolean hasMail = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        Display display = getWindowManager().getDefaultDisplay();

        int height = 0 ;

            height = display.getHeight();


        if ((height>0)&&(height<801)) {

            TextView tvmail = (TextView) findViewById(R.id.mailtv) ;
            tvmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
            TextView tvcall = (TextView) findViewById(R.id.textCallService) ;
            tvcall.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);

        }




        Button envoi = (Button) findViewById(R.id.newRecordButton);
        envoi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                ConnectivityManager cm =
                        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (isConnected) {

                    if (hasMail) {

                        Intent myIntent = new Intent(MainActivity.this, GeoLocActivity.class);
                        MainActivity.this.startActivity(myIntent);
                    } else {
                        Toast.makeText(context, "Veuillez renseigner un mail de suivi", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Aucune connection internet n'est disponible ...", Toast.LENGTH_LONG).show();
                }

            }
        });


        Button sendSaved = (Button) findViewById(R.id.callServiceButton);
        sendSaved.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:0561222222"));
                startActivity(callIntent);

            }
        });





    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void onResume() {
        super.onResume();
        VelobsSingleton.getInstance().reset();

        Button mailButton = (Button) findViewById(R.id.mailButton);
        mailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, MailActivity.class);
                MainActivity.this.startActivity(myIntent);

            }
        });

        TextView tvmail = (TextView) findViewById(R.id.mailtv) ;
        String mail = getValue(this) ;
        if (mail!=null) {
            tvmail.setText("Mail de suivi : "+mail);
            mailButton.setText("Changer le mail de suivi");
            VelobsSingleton.getInstance().mail = mail ;
            hasMail = true ;
        } else {
            tvmail.setText("Vous n'avez pas de mail de suivi");
            mailButton.setText("Ajouter un mail de suivi");
            hasMail = false ;
        }

    }


    public static final String PREFS_NAME = "VelobsPrefs";
    public static final String PREFS_KEY = "mail_String";

    public String getValue(Context context) {
        SharedPreferences settings;
        String text;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); 
        text = settings.getString(PREFS_KEY, null); 
        return text;
    }

    public void save(Context context, String text) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); 
        editor = settings.edit(); 

        editor.putString(PREFS_KEY, text); 
        editor.commit(); 
    }

}
