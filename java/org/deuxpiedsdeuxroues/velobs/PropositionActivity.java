package org.deuxpiedsdeuxroues.velobs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class PropositionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposition);
        Button cancel = (Button) findViewById(R.id.prevButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                finish();

            }
        });

        final EditText propositiontionText = (EditText) findViewById(R.id.propositiontext);
        propositiontionText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                VelobsSingleton.getInstance().prop = propositiontionText.getText().toString();

                Intent myIntent = new Intent(PropositionActivity.this, PictureAndSendActivity.class);
                PropositionActivity.this.startActivity(myIntent);

            }
        });

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
