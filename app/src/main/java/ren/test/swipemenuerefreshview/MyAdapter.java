package ren.test.swipemenuerefreshview;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * Created by Administrator on 2017/7/28
 */

public class MyAdapter extends BaseAdapter {
    private List<String> list;
    private LayoutInflater inflater;

    public MyAdapter(List<String> list, Context context) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item, null);
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        TextView top = (TextView) convertView.findViewById(R.id.top);
        final View finalConvertView = convertView;
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = getItem(position);
                list.remove(position);
                list.add(0, a);
                notifyDataSetChanged();
                Toast.makeText(finalConvertView.getContext(), "top", Toast.LENGTH_SHORT).show();
            }
        });
        final TextView noread = (TextView) convertView.findViewById(R.id.noread);
        noread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(finalConvertView.getContext(), "将 " + getItem(position) + " 标记为未读", Toast.LENGTH_SHORT).show();
            }
        });
        TextView delete = (TextView) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        textView.setText(getItem(position));
        return convertView;
    }
}
