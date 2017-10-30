package ren.test.swipemenuerefreshview;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.listview);
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        final Handler handler = new Handler();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(i + "条数据");
        }
        final MyAdapter ad = new MyAdapter(list, this);
        listView.setAdapter(ad);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, ad.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
