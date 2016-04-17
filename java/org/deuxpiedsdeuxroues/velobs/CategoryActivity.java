package org.deuxpiedsdeuxroues.velobs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class CategoryActivity extends ActionBarActivity {

    String categories ;
    String idSpinner ;
    ArrayAdapter<SousCategorie> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                categories= null;
            } else {
                categories= extras.getString("CATEGORIES");
            }
        } else {
            categories= (String) savedInstanceState.getSerializable("CATEGORIES");
        }


        Button cancel = (Button) findViewById(R.id.prevButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                finish();

            }
        });

        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                VelobsSingleton.getInstance().subCat=idSpinner;


                Intent myIntent = new Intent(CategoryActivity.this, AddressActivity.class);
                CategoryActivity.this.startActivity(myIntent);

            }
        });







        Spinner spinner = (Spinner) findViewById(R.id.spinnercat);

        adapter = new ArrayAdapter<SousCategorie>(this,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(sListener);
        populateSpinner();

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private Spinner.OnItemSelectedListener sListener = new Spinner.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {

            idSpinner = ((SousCategorie) parent.getSelectedItem()).getIdSousCat();

        }

        public void onNothingSelected(AdapterView parent) {

        }
    };

    private void populateSpinner() {

        try {

            adapter.clear();

            DocumentBuilderFactory fabrique = DocumentBuilderFactory
                    .newInstance();

            DocumentBuilder constructeur = fabrique
                    .newDocumentBuilder();

            Document document = constructeur.parse(new InputSource(new ByteArrayInputStream(categories.getBytes("utf-8"))));

            Element racine = document.getDocumentElement();
            NodeList liste = racine.getElementsByTagName("categorie");

            for (int i = 0; i < liste.getLength(); i++) {
                
                NodeList liste2 = ((Element) liste.item(i))
                        .getElementsByTagName("souscategorie");

                for (int j = 0; j < liste2.getLength(); j++) {
                    

                    adapter.add(new SousCategorie(
                            ((Element) liste2.item(j))
                                    .getAttribute("id"),
                            ((Element) liste2.item(j))
                                    .getAttribute("nom"),
                            ((Element) liste.item(i))
                                    .getAttribute("id")));
                }
            }
        } catch (Exception e) {

        }

    }


    class SousCategorie {
        public SousCategorie(String idSousCat, String value, String idCat) {
            this.idSousCat = idSousCat;
            this.value = value;
            this.idCat = idCat;
        }

        public String getSpinnerText() {
            return value;
        }

        public String getIdSousCat() {
            return idSousCat;
        }

        public String getIdCat() {
            return idCat;
        }

        public String toString() {
            return value;
        }

        String idSousCat;
        String value;
        String idCat;
    }




}
