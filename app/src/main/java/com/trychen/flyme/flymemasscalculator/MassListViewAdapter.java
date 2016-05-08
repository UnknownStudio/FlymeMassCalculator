package com.trychen.flyme.flymemasscalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by trychen on 16/4/24.
 */
public class MassListViewAdapter extends BaseAdapter{
    private LayoutInflater listContainer;
    private Context context;
    private List<View> alls = new ArrayList<>();
    public MassListViewAdapter(Context context) {
        listContainer = LayoutInflater.from(context);
        this.context= context;
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
            Map<String, Object> map = Main.masses.get(position);
            final String name = (String) map.get("name");
            final String num = (String) map.get("num");
            final String percent = (String) map.get("percents");
            final String weigh = (String) map.get("weigh");
            listItemView.name.setText(name);
            listItemView.num.setText(num);

            final ListItemView finalListItemView = listItemView;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Main.popupWindows.changeMass(name + " " + num,percent + " " + weigh,true);
                }
            });
//            listItemView.per = (TextView) convertView.findViewById(R.id.pMassPercents);
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

//        listItemView.per.setText((String) map.get("percents"));
//        System.out.println(map.get("percents"));
        return convertView;
    }
}
