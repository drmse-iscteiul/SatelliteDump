package com.iscte.mobileapps.satellitedump;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class NmeaListAdapter extends BaseAdapter {
    public static ArrayList<NmeaItem> nmeaArrayList;
    public static ArrayList<NmeaItem> newNmeaArrayList;
    private ViewHolder holder;
    private LayoutInflater mInflater;

    public NmeaListAdapter(Context context, ArrayList<NmeaItem> message)
    {
        nmeaArrayList = message;
        mInflater = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return nmeaArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return nmeaArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView = mInflater.inflate(R.layout.custom_row, null);
            holder = new ViewHolder();
            holder.txtname = (TextView) convertView.findViewById(R.id.name);
            holder.txtTelephone = (TextView) convertView.findViewById(R.id.telephone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(nmeaArrayList.get(position).getName().equals("$GPGGA")){
            holder.txtname.setText(nmeaArrayList.get(position).getName() + " - Global Positioning System Fix Data");
        } else if(nmeaArrayList.get(position).getName().equals("$GPGSV")) {
            holder.txtname.setText(nmeaArrayList.get(position).getName() + " - GPS Satellites in view");
        } else if(nmeaArrayList.get(position).getName().equals("$GLGSV")) {
            holder.txtname.setText(nmeaArrayList.get(position).getName() + " - GLONASS Satellites in view");
        } else if(nmeaArrayList.get(position).getName().equals("$BDGSV")) {
            holder.txtname.setText(nmeaArrayList.get(position).getName() + " - BeiDou Satellites in view");
        } else if(nmeaArrayList.get(position).getName().equals("$GPGSA")) {
            holder.txtname.setText(nmeaArrayList.get(position).getName() + " - GPS Dilution of precision and active satellites");
        } else if(nmeaArrayList.get(position).getName().equals("$GNGSA")) {
            holder.txtname.setText(nmeaArrayList.get(position).getName() + " - Dilution of precision and active satellites (Global Navigation Satellite System or GLONASS)");
        } else if(nmeaArrayList.get(position).getName().equals("$QZGSA")) {
            holder.txtname.setText(nmeaArrayList.get(position).getName() + " - Dilution of precision and active satellites (Quasi-Zenith Satellite System (QZSS))");
        } else if(nmeaArrayList.get(position).getName().equals("$BDGSA")) {
            holder.txtname.setText(nmeaArrayList.get(position).getName() + " - Dilution of precision and active satellites (BeiDou Navigation Satellite System)");
        } else if(nmeaArrayList.get(position).getName().equals("$GPRMC")) {
            holder.txtname.setText(nmeaArrayList.get(position).getName() + " - Recommended minimum specific GPS/Transit data");
        } else {
            holder.txtname.setText(nmeaArrayList.get(position).getName());
        }
        //holder.txtname.setText(nmeaArrayList.get(position).getName());
        holder.txtTelephone.setText(nmeaArrayList.get(position).getTelephone());

        return convertView;
    }

    static class ViewHolder
    {
        TextView txtname;
        TextView txtTelephone;
    }
}
