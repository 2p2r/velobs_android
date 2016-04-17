package org.deuxpiedsdeuxroues.velobs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ProximityPoiListActivity extends ActionBarActivity {

    String repsonseProxPoi = null ;
    String categories = null ;
    ListView lvListe = null;
    ProximityPoiListAdapter adapter;
    List<PointOfInterest> maListe = new ArrayList<PointOfInterest>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_poi_list);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                repsonseProxPoi = null;
                categories = null ;
            } else {
                repsonseProxPoi = extras.getString("POI_LIST");
                categories = extras.getString("CATEGORIES");
            }
        } else {
            repsonseProxPoi = (String) savedInstanceState.getSerializable("POI_LIST");
            categories = (String) savedInstanceState.getSerializable("CATEGORIES");
        }

        Button next = (Button) findViewById(R.id.newObsButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent myIntent = new Intent(ProximityPoiListActivity.this, CategoryActivity.class);
                myIntent.putExtra("CATEGORIES", categories);

                ProximityPoiListActivity.this.startActivity(myIntent);

            }
        });

        remplirLaListe();

        lvListe = (ListView) findViewById(R.id.listPoi);

        adapter = new ProximityPoiListAdapter(this, maListe);

        lvListe.setAdapter(adapter);


        lvListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                VelobsSingleton.getInstance().poi = maListe.get(position);
                Intent myIntent = new Intent(ProximityPoiListActivity.this,
                        ReviewProximityPoiActivity.class);
                ProximityPoiListActivity.this.startActivity(myIntent);



            }
        });

    }

    public void onPause() {
        super.onPause();
        VelobsSingleton.getInstance().checkPOI = true;
        overridePendingTransition(0, 0);
    }

    public void remplirLaListe () {


        try {
            DocumentBuilderFactory fabrique = DocumentBuilderFactory
                    .newInstance();

            DocumentBuilder constructeur = fabrique
                    .newDocumentBuilder();
            Document document = constructeur.parse(new InputSource(new ByteArrayInputStream(repsonseProxPoi.trim().getBytes("utf-8"))));
            Element root = document.getDocumentElement();

            NodeList nListPoi = document.getElementsByTagName("poi");


            for (int i=0;i<nListPoi.getLength();i++) {
                
                maListe.add(new PointOfInterest(
                        ((Element) nListPoi.item(i)).getAttribute("id"),
                        ((Element) nListPoi.item(i)).getElementsByTagName("category").item(0).getTextContent(),
                        ((Element) nListPoi.item(i)).getElementsByTagName("adresse").item(0).getTextContent(),
                        ((Element) nListPoi.item(i)).getElementsByTagName("distance").item(0).getTextContent(),
                        ((Element) nListPoi.item(i)).getElementsByTagName("status").item(0).getTextContent(),
                        ((Element) nListPoi.item(i)).getElementsByTagName("photo").item(0).getTextContent(),
                        ((Element) nListPoi.item(i)).getElementsByTagName("ville").item(0).getTextContent(),
                        ((Element) nListPoi.item(i)).getElementsByTagName("desc").item(0).getTextContent()


                ));


            }



        } catch (Exception e) {
            e.printStackTrace();
        }



    }




}
