package org.deuxpiedsdeuxroues.velobs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AddressActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        Button cancel = (Button) findViewById(R.id.prevButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                finish();

            }
        });

        final EditText nomderueText = (EditText) findViewById(R.id.nomderuetext);
        final EditText repereText = (EditText) findViewById(R.id.reperetext);
        nomderueText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        repereText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                VelobsSingleton.getInstance().rue = nomderueText.getText().toString();
                VelobsSingleton.getInstance().repere = repereText.getText().toString();


                Intent myIntent = new Intent(AddressActivity.this, DescriptionActivity.class);
                AddressActivity.this.startActivity(myIntent);

            }
        });

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
