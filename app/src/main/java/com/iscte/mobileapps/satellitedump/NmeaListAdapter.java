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

    private LayoutInflater mInflater;

    public NmeaListAdapter(Context context, ArrayList<NmeaItem> message)
    {
        nmeaArrayList = message;
        mInflater = LayoutInflater.from(context);

        newArray();
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
        ViewHolder holder;
        if(convertView==null)
        {
            convertView = mInflater.inflate(R.layout.custom_row, null);
            holder = new ViewHolder();
            holder.txtname = (TextView) convertView.findViewById(R.id.name);
            holder.txtTelephone = (TextView) convertView.findViewById(R.id.telephone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtname.setText(nmeaArrayList.get(position).getName());
        holder.txtTelephone.setText(nmeaArrayList.get(position).getTelephone());
        return convertView;
    }


    static class ViewHolder
    {
        TextView txtname;
        TextView txtTelephone;
    }

    public void newArray(){




        HashSet<NmeaItem> hashSet = new HashSet<NmeaItem>();
        hashSet.addAll(nmeaArrayList);
        nmeaArrayList.clear();
        nmeaArrayList.addAll(hashSet);

    }

}
