package org.deuxpiedsdeuxroues.velobs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.location.LocationListener;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.net.URL;
import java.net.HttpURLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class GeoLocActivity extends ActionBarActivity implements LocationListener  {

    private LocationManager locationManager;
    private Button proxPoiButton ;
    private String responseProxPoi ;
    private String latitude = null;
    private String longitude = null ;
    private boolean categoriesDownloaded = false ;
    private boolean doublonsDownloaded = false ;
    private String categories;
    private Context c;
    private boolean boutonGPS = false ;
    private boolean foundGPSfix = false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_loc);

        c = this ;

        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tMgr.getLine1Number()!=null) {
            VelobsSingleton.getInstance().tel = tMgr.getLine1Number();
        } else {
            VelobsSingleton.getInstance().tel = "";
        }

        String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat(DATE_FORMAT_NOW);
        VelobsSingleton.getInstance().dateObs = simpleDate.format(cal.getTime());

        loadCat();

        proxPoiButton = (Button) findViewById(R.id.proxPoi);
        proxPoiButton.setVisibility(View.INVISIBLE);


        Button cancel = (Button) findViewById(R.id.prevButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                finish();

            }
        });

        final Context ct = this ;

        Button gps = (Button) findViewById(R.id.gpsButton);
        gps.setOnClickListener(new View.OnClickListener() {
                                   public void onClick(View view) {

                                       if (latitude==null&&longitude==null) {

                                           locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);

                                           if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                               abonnement();
                                           }

                                           DialogInterface.OnCancelListener mProgressCanceled = new DialogInterface.OnCancelListener() {
                                               public void onCancel(DialogInterface arg0) {
                                                   
                                               }
                                           };

                                           mProgressDialog = ProgressDialog.show(c, "Veuillez patienter",
                                                   "Recherche de votre localisation ...", true, true, mProgressCanceled);

                                           Message msg = null;
                                           String progressBarData = "Recherche de votre localisation ...";

                                           msg = mHandler.obtainMessage(MSG_IND, (Object) progressBarData);

                                           mHandler.sendMessage(msg);

                                           boutonGPS = true;

                                           final Handler handler = new Handler();
                                           handler.postDelayed(new Runnable() {
                                               @Override
                                               public void run() {
                                                   
                                                   if (foundGPSfix==false) {
                                                       
                                                       Toast.makeText(ct,"le gps n'a pas trouvé votre position, Veuillez choisir le lieu sur la carte"
                                                               ,Toast.LENGTH_LONG).show();
                                                       Intent myIntent = new Intent(GeoLocActivity.this, MapActivity.class);
                                                       GeoLocActivity.this.startActivity(myIntent);
                                                       Message msg = null;
                                                       String progressBarData = "pas trouvé ...";

                                                       msg = mHandler.obtainMessage(MSG_CNF, (Object) progressBarData);

                                                       mHandler.sendMessage(msg);


                                                   } else {
                                                       System.out.println("fix trouvé après 1 seconde");
                                                   }
                                               }
                                           }, 20000);
                                       }



                                   }
                               }
        );

        Button map = (Button) findViewById(R.id.mapButton);
        map.setOnClickListener(new View.OnClickListener() {
                                   public void onClick(View view) {

                                       Intent myIntent = new Intent(GeoLocActivity.this, MapActivity.class);
                                       GeoLocActivity.this.startActivity(myIntent);

                                   }
                               }
        );


        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {



                if ( categoriesDownloaded&&doublonsDownloaded) {

                    VelobsSingleton.getInstance().lati = latitude;
                    VelobsSingleton.getInstance().longi = longitude;
                    VelobsSingleton.getInstance().typeGeoLoc = "gps" ;

                    System.out.println("longi :"+longitude) ;

                    Intent myIntent = new Intent(GeoLocActivity.this, CategoryActivity.class);
                    myIntent.putExtra("CATEGORIES", categories);

                    GeoLocActivity.this.startActivity(myIntent);
                } else {
                    if ((VelobsSingleton.getInstance().lati==null)&&(VelobsSingleton.getInstance().longi==null)) {
                        Toast.makeText(ct,"Aucune localisation n'est renseignée",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ct, "Attente de connection au serveur ...", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


        Button poi = (Button) findViewById(R.id.proxPoi);
        poi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent myIntent = new Intent(GeoLocActivity.this, ProximityPoiListActivity.class);
                myIntent.putExtra("POI_LIST", responseProxPoi);
                myIntent.putExtra("CATEGORIES", categories);

                VelobsSingleton.getInstance().lati = latitude;
                VelobsSingleton.getInstance().longi = longitude;
                VelobsSingleton.getInstance().typeGeoLoc = "gps" ;

                GeoLocActivity.this.startActivity(myIntent);



            }
        });


    }






    private void updateStatusProx() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    DocumentBuilderFactory fabrique = DocumentBuilderFactory
                            .newInstance();

                    DocumentBuilder constructeur = fabrique
                            .newDocumentBuilder();
                    Document document = constructeur.parse(new InputSource(new ByteArrayInputStream(responseProxPoi.trim().getBytes("utf-8"))));
                    Element root = document.getDocumentElement();

                    NodeList nListCodeRetour = document.getElementsByTagName("coderetour");

                    if (nListCodeRetour.getLength() == 1) {

                        String codeRetourString = ((Element) nListCodeRetour.item(0))
                                .getAttribute("result");

                        int codeRetour = Integer.parseInt(codeRetourString);

                        if (codeRetour == 0) {

                            Toast.makeText(GeoLocActivity.this, "Aucune observation à proximité", Toast.LENGTH_SHORT).show();
                            proxPoiButton.setVisibility(View.INVISIBLE);


                        } else {

                            NodeList nListPoi = document.getElementsByTagName("poi");
                            
                            if (codeRetour == 1) {
                                proxPoiButton.setText("Voir l'observation enregistrée à proximité");
                                proxPoiButton.setVisibility(View.VISIBLE);

                            } else {
                                proxPoiButton.setText("Voir les observations enregistrées à proximité");
                                proxPoiButton.setVisibility(View.VISIBLE);
                            }

                        }
                        doublonsDownloaded = true;

                        Message msg = null;
                        msg = mHandler
                                .obtainMessage(MSG_CNF,
                                        "");
                        mHandler.sendMessage(msg);

                    } 


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private void checkProxPoi(double lat,double lng) {

        final String latPara = String.valueOf(lat) ;
        final String lngPara = String.valueOf(lng) ;

        Thread sendProcess = new Thread((new Runnable() {

            public void run() {
                InputStream is = null;

                try {

                    Message msg = null;
                    String progressBarData = "Recherche d'observations proches ...";


                    msg = mHandler.obtainMessage(MSG_IND,
                            (Object) progressBarData);

                    mHandler.sendMessage(msg);

                    try {       
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(
                                "http://"+c.getString(R.string.url_servername)+"/lib/php/mobile/checkProxPoi.php");

                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);



                        builder.addTextBody("lat",latPara);
                        builder.addTextBody("lng", lngPara);

                        final HttpEntity yourEntity = builder.build();

                        httpPost.setEntity(yourEntity);



                        HttpResponse httpResponse = httpClient
                                .execute(httpPost);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();

                    }  catch (org.apache.http.client.ClientProtocolException e) {
                        e.printStackTrace();

                    }  catch (IOException e) {
                        e.printStackTrace();

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
                        responseProxPoi = sb.toString();
                        updateStatusProx();
                        is.close();


                    } catch (Exception e) {
                        Log.e("Buffer Error",
                                "Error converting result " + e.toString());

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }));
        sendProcess.start();
    }

    public void onResume() {
        super.onResume();

        if (VelobsSingleton.getInstance().checkPOI== true ) {

            VelobsSingleton.getInstance().checkPOI = false ;
            Toast.makeText(GeoLocActivity.this, "Si votre observation semble nouvelle, passez à l'étape suivante", Toast.LENGTH_SHORT).show();
        } else {

            if (VelobsSingleton.getInstance().lati != null && VelobsSingleton.getInstance().longi != null) {
                System.out.println("on revient de map avec une lat long");
                longitude = VelobsSingleton.getInstance().longi;
                latitude = VelobsSingleton.getInstance().lati;

                DialogInterface.OnCancelListener mProgressCanceled = new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface arg0) {


                    }
                };

                mProgressDialog = ProgressDialog.show(c, "Veuillez patienter",
                        "Recherche des enregistrements proches ...", true, true, mProgressCanceled);

                Message msg = null;
                String progressBarData = "Recherche des enregistrements proches ...";


                msg = mHandler.obtainMessage(MSG_IND, (Object) progressBarData);

                mHandler.sendMessage(msg);

                checkProxPoi(Double.parseDouble(VelobsSingleton.getInstance().lati),
                        Double.parseDouble(VelobsSingleton.getInstance().longi));


            }
        }

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        desabonnement();
    }


    public void abonnement() {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

    }


    public void desabonnement() {

        if (locationManager!= null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {

        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());

        System.out.println("GPS long : " + longitude + " lat :" + latitude);

        foundGPSfix = true ;

        Message msg = null;
        String progressBarData = "Localisation trouvée";

        msg = mHandler.obtainMessage(MSG_CNF, (Object) progressBarData);

        mHandler.sendMessage(msg);


        checkProxPoi(location.getLatitude(), location.getLongitude()) ;
    }

    @Override
    public void onProviderDisabled(final String provider) {

    }

    @Override
    public void onProviderEnabled(final String provider) {

    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) { }


    public void loadCat() {
        new DownloadCatTask().execute();
    }

    private class DownloadCatTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            URL url = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder;

            try {

                url = new URL(
                        "http://"+c.getString(R.string.url_servername)+"/lib/php/mobile/getMobileCategory.php");
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();

                connection.setRequestMethod("GET");

                connection.setReadTimeout(15 * 1000);
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                categories = stringBuilder.toString().trim();
                categoriesDownloaded = true ;

            } catch (Exception e) {


            } finally {

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }

            return null;
        }
    }

    public static final int MSG_IND = 2;
    public static final int MSG_CNF = 1;
    public static final int MSG_ERR = 0;
    protected ProgressDialog mProgressDialog;


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

                    break;
                case MSG_ERR:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }


                    break;
                default: // should never happen
                    break;
            }
        }
    };





}
