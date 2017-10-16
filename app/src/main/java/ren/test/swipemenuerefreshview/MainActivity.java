package ren.test.swipemenuerefreshview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ren.test.swipemenurefreshview.widget.SwipeMenuRefreshView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.listview);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(i + "条数据");
        }
        MyAdapter ad = new MyAdapter(list, this);
        listView.setAdapter(ad);
        SwipeMenuRefreshView refreshLayout = (SwipeMenuRefreshView) findViewById(R.id.refresh);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("rq", "");
            }
        });
    }
}
