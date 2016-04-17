package org.deuxpiedsdeuxroues.velobs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class DescriptionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Button cancel = (Button) findViewById(R.id.prevButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                finish();

            }
        });

        final EditText descriptionText = (EditText) findViewById(R.id.descriptiontext);
        descriptionText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                VelobsSingleton.getInstance().desc = descriptionText.getText().toString();

                Intent myIntent = new Intent(DescriptionActivity.this, PropositionActivity.class);
                DescriptionActivity.this.startActivity(myIntent);

            }
        });

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
