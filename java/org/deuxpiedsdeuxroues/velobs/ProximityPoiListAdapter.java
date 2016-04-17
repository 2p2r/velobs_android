package org.deuxpiedsdeuxroues.velobs;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ProximityPoiListAdapter extends BaseAdapter {

    List<PointOfInterest> uneListe;

    LayoutInflater inflater;


    public boolean isLoc = false;
    public double latiHere;
    public double longiHere;

    public ProximityPoiListAdapter(Context context, List<PointOfInterest> liste) {

        inflater = LayoutInflater.from(context);

        this.uneListe = liste;


    }

    @Override
    public int getCount() {
        return uneListe.size();
    }

    @Override
    public Object getItem(int arg0) {
        return uneListe.get(arg0);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    private class ViewHolder {

        TextView tvCategory;
        TextView tvAdress;
        TextView tvDistance;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

           
                convertView = inflater.inflate(R.layout.poilist_item, null);


            holder.tvCategory = (TextView)convertView.findViewById(R.id.tvCategory);
            holder.tvAdress = (TextView)convertView.findViewById(R.id.tvAdress);
            holder.tvDistance = (TextView)convertView.findViewById(R.id.tvDistance);



            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        holder.tvCategory.setText(uneListe.get(position).getCategory());

        holder.tvAdress.setText(uneListe.get(position).getAddress());

        holder.tvDistance.setText("à "+uneListe.get(position).getDistance()+" mètres");
        holder.tvDistance.setLines(1);
        holder.tvDistance.setTextColor(Color.argb(255, 0, 145, 210));


        return convertView;
    }

}
