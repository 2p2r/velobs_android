package org.deuxpiedsdeuxroues.velobs;


import android.content.Context;

import java.io.File;

public class VelobsSingleton {

    File imageFile ;
    boolean withImage = false ;
    String lati ;
    String longi ;
    String dateObs ;
    String desc ;
    String prop ;
    String subCat ;
    String typeGeoLoc ;
    String mail ;
    String tel ;
    String rue ;
    String repere ;
    Context c;
    boolean checkPOI ;
    PointOfInterest poi;

    private static final VelobsSingleton instance = new VelobsSingleton();

    private VelobsSingleton() {}

    public final static VelobsSingleton getInstance() {
        return instance;
    }


    public void reset() {
        imageFile = null ;
        lati = null ;
        longi = null ;
        dateObs = null ;
        desc = null ;
        prop = null ;
        subCat = null ;
        typeGeoLoc = null ;
        mail = null ;
        tel = null ;
        rue = null ;
        repere = null ;
        c = null ;
        withImage = false ;
        checkPOI = false ;
        poi = null ;
    }



}
