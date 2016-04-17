package org.deuxpiedsdeuxroues.velobs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        final EditText mailText = (EditText) findViewById(R.id.mailtext);
        mailText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        if (VelobsSingleton.getInstance().mail!=null) {
            mailText.setHint(VelobsSingleton.getInstance().mail);
        }


        final Context ct = this ;

        Button next = (Button) findViewById(R.id.okButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (isValidEmail(mailText.getText())) {


                    save(ct, mailText.getText().toString());

                    finish();

                } else {
                    Toast.makeText(ct, "Cet email n'est pas valide ...", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public static final String PREFS_NAME = "VelobsPrefs";
    public static final String PREFS_KEY = "mail_String";


    public void save(Context context, String text) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); 
        editor = settings.edit(); 

        editor.putString(PREFS_KEY, text); 
        editor.commit(); 
    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }



}
