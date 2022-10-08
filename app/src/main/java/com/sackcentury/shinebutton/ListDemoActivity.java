package com.sackcentury.shinebutton;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.ArrayList;
import java.util.List;

public class ListDemoActivity extends Activity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_demo);
        listView = findViewById(R.id.list);
        listView.setAdapter(new ListAdapter());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }

    List<Data> dataList = new ArrayList<>();


    class ListAdapter extends BaseAdapter {
        public ListAdapter() {
            for (int i = 0; i < 20; i++) {
                dataList.add(new Data());
            }
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(ListDemoActivity.this).inflate(R.layout.list_item, null);
            }
            ShineButton button = view.findViewById(R.id.po_image);
            TextView textView = view.findViewById(R.id.text_item_id);
            textView.setText("ShineButton Position " + i);
            button.setChecked(dataList.get(i).checked);
            button.setOnCheckStateChangeListener((view1, checked) -> dataList.get(i).checked = checked);
            return view;
        }
    }

    class Data {
        public int position;
        public boolean checked;
    }
}
