package com.example.user.wifistaterecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.user.wifistaterecord.room.WiFIStateData;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<WiFIStateData> {

    private int mResource;
    public ListViewAdapter(Context context, int resource, List<WiFIStateData> objects) {
        super(context, resource, objects);
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WiFIStateData data = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(mResource,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.id = (TextView)view.findViewById(R.id.id);
            viewHolder.time = (TextView)view.findViewById(R.id.time);
            viewHolder.state = (TextView)view.findViewById(R.id.state);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.id.setText(String.valueOf(data.id));
        viewHolder.time.setText(data.time);
        viewHolder.state.setText(data.state);

        return view;
    }

    class ViewHolder{
        TextView id;
        TextView time;
        TextView state;

    }
}
