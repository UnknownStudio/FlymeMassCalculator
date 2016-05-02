package com.trychen.flyme.masscalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by trychen on 16/4/24.
 */
public class MassListViewAdapter extends BaseAdapter{
    private LayoutInflater listContainer;
    public MassListViewAdapter(Context context) {
        listContainer = LayoutInflater.from(context);
    }

    public final class ListItemView{                //自定义控件集合
        public TextView name;
        public TextView num;
//        public TextView per;
    }

    @Override
    public int getCount() {
        return Main.masses.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView listItemView = null;
        if (convertView==null){
            listItemView = new ListItemView();
            convertView = listContainer.inflate(R.layout.griditem, null);
            listItemView.name = (TextView) convertView.findViewById(R.id.pItemTextMass);
            listItemView.num = (TextView) convertView.findViewById(R.id.pItemTextCount);
//            listItemView.per = (TextView) convertView.findViewById(R.id.pMassPercents);
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }
        Map<String, Object> map = Main.masses.get(position);
        listItemView.name.setText((String) map.get("name"));
        System.out.println(map.get("name"));
        listItemView.num.setText((String) map.get("num"));
        System.out.println(map.get("num"));
//        listItemView.per.setText((String) map.get("percents"));
//        System.out.println(map.get("percents"));
        return convertView;
    }
}
